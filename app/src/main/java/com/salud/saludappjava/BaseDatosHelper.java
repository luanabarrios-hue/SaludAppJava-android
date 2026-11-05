package com.salud.saludappjava;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class BaseDatosHelper extends SQLiteOpenHelper {

    private static final String NOMBRE_DB = "SaludApp.db";
    private static final int VERSION_DB = 1;

    public BaseDatosHelper(Context context) {
        super(context, NOMBRE_DB, null, VERSION_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabla de citas locales para funcionamiento offline
        db.execSQL("CREATE TABLE citas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "cedula TEXT, " +
                "nombre TEXT, " +
                "telefono TEXT, " +
                "departamento TEXT, " +
                "hospital TEXT, " +
                "especialidad TEXT, " +
                "doctor TEXT, " +
                "fecha TEXT, " +
                "hora TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS citas");
        onCreate(db);
    }

    // Guardar cita localmente asegurando que no se duplique el mismo horario en el mismo hospital y especialidad
    public boolean guardarCita(String cedula, String nombre, String telefono, String depto, String hospital, String especialidad, String doctor, String fecha, String hora) {
        if (estaHorarioOcupado(hospital, especialidad, fecha, hora)) {
            return false; // Horario ya ocupado
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cedula", cedula);
        values.put("nombre", nombre);
        values.put("telefono", telefono);
        values.put("departamento", depto);
        values.put("hospital", hospital);
        values.put("especialidad", especialidad);
        values.put("doctor", doctor);
        values.put("fecha", fecha);
        values.put("hora", hora);

        long resultado = db.insert("citas", null, values);
        return resultado != -1;
    }

    // Verificar si un horario ya está reservado (Evita duplicados)
    public boolean estaHorarioOcupado(String hospital, String especialidad, String fecha, String hora) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM citas WHERE hospital = ? AND especialidad = ? AND fecha = ? AND hora = ?",
                new String[]{hospital, especialidad, fecha, hora});
        boolean ocupado = cursor.getCount() > 0;
        cursor.close();
        return ocupado;
    }

    // Obtener estado de hospitales por departamento para la validación visual (🟢 / 🔴)
    public List<String[]> obtenerEstadoHospitalesPorEspecialidad(String departamento, String especialidad) {
        List<String[]> lista = new ArrayList<>();

        if (departamento.equals("Central")) {
            lista.add(new String[]{"Hospital General de San Lorenzo (Calleí) (MSPBS)", "SI"});
            lista.add(new String[]{"Hospital de Especialidades Quirúrgicas Ingavi (IPS)", "NO"});
        } else if (departamento.equals("Capital (Asunción)")) {
            lista.add(new String[]{"Hospital de Clínicas (UNA)", "SI"});
            lista.add(new String[]{"Hospital General Barrio Obrero (MSPBS)", "SI"});
            lista.add(new String[]{"Hospital Central del IPS", "SI"});
        } else {
            // Ejemplo genérico para los demás departamentos del país
            lista.add(new String[]{"Hospital Regional de " + departamento, "SI"});
            lista.add(new String[]{"Centro de Salud Distrital", "NO"});
        }
        return lista;
    }

    // Obtener lista de hospitales que tienen citas registradas para el panel de administración
    public String[] obtenerTodosLosHospitalesConCitas() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT hospital FROM citas", null);

        if (cursor.getCount() == 0) {
            cursor.close();
            return new String[]{"Sin citas registradas aún"};
        }

        String[] hospitales = new String[cursor.getCount()];
        int i = 0;
        while (cursor.moveToNext()) {
            hospitales[i] = cursor.getString(0);
            i++;
        }
        cursor.close();
        return hospitales;
    }

    // Obtener reporte de citas por hospital para el panel admin (PIN 1234)
    public String obtenerCitasPorHospital(String hospital) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT cedula, nombre, especialidad, fecha, hora FROM citas WHERE hospital = ?", new String[]{hospital});

        if (cursor.getCount() == 0) {
            cursor.close();
            return "No hay citas registradas para este centro.";
        }

        StringBuilder sb = new StringBuilder();
        while (cursor.moveToNext()) {
            sb.append("🆔 Cédula: ").append(cursor.getString(0)).append("\n")
                    .append("👤 Paciente: ").append(cursor.getString(1)).append("\n")
                    .append("🩺 Especialidad: ").append(cursor.getString(2)).append("\n")
                    .append("📅 Fecha/Hora: ").append(cursor.getString(3)).append(" - ").append(cursor.getString(4)).append("\n")
                    .append("---------------------------\n");
        }
        cursor.close();
        return sb.toString();
    }
}