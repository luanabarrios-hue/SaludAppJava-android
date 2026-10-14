package com.salud.saludappjava;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SegundaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 120, 60, 60);

        TextView texto = new TextView(this);
        texto.setText("Pantalla 2: Registro Offline\n\n" +
                "✅ Base de datos SQLite 'salud_local.db' creada con éxito.\n" +
                "✅ Tablas 'establecimientos' y 'solicitudes' listas.");
        texto.setTextSize(18);
        BaseDatosHelper dbHelper = new BaseDatosHelper(this);
        String hospitalesGuardados = dbHelper.obtenerEstablecimientosOffline();
        texto.append("\n\n" + hospitalesGuardados);
        Button btnVolver = new Button(this);
        btnVolver.setText("Volver a la Pantalla Principal");
        btnVolver.setOnClickListener(v -> finish());

        layout.addView(texto);

        // --- CREAR EL FORMULARIO OFFLINE ---
        android.widget.EditText inputHospital = new android.widget.EditText(this);
        inputHospital.setHint("Ej: IPS Central");

        android.widget.EditText inputPaciente = new android.widget.EditText(this);
        inputPaciente.setHint("Tu Nombre y Apellido");

        android.widget.EditText inputMotivo = new android.widget.EditText(this);
        inputMotivo.setHint("Motivo de consulta");

        android.widget.Button btnGuardarTurno = new android.widget.Button(this);
        btnGuardarTurno.setText("GUARDAR TURNO OFFLINE");

        // Agregar las cosas a la pantalla
        layout.addView(inputHospital);
        layout.addView(inputPaciente);
        layout.addView(inputMotivo);
        layout.addView(btnGuardarTurno);

        // Qué pasa al tocar el botón
        btnGuardarTurno.setOnClickListener(v -> {
            String hospital = inputHospital.getText().toString().trim();
            String paciente = inputPaciente.getText().toString().trim();
            String motivo = inputMotivo.getText().toString().trim();

            if (hospital.isEmpty() || paciente.isEmpty() || motivo.isEmpty()) {
                android.widget.Toast.makeText(this, "¡Error! Completá todos los campos", android.widget.Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.guardarSolicitudLocal(hospital, paciente, motivo);
                android.widget.Toast.makeText(this, "✅ Turno guardado en SQLite. Pendiente de sincronizar.", android.widget.Toast.LENGTH_LONG).show();
                inputHospital.setText("");
                inputPaciente.setText("");
                inputMotivo.setText("");
            }
        });
        layout.addView(btnVolver);

        setContentView(layout);
    }
}