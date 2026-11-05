package com.salud.saludappjava;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TerceraActivity extends AppCompatActivity {

    private BaseDatosHelper dbHelper;
    private TextView txtListaCitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tercera);

        dbHelper = new BaseDatosHelper(this);
        txtListaCitas = findViewById(R.id.txtListaCitas);

        // Al entrar a la pantalla de administración, mostramos la selección de hospitales con citas
        mostrarSeleccionHospitalAdmin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cada vez que abras esta pantalla, recarga el selector de hospitales
        mostrarSeleccionHospitalAdmin();
    }

    private void mostrarSeleccionHospitalAdmin() {
        String[] hospitalesConCitas = dbHelper.obtenerTodosLosHospitalesConCitas();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🔍 Seleccione el Hospital a Auditar");
        builder.setCancelable(false);
        builder.setItems(hospitalesConCitas, (dialog, which) -> {
            String hospitalSeleccionado = hospitalesConCitas[which];
            if (hospitalSeleccionado.contains("Sin citas")) {
                txtListaCitas.setText("No hay citas agendadas en ningún hospital todavía.");
            } else {
                // Obtenemos las citas exclusivas de este hospital sin mezclar con otros
                String reporteCitas = dbHelper.obtenerCitasPorHospital(hospitalSeleccionado);
                txtListaCitas.setText(reporteCitas);
            }
        });

        builder.setNegativeButton("Volver", (dialog, which) -> finish());
        builder.show();
    }
}