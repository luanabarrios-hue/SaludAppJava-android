package com.salud.saludappjava;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView txtResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Inicializar y crear la base de datos SQLite y sus tablas
        BaseDatosHelper dbHelper = new BaseDatosHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.close();

        // 2. Armar la pantalla principal
        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 120, 60, 60);

        TextView titulo = new TextView(this);
        titulo.setText("Servicios de Salud (MSPBS / IPS)");
        titulo.setTextSize(22);

        Button btnConsultar = new Button(this);
        btnConsultar.setText("1. Consultar JSON desde Spring Boot");
        btnConsultar.setOnClickListener(v -> consultarServidor());

        Button btnSegundaPantalla = new Button(this);
        btnSegundaPantalla.setText("2. Ir a Segunda Pantalla (SQLite)");
        btnSegundaPantalla.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SegundaActivity.class);
            startActivity(intent);
        });

        txtResultado = new TextView(this);
        txtResultado.setText("\nEstado SQLite: Base de datos creada.\n\nPresioná el botón 1 para pedir el JSON al servidor Spring Boot...");
        txtResultado.setTextSize(15);

        layout.addView(titulo);
        layout.addView(btnConsultar);
        layout.addView(btnSegundaPantalla);
        layout.addView(txtResultado);
        scrollView.addView(layout);

        setContentView(scrollView);
    }

    private void consultarServidor() {
        txtResultado.setText("\nConectando con Spring Boot...");
        new Thread(() -> {
            try {
                // 10.0.2.2 es la dirección especial con la que el emulador de Android ve al localhost de tu PC
                URL url = new URL("http://127.0.0.1:8080/api/establecimientos");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder respuesta = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) {
                    respuesta.append(linea);
                }
                reader.close();
                String jsonString = respuesta.toString();
                org.json.JSONArray jsonArray = new org.json.JSONArray(jsonString);
                BaseDatosHelper dbHelper = new BaseDatosHelper(MainActivity.this);

                // Recorremos el JSON y guardamos cada hospital en SQLite
                for (int i = 0; i < jsonArray.length(); i++) {
                    org.json.JSONObject hospital = jsonArray.getJSONObject(i);
                    dbHelper.guardarEstablecimiento(
                            hospital.getInt("id"),
                            hospital.getString("nombre"),
                            hospital.getString("institucion"),
                            hospital.getString("localidad"),
                            hospital.getString("direccion"),
                            hospital.getString("telefono"),
                            hospital.getString("requisitos")
                    );
                }

                runOnUiThread(() -> txtResultado.setText("¡Datos descargados y guardados en la base de datos offline (SQLite) con éxito!\n\nYa podés apagar el Wi-Fi."));

            } catch (Exception e) {
                runOnUiThread(() -> txtResultado.setText("\n❌ Error al conectar: " + e.getMessage() +
                        "\n\nVerificá que IntelliJ IDEA siga corriendo el servidor."));
            }
        }).start();
    }
}