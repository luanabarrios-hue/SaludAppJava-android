package com.salud.saludappjava;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class BaseDatosHelper extends SQLiteOpenHelper {

    private static final String NOMBRE_DB = "salud_local.db";
    private static final int VERSION_DB = 16; // Versión actualizada

    public BaseDatosHelper(Context context) {
        super(context, NOMBRE_DB, null, VERSION_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS solicitudes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "cedula TEXT, " +
                "nombre TEXT, " +
                "telefono TEXT, " +
                "departamento TEXT, " +
                "hospital TEXT, " +
                "especialidad TEXT, " +
                "doctor TEXT, " +
                "fecha TEXT, " +
                "hora TEXT);");

        db.execSQL("CREATE TABLE IF NOT EXISTS establecimientos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "departamento TEXT, " +
                "nombre_hospital TEXT, " +
                "institucion TEXT, " +
                "especialidades_permitidas TEXT);");

        // 1. Hospitales principales y de referencia nacional con alta variedad
        insertarEstablecimiento(db, "Central", "Hospital General de San Lorenzo (Calleí)", "MSPBS", "Pediatria, Clinica, Traumatologia, Ginecologia, Odontologia, Dermatologia, Psicologia, Cardiologia");
        insertarEstablecimiento(db, "Central", "Hospital de Especialidades Quirúrgicas Ingavi", "IPS", "Traumatologia, Clinica, Cirugia, Cardiologia, Urologia");
        insertarEstablecimiento(db, "Capital (Asunción)", "Hospital Central del IPS", "IPS", "Pediatria, Psicologia, Clinica, Traumatologia, Cardiologia, Neurologia, Oftalmologia, Urologia");
        insertarEstablecimiento(db, "Capital (Asunción)", "Hospital de Clínicas (UNA)", "MSPBS / UNA", "Pediatria, Psicologia, Clinica, Traumatologia, Cardiologia, Oftalmologia, Psiquiatria");

        // 2. Cobertura exacta y realista para los 17 Departamentos + Asunción
        // CONCEPCIÓN
        insertarEstablecimiento(db, "Concepción", "Hospital Regional de Concepción", "MSPBS", "Pediatria, Clinica, Traumatologia, Ginecologia, Cirugia");
        insertarEstablecimiento(db, "Concepción", "Puesto Sanitario IPS Concepción", "IPS", "Clinica, Pediatria");

        // SAN PEDRO (Ejemplo donde se evalúa qué hay y qué no hay)
        insertarEstablecimiento(db, "San Pedro", "Hospital General de Santa Rosa del Aguaray", "MSPBS", "Pediatria, Clinica, Traumatologia, Ginecologia, Cirugia");
        insertarEstablecimiento(db, "San Pedro", "Hospital Regional de San Pedro de Ycuamandyyú", "MSPBS", "Pediatria, Clinica, Ginecologia");
        insertarEstablecimiento(db, "San Pedro", "Puesto Sanitario IPS San Pedro", "IPS", "Clinica, Odontologia");

        // CORDILLERA
        insertarEstablecimiento(db, "Cordillera", "Hospital Regional de Liniers / Caacupé", "MSPBS", "Pediatria, Clinica, Traumatologia, Ginecologia, Oftalmologia");
        insertarEstablecimiento(db, "Cordillera", "Puesto Sanitario IPS Caacupé", "IPS", "Clinica, Pediatria");

        // GUAIRÁ
        insertarEstablecimiento(db, "Guairá", "Hospital Regional de Villarrica", "MSPBS", "Pediatria, Clinica, Traumatologia, Ginecologia, Cardiologia");
        insertarEstablecimiento(db, "Guairá", "IPS Villarrica", "IPS", "Clinica, Pediatria, Odontologia");

        // CAAGUAZÚ
        insertarEstablecimiento(db, "Caaguazú", "Hospital Regional de Coronel Oviedo", "MSPBS", "Pediatria, Clinica, Traumatologia, Ginecologia, Cirugia");
        insertarEstablecimiento(db, "Caaguazú", "Hospital Distrital de Caaguazú", "MSPBS", "Pediatria, Clinica, Ginecologia");
        insertarEstablecimiento(db, "Caaguazú", "IPS Coronel Oviedo", "IPS", "Clinica, Pediatria, Traumatologia");

        // CAAZAPÁ
        insertarEstablecimiento(db, "Caazapá", "Hospital Regional de Caazapá", "MSPBS", "Pediatria, Clinica, Ginecologia, Traumatologia");
        insertarEstablecimiento(db, "Caazapá", "IPS Caazapá", "IPS", "Clinica, Odontologia");

        // ITAPÚA
        insertarEstablecimiento(db, "Itapúa", "Hospital Regional de Encarnación", "MSPBS", "Pediatria, Clinica, Traumatologia, Ginecologia, Psicologia, Oftalmologia, Cardiologia");
        insertarEstablecimiento(db, "Itapúa", "IPS Encarnación", "IPS", "Clinica, Pediatria, Traumatologia, Cirugia");

        // MISIONES
        insertarEstablecimiento(db, "Misiones", "Hospital Regional de San Juan Bautista", "MSPBS", "Pediatria, Clinica, Traumatologia, Ginecologia");
        insertarEstablecimiento(db, "Misiones", "IPS San Juan Bautista", "IPS", "Clinica, Pediatria");

        // PARAGUARÍ
        insertarEstablecimiento(db, "Paraguarí", "Hospital Regional de Paraguarí", "MSPBS", "Pediatria, Clinica, Traumatologia, Ginecologia");
        insertarEstablecimiento(db, "Paraguarí", "IPS Paraguarí", "IPS", "Clinica, Pediatria");

        // ALTO PARANÁ
        insertarEstablecimiento(db, "Alto Paraná", "Hospital Regional de Ciudad del Este", "MSPBS", "Pediatria, Clinica, Traumatologia, Ginecologia, Psicologia, Cardiologia, Oftalmologia");
        insertarEstablecimiento(db, "Alto Paraná", "Hospital Subcentral IPS Ciudad del Este", "IPS", "Clinica, Pediatria, Traumatologia, Cirugia, Urologia");

        // ÑEEMBUCÚ
        insertarEstablecimiento(db, "Ñeembucú", "Hospital Regional de Pilar", "MSPBS", "Pediatria, Clinica, Traumatologia, Ginecologia");
        insertarEstablecimiento(db, "Ñeembucú", "IPS Pilar", "IPS", "Clinica, Pediatria");

        // AMAMBAY
        insertarEstablecimiento(db, "Amambay", "Hospital Regional de Pedro Juan Caballero", "MSPBS", "Pediatria, Clinica, Traumatologia, Ginecologia, Cirugia");
        insertarEstablecimiento(db, "Amambay", "IPS Pedro Juan Caballero", "IPS", "Clinica, Pediatria, Traumatologia");

        // CANINDEYÚ
        insertarEstablecimiento(db, "Canindeyú", "Hospital Regional de Salto del Guairá", "MSPBS", "Pediatria, Clinica, Traumatologia, Ginecologia");
        insertarEstablecimiento(db, "Canindeyú", "IPS Salto del Guairá", "IPS", "Clinica, Pediatria");

        // PRESIDENTE HAYES
        insertarEstablecimiento(db, "Presidente Hayes", "Hospital Regional de Villa Hayes", "MSPBS", "Pediatria, Clinica, Traumatologia, Ginecologia");
        insertarEstablecimiento(db, "Presidente Hayes", "IPS Villa Hayes", "IPS", "Clinica, Pediatria");

        // BOQUERÓN
        insertarEstablecimiento(db, "Boquerón", "Hospital Regional de Filadelfia", "MSPBS", "Pediatria, Clinica, Traumatologia, Ginecologia");
        insertarEstablecimiento(db, "Boquerón", "IPS Filadelfia", "IPS", "Clinica, Pediatria");

        // ALTO PARAGUAY
        insertarEstablecimiento(db, "Alto Paraguay", "Hospital Regional de Fuerte Olimpo", "MSPBS", "Pediatria, Clinica, Ginecologia");
        insertarEstablecimiento(db, "Alto Paraguay", "Puesto Sanitario IPS Fuerte Olimpo", "IPS", "Clinica");
    }

    private void insertarEstablecimiento(SQLiteDatabase db, String depto, String hosp, String inst, String espec) {
        ContentValues cv = new ContentValues();
        cv.put("departamento", depto);
        cv.put("nombre_hospital", hosp);
        cv.put("institucion", inst);
        cv.put("especialidades_permitidas", espec);
        db.insert("establecimientos", null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS solicitudes;");
        db.execSQL("DROP TABLE IF EXISTS establecimientos;");
        onCreate(db);
    }

    // Devuelve el estado detallado (SI / NO) de los hospitales del departamento ante la especialidad buscada
    public List<String[]> obtenerEstadoHospitalesPorEspecialidad(String depto, String especialidadBuscada) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nombre_hospital, institucion, especialidades_permitidas FROM establecimientos WHERE departamento = ?", new String[]{depto});
        List<String[]> listaResultado = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String nombreHosp = cursor.getString(0) + " (" + cursor.getString(1) + ")";
                String especialidades = cursor.getString(2).toLowerCase();
                boolean tiene = especialidades.contains(especialidadBuscada.toLowerCase().trim());
                listaResultado.add(new String[]{nombreHosp, tiene ? "SI" : "NO"});
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            listaResultado.add(new String[]{"Hospital Regional de " + depto + " (MSPBS)", "SI"});
            listaResultado.add(new String[]{"Puesto Sanitario IPS " + depto + " (IPS)", "SI"});
        }
        db.close();
        return listaResultado;
    }

    public boolean hospitalTieneEspecialidad(String nombreHospitalLimpio, String especialidadBuscada) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT especialidades_permitidas FROM establecimientos WHERE nombre_hospital LIKE ?",
                new String[]{"%" + nombreHospitalLimpio + "%"});
        boolean tiene = false;
        if (cursor != null && cursor.moveToFirst()) {
            String especialidades = cursor.getString(0).toLowerCase();
            if (especialidades.contains(especialidadBuscada.toLowerCase().trim())) {
                tiene = true;
            }
        } else {
            tiene = true;
        }
        if (cursor != null) cursor.close();
        db.close();
        return tiene;
    }

    public boolean estaHorarioOcupado(String hospital, String especialidad, String fecha, String hora) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM solicitudes WHERE hospital = ? AND especialidad = ? AND fecha = ? AND hora = ?",
                new String[]{hospital, especialidad, fecha, hora});
        boolean ocupado = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return ocupado;
    }

    public boolean guardarCita(String cedula, String nombre, String telefono, String depto, String hospital, String especialidad, String doctor, String fecha, String hora) {
        if (estaHorarioOcupado(hospital, especialidad, fecha, hora)) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("cedula", cedula);
        cv.put("nombre", nombre);
        cv.put("telefono", telefono);
        cv.put("departamento", depto);
        cv.put("hospital", hospital);
        cv.put("especialidad", especialidad);
        cv.put("doctor", doctor);
        cv.put("fecha", fecha);
        cv.put("hora", hora);
        long res = db.insert("solicitudes", null, cv);
        db.close();
        return res != -1;
    }

    public String[] obtenerTodosLosHospitalesConCitas() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT hospital FROM solicitudes", null);
        List<String> lista = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                lista.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        db.close();
        if (lista.isEmpty()) return new String[]{"Sin citas agendadas aún"};
        return lista.toArray(new String[0]);
    }

    public String obtenerCitasPorHospital(String hospital) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM solicitudes WHERE hospital = ? ORDER BY fecha ASC, hora ASC", new String[]{hospital});
        StringBuilder sb = new StringBuilder();
        sb.append("🏥 CITAS AGENDADAS EN:\n").append(hospital).append("\n===================================\n\n");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                sb.append("📅 Fecha: ").append(cursor.getString(cursor.getColumnIndexOrThrow("fecha")))
                        .append(" - ⏰ Hora: ").append(cursor.getString(cursor.getColumnIndexOrThrow("hora")))
                        .append("\n🩺 Esp: ").append(cursor.getString(cursor.getColumnIndexOrThrow("especialidad")))
                        .append("\n👤 Paciente: ").append(cursor.getString(cursor.getColumnIndexOrThrow("nombre")))
                        .append(" (CI: ").append(cursor.getString(cursor.getColumnIndexOrThrow("cedula"))).append(")")
                        .append("\n📞 Contacto: ").append(cursor.getString(cursor.getColumnIndexOrThrow("telefono")))
                        .append("\n-----------------------------------\n");
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        db.close();
        return sb.toString();
    }
}