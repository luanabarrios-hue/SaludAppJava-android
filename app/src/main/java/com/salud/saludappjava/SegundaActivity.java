package com.salud.saludappjava;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class SegundaActivity extends AppCompatActivity {

    private EditText inputCedula, inputNombre, inputTelefono;
    private Button btnSeleccionarEspecialidad, btnSeleccionarDepartamento, btnSeleccionarHospital,
            btnVerDisponibilidad, btnVerCitasAdmin, btnVolver, btnSincronizarBackend, btnDescargarBackend;
    private BaseDatosHelper dbHelper;

    private String especialidadElegida = "";
    private String departamentoElegido = "";
    private String hospitalElegido = "";

    // URL de ngrok configurada directamente para tu proyecto
    private static final String NGROK_URL = "https://conch-subscribe-retiring.ngrok-free.dev";

    private final String[] departamentosParaguay = {
            "Capital (Asunción)", "Concepción", "San Pedro", "Cordillera", "Guairá",
            "Caaguazú", "Caazapá", "Itapúa", "Misiones", "Paraguarí", "Alto Paraná",
            "Central", "Ñeembucú", "Amambay", "Canindeyú", "Presidente Hayes",
            "Boquerón", "Alto Paraguay"
    };

    private final String[] especialidadesCat = {
            "Pediatria", "Clinica", "Traumatologia", "Psicologia", "Ginecologia",
            "Cardiologia", "Neurologia", "Oftalmologia", "Dermatologia", "Odontologia", "Urologia", "Psiquiatria"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segunda);

        dbHelper = new BaseDatosHelper(this);

        descargarCitasDelServidor(); // NUEVO: trae las citas del servidor al abrir la pantalla

        inputCedula = findViewById(R.id.inputCedula);
        inputNombre = findViewById(R.id.inputNombre);
        inputTelefono = findViewById(R.id.inputTelefono);

        btnSeleccionarEspecialidad = findViewById(R.id.btnSeleccionarEspecialidad);
        btnSeleccionarDepartamento = findViewById(R.id.btnSeleccionarDepartamento);
        btnSeleccionarHospital = findViewById(R.id.btnSeleccionarHospital);
        btnVerDisponibilidad = findViewById(R.id.btnVerDisponibilidad);
        btnVerCitasAdmin = findViewById(R.id.btnVerCitasAdmin);
        btnVolver = findViewById(R.id.btnVolver);

        btnSincronizarBackend = findViewById(R.id.btnSincronizarBackend);
        btnDescargarBackend = findViewById(R.id.btnDescargarBackend);

        btnSeleccionarEspecialidad.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("🩺 Seleccione la Especialidad Requerida");
            builder.setItems(especialidadesCat, (dialog, which) -> {
                especialidadElegida = especialidadesCat[which];
                btnSeleccionarEspecialidad.setText("ESPECIALIDAD: " + especialidadElegida);
            });
            builder.show();
        });

        btnSeleccionarDepartamento.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("🇵🇾 Seleccione el Departamento de Paraguay");
            builder.setItems(departamentosParaguay, (dialog, which) -> {
                departamentoElegido = departamentosParaguay[which];
                hospitalElegido = "";
                btnSeleccionarDepartamento.setText("DEPTO: " + departamentoElegido);
                btnSeleccionarHospital.setText("SELECCIONAR HOSPITAL DE " + departamentoElegido.toUpperCase());
            });
            builder.show();
        });

        btnSeleccionarHospital.setOnClickListener(v -> {
            if (departamentoElegido.isEmpty()) {
                Toast.makeText(this, "Seleccione primero un Departamento.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (especialidadElegida.isEmpty()) {
                Toast.makeText(this, "Seleccione primero la Especialidad.", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String[]> estadoHospitales = dbHelper.obtenerEstadoHospitalesPorEspecialidad(departamentoElegido, especialidadElegida);

            final String[] listaNombresHosp = new String[estadoHospitales.size()];
            for (int i = 0; i < estadoHospitales.size(); i++) {
                String hosp = estadoHospitales.get(i)[0];
                String tiene = estadoHospitales.get(i)[1];

                if (tiene.equals("SI")) {
                    listaNombresHosp[i] = "🟢 " + hosp + " (SÍ TIENE)";
                } else {
                    listaNombresHosp[i] = "🔴 " + hosp + " (NO TIENE)";
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("🏥 " + especialidadElegida + " en " + departamentoElegido);
            builder.setItems(listaNombresHosp, (dialog, which) -> {
                String hospOriginal = estadoHospitales.get(which)[0];
                String tieneEsp = estadoHospitales.get(which)[1];

                if (!tieneEsp.equals("SI")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("⚠️ Especialidad No Disponible");
                    alert.setMessage("El centro médico seleccionado NO cuenta con " + especialidadElegida + ".\n\nPor favor elija un hospital marcado con 🟢.");
                    alert.setPositiveButton("Entendido", null);
                    alert.show();
                } else {
                    hospitalElegido = hospOriginal;
                    btnSeleccionarHospital.setText("HOSPITAL: " + hospitalElegido);
                    Toast.makeText(this, "Hospital confirmado con especialista disponible.", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        });

        btnVerDisponibilidad.setOnClickListener(v -> {
            String cedula = inputCedula.getText().toString().trim();
            String nombre = inputNombre.getText().toString().trim();
            String telefono = inputTelefono.getText().toString().trim();

            if (cedula.isEmpty() || nombre.isEmpty() || telefono.isEmpty() || especialidadElegida.isEmpty() || departamentoElegido.isEmpty() || hospitalElegido.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los datos y seleccione un hospital válido.", Toast.LENGTH_LONG).show();
                return;
            }

            String fecha = "25/10/2026";
            String h1 = "08:00", h2 = "09:00", h3 = "10:00";

            boolean o1 = dbHelper.estaHorarioOcupado(hospitalElegido, especialidadElegida, fecha, h1);
            boolean o2 = dbHelper.estaHorarioOcupado(hospitalElegido, especialidadElegida, fecha, h2);
            boolean o3 = dbHelper.estaHorarioOcupado(hospitalElegido, especialidadElegida, fecha, h3);

            String cartelera = "🏥 Centro: " + hospitalElegido + "\n" +
                    "🩺 Especialidad: " + especialidadElegida + "\n" +
                    "📅 Fecha: " + fecha + "\n\n" +
                    "Horarios:\n" +
                    (o1 ? "🔴 " + h1 + " (Ocupado)" : "🟢 " + h1 + " (Disponible)") + "\n" +
                    (o2 ? "🔴 " + h2 + " (Ocupado)" : "🟢 " + h2 + " (Disponible)") + "\n" +
                    (o3 ? "🔴 " + h3 + " (Ocupado)" : "🟢 " + h3 + " (Disponible)");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("📅 Cartelera Médica en Vivo");
            builder.setMessage(cartelera);

            if (!o1) builder.setPositiveButton("Reservar " + h1, (d, w) -> procesarReserva(cedula, nombre, telefono, departamentoElegido, hospitalElegido, especialidadElegida, "Dr. Especialista", fecha, h1));
            if (!o2) builder.setNeutralButton("Reservar " + h2, (d, w) -> procesarReserva(cedula, nombre, telefono, departamentoElegido, hospitalElegido, especialidadElegida, "Dr. Especialista", fecha, h2));
            if (!o3) builder.setNegativeButton("Reservar " + h3, (d, w) -> procesarReserva(cedula, nombre, telefono, departamentoElegido, hospitalElegido, especialidadElegida, "Dr. Especialista", fecha, h3));

            builder.show();
        });

        if (btnSincronizarBackend != null) {
            btnSincronizarBackend.setOnClickListener(v -> {
                String cedula = inputCedula.getText().toString().trim();
                String nombre = inputNombre.getText().toString().trim();
                String telefono = inputTelefono.getText().toString().trim();

                if (cedula.isEmpty() || nombre.isEmpty() || telefono.isEmpty() || hospitalElegido.isEmpty() || especialidadElegida.isEmpty()) {
                    Toast.makeText(this, "Complete los datos y seleccione hospital/especialidad para sincronizar.", Toast.LENGTH_SHORT).show();
                    return;
                }

                enviarCitaAlServidor(cedula, nombre, telefono, departamentoElegido, hospitalElegido, especialidadElegida, "25/10/2026", "08:00");
            });
        }

        if (btnDescargarBackend != null) {
            btnDescargarBackend.setOnClickListener(v -> descargarCitasDelServidor());
        }

        btnVerCitasAdmin.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Acceso Administrativo");
            final EditText inputPin = new EditText(this);
            inputPin.setHint("Ingrese PIN (1234)");
            inputPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            builder.setView(inputPin);

            builder.setPositiveButton("Ingresar", (d, w) -> {
                if (inputPin.getText().toString().trim().equals("1234")) {
                    abrirSeleccionHospitalAdmin();
                } else {
                    Toast.makeText(this, "PIN incorrecto.", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancelar", null);
            builder.show();
        });

        btnVolver.setOnClickListener(v -> finish());
    }

    private void abrirSeleccionHospitalAdmin() {
        String[] hospitalesConCitas = dbHelper.obtenerTodosLosHospitalesConCitas();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🔍 Seleccione el Hospital a Auditar");
        builder.setItems(hospitalesConCitas, (dialog, which) -> {
            String hospitalSeleccionado = hospitalesConCitas[which];
            if (!hospitalSeleccionado.contains("Sin citas")) {
                String reporteCitas = dbHelper.obtenerCitasPorHospital(hospitalSeleccionado);
                AlertDialog.Builder rep = new AlertDialog.Builder(this);
                rep.setTitle("Reporte Administrativo");
                rep.setMessage(reporteCitas);
                rep.setPositiveButton("Cerrar", null);
                rep.show();
            }
        });
        builder.show();
    }

    private void procesarReserva(String cedula, String nombre, String telefono, String depto, String hospital, String especialidad, String doctor, String fecha, String hora) {
        boolean ok = dbHelper.guardarCita(cedula, nombre, telefono, depto, hospital, especialidad, doctor, fecha, hora);
        if (ok) {
            enviarCitaAlServidor(cedula, nombre, telefono, depto, hospital, especialidad, fecha, hora); // NUEVO: manda la cita al servidor
            Toast.makeText(this, "¡Cita confirmada localmente en " + hospital + "!", Toast.LENGTH_LONG).show();
            inputCedula.setText("");
            inputNombre.setText("");
            inputTelefono.setText("");
            especialidadElegida = "";
            departamentoElegido = "";
            hospitalElegido = "";
            btnSeleccionarEspecialidad.setText("4. ELEGIR ESPECIALIDAD MÉDICA");
            btnSeleccionarDepartamento.setText("5. ELEGIR DEPARTAMENTO (17 DEPTO DE PY)");
            btnSeleccionarHospital.setText("6. SELECCIONAR HOSPITAL DEL DEPARTAMENTO");
        } else {
            Toast.makeText(this, "Error: El horario ya fue ocupado.", Toast.LENGTH_LONG).show();
        }
    }

    private void enviarCitaAlServidor(String cedula, String nombre, String telefono, String departamento, String hospital, String especialidad, String fecha, String hora) {
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(NGROK_URL + "/api/citas/sincronizar");
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("ngrok-skip-browser-warning", "true"); // <-- Cabecera clave para ngrok
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);

                String jsonInputString = "{" +
                        "\"cedula\": \"" + cedula + "\"," +
                        "\"nombre\": \"" + nombre + "\"," +
                        "\"telefono\": \"" + telefono + "\"," +
                        "\"departamento\": \"" + departamento + "\"," +
                        "\"hospital\": \"" + hospital + "\"," +
                        "\"especialidad\": \"" + especialidad + "\"," +
                        "\"fecha\": \"" + fecha + "\"," +
                        "\"hora\": \"" + hora + "\"" +
                        "}";

                try (java.io.OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();

                java.io.BufferedReader br;
                if (responseCode >= 200 && responseCode < 300) {
                    br = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(), "utf-8"));
                } else {
                    br = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream(), "utf-8"));
                }

                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                br.close();
                conn.disconnect();

                runOnUiThread(() -> {
                    if (responseCode >= 200 && responseCode < 300) {
                        Toast.makeText(this, "🌐 " + response.toString(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "⚠️ Servidor respondió con código: " + responseCode, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                String errorReal = e.getMessage();
                runOnUiThread(() -> {
                    Toast.makeText(this, "❌ Error de conexión: " + errorReal, Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void descargarCitasDelServidor() {
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(NGROK_URL + "/api/citas/listar");
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("ngrok-skip-browser-warning", "true"); // <-- Cabecera clave para ngrok
                conn.setConnectTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(), "utf-8"));
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();
                    conn.disconnect();

                    org.json.JSONArray jsonArray = new org.json.JSONArray(content.toString());
                    int insertadas = 0;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        org.json.JSONObject obj = jsonArray.getJSONObject(i);
                        String cedula = obj.optString("cedula");
                        String nombre = obj.optString("nombre");
                        String telefono = obj.optString("telefono");
                        String departamento = obj.optString("departamento");
                        String hospital = obj.optString("hospital");
                        String especialidad = obj.optString("especialidad");
                        String doctor = obj.optString("doctor", "Dr. Especialista");
                        String fecha = obj.optString("fecha");
                        String hora = obj.optString("hora");

                        boolean ocupado = dbHelper.estaHorarioOcupado(hospital, especialidad, fecha, hora);
                        if (!ocupado) {
                            dbHelper.guardarCita(cedula, nombre, telefono, departamento, hospital, especialidad, doctor, fecha, hora);
                            insertadas++;
                        }
                    }

                    int finalInsertadas = insertadas;
                    runOnUiThread(() -> {
                        Toast.makeText(this, "📥 ¡Descarga exitosa! Se sincronizaron " + finalInsertadas + " citas nuevas a SQLite.", Toast.LENGTH_LONG).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "⚠️ El servidor no respondió correctamente al listar.", Toast.LENGTH_SHORT).show();
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                String errorReal = e.getMessage();
                runOnUiThread(() -> {
                    Toast.makeText(this, "❌ Error al descargar: " + errorReal, Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
}