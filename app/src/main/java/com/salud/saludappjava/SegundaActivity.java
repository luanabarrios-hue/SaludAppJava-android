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
    private Button btnSeleccionarEspecialidad, btnSeleccionarDepartamento, btnSeleccionarHospital, btnVerDisponibilidad, btnVerCitasAdmin, btnVolver;
    private BaseDatosHelper dbHelper;

    private String especialidadElegida = "";
    private String departamentoElegido = "";
    private String hospitalElegido = "";

    // Los 17 departamentos oficiales de Paraguay + Capital (Asunción)
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

        inputCedula = findViewById(R.id.inputCedula);
        inputNombre = findViewById(R.id.inputNombre);
        inputTelefono = findViewById(R.id.inputTelefono);

        btnSeleccionarEspecialidad = findViewById(R.id.btnSeleccionarEspecialidad);
        btnSeleccionarDepartamento = findViewById(R.id.btnSeleccionarDepartamento);
        btnSeleccionarHospital = findViewById(R.id.btnSeleccionarHospital);
        btnVerDisponibilidad = findViewById(R.id.btnVerDisponibilidad);
        btnVerCitasAdmin = findViewById(R.id.btnVerCitasAdmin);
        btnVolver = findViewById(R.id.btnVolver);

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

        // Ventana informativa que muestra qué hospitales SÍ tienen y cuáles NO tienen la especialidad
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

            StringBuilder sb = new StringBuilder();
            sb.append("📋 DISPONIBILIDAD PARA: ").append(especialidadElegida.toUpperCase()).append("\n");
            sb.append("📍 Departamento: ").append(departamentoElegido).append("\n\n");

            final String[] listaNombresHosp = new String[estadoHospitales.size()];
            for (int i = 0; i < estadoHospitales.size(); i++) {
                String hosp = estadoHospitales.get(i)[0];
                String tiene = estadoHospitales.get(i)[1];
                listaNombresHosp[i] = hosp;

                if (tiene.equals("SI")) {
                    sb.append("🟢 [SÍ TIENE] ").append(hosp).append("\n");
                } else {
                    sb.append("🔴 [NO TIENE] ").append(hosp).append("\n");
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("🏥 Estado en " + departamentoElegido);
            builder.setMessage(sb.toString());
            builder.setItems(listaNombresHosp, (dialog, which) -> {
                String elegido = listaNombresHosp[which];
                String nombreLimpio = elegido.split("\\(")[0].trim();
                boolean tieneEsp = dbHelper.hospitalTieneEspecialidad(nombreLimpio, especialidadElegida);

                if (!tieneEsp) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("⚠️ Especialidad No Disponible");
                    alert.setMessage("El centro médico seleccionado NO cuenta con " + especialidadElegida + ".\n\nPor favor elija un hospital marcado con 🟢.");
                    alert.setPositiveButton("Entendido", null);
                    alert.show();
                } else {
                    hospitalElegido = elegido;
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
            Toast.makeText(this, "¡Cita confirmada en " + hospital + "!", Toast.LENGTH_LONG).show();
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
}