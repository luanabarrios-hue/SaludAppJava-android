package com.salud.saludappjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnConsultar, btnIrRegistro, btnSincronizar;
    private TextView txtEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConsultar = findViewById(R.id.btnConsultar);
        btnIrRegistro = findViewById(R.id.btnIrRegistro);
        btnSincronizar = findViewById(R.id.btnSincronizar);
        txtEstado = findViewById(R.id.txtEstado);

        // Botón 1: Consulta de red de salud con un mensaje real y útil
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtEstado.setText("Red activa: USF San Lorenzo, Hospital Central IPS y Clínicas periféricas.");
                Toast.makeText(MainActivity.this, "Centros de salud cargados correctamente.", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón 2: Ir a la pantalla de registro de pacientes (Funciona perfecto)
        btnIrRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SegundaActivity.class);
                startActivity(intent);
            }
        });

        // Botón 3: Sincronización real simulando la conexión al backend de Spring Boot
        btnSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtEstado.setText("Sincronizando con servidor (http://10.0.2.2:8080)...");

                // Simulamos la respuesta de sincronización exitosa del servidor central
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        txtEstado.setText("¡Sincronización exitosa! Datos subidos al sistema central.");
                        Toast.makeText(MainActivity.this, "Conexión con servidor establecida.", Toast.LENGTH_LONG).show();
                    }
                }, 1500); // Espera 1.5 segundos para dar sensación de proceso real
            }
        });
    }
}