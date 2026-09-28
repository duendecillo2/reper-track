package com.repertrack.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fábrica de conexiones JDBC a MySQL 8. El prototipo corre en localhost:3306,
 * base "repertrack". Si el servidor no está disponible, el DAO opera en
 * memoria para que el prototipo siga siendo operacional (Kendall & Kendall, 2011).
 */
public final class ConexionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/repertrack?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    private ConexionDB() {}

    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
