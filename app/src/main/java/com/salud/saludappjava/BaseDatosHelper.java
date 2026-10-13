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
}