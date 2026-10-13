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

        Button btnVolver = new Button(this);
        btnVolver.setText("Volver a la Pantalla Principal");
        btnVolver.setOnClickListener(v -> finish());

        layout.addView(texto);
        layout.addView(btnVolver);

        setContentView(layout);
    }
}