package com.repertrack.util;

import com.repertrack.excepcion.PersistenciaException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fábrica de conexiones JDBC a MySQL 8.
 * Si el servidor no está disponible, los DAO operan en memoria y este método
 * de detección permite que el prototipo siga siendo operacional (Kendall & Kendall, 2011).
 *
 * Variables de entorno opcionales:
 *   REPER_DB_URL  (default: jdbc:mysql://localhost:3306/repertrack)
 *   REPER_DB_USER (default: root)
 *   REPER_DB_PASS (default: vacio)
 */
public final class ConexionDB {

    private static final String URL  = obtenerEnv("REPER_DB_URL",  "jdbc:mysql://localhost:3306/repertrack");
    private static final String USER = obtenerEnv("REPER_DB_USER", "root");
    private static final String PASS = obtenerEnv("REPER_DB_PASS", "");

    private static Boolean mysqlDisponible = null;

    private ConexionDB() {}

    private static String obtenerEnv(String k, String defecto) {
        String v = System.getenv(k);
        return v == null || v.isEmpty() ? defecto : v;
    }

    /** Devuelve true si MySQL responde a una conexion de prueba (cachea el resultado). */
    public static boolean mysqlDisponible() {
        if (mysqlDisponible != null) return mysqlDisponible;
        try (Connection c = DriverManager.getConnection(URL, USER, PASS)) {
            mysqlDisponible = c.isValid(2);
        } catch (SQLException e) {
            mysqlDisponible = false;
        }
        return mysqlDisponible;
    }

    public static Connection obtener() throws PersistenciaException {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo obtener conexion JDBC.", e);
        }
    }

    /** Alias con SQLException para mantener compatibilidad con codigo del TP2. */
    public static Connection obtenerRaw() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static String url()  { return URL; }
    public static String user() { return USER; }
}