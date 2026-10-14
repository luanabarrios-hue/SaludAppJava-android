package com.salud.saludappjava;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatosHelper extends SQLiteOpenHelper {

    public BaseDatosHelper(Context context) {
        super(context, "salud_local.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE establecimientos (" +
                "id INTEGER PRIMARY KEY, " +
                "nombre TEXT, " +
                "institucion TEXT, " +
                "localidad TEXT, " +
                "direccion TEXT, " +
                "telefono TEXT, " +
                "requisitos TEXT)");

        db.execSQL("CREATE TABLE solicitudes (" +
                "uuid TEXT PRIMARY KEY, " +
                "establecimiento_nombre TEXT, " +
                "tipo TEXT, " +
                "mensaje TEXT, " +
                "sincronizado INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS establecimientos");
        db.execSQL("DROP TABLE IF EXISTS solicitudes");
        onCreate(db);
    }

    // --- ESTA FUNCIÓN GUARDA LOS HOSPITALES EN MODO OFFLINE ---
    public void guardarEstablecimiento(int id, String nombre, String institucion, String localidad, String direccion, String telefono, String requisitos) {
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();

        values.put("id", id);
        values.put("nombre", nombre);
        values.put("institucion", institucion);
        values.put("localidad", localidad);
        values.put("direccion", direccion);
        values.put("telefono", telefono);
        values.put("requisitos", requisitos);

        // CONFLICT_REPLACE hace que si el hospital ya está guardado, lo actualice en vez de dar error
        db.insertWithOnConflict("establecimientos", null, values, android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }
    // --- ESTA FUNCIÓN LEE LOS HOSPITALES GUARDADOS PARA MOSTRARLOS SIN INTERNET ---
    public String obtenerEstablecimientosOffline() {
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();
        android.database.Cursor cursor = db.rawQuery("SELECT * FROM establecimientos", null);
        StringBuilder resultado = new StringBuilder();

        if (cursor.moveToFirst()) {
            do {
                resultado.append("🏥 ").append(cursor.getString(1)).append("\n"); // Nombre
                resultado.append("📍 ").append(cursor.getString(3)).append(" - ").append(cursor.getString(4)).append("\n"); // Localidad y Dirección
                resultado.append("📝 Requisitos: ").append(cursor.getString(6)).append("\n\n");
            } while (cursor.moveToNext());
        } else {
            resultado.append("Todavía no hay hospitales guardados en el celular.");
        }
        cursor.close();
        db.close();
        return resultado.toString();
    }
    // --- ESTA FUNCIÓN GUARDA LOS TURNOS NUEVOS EN MODO OFFLINE ---
    public void guardarSolicitudLocal(String hospital, String paciente, String motivo) {
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();

        // Generamos el UUID único que pide el profe para la semana 3
        values.put("uuid", java.util.UUID.randomUUID().toString());
        values.put("establecimiento_nombre", hospital);
        values.put("tipo", paciente); // Guardamos el nombre del paciente acá
        values.put("mensaje", motivo);
        values.put("sincronizado", 0); // 0 significa "Pendiente de enviar al servidor"

        db.insert("solicitudes", null, values);
        db.close();
    }
}