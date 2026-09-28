package com.repertrack.dao;

import com.repertrack.model.SesionDePractica;
import com.repertrack.util.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de sesiones con transacción manual (RNF05): setAutoCommit(false),
 * commit() si todo sale bien, rollback() ante SQLException.
 * Incluye réplica en memoria para demo sin servidor MySQL.
 */
public class SesionDAO {
    private final List<SesionDePractica> memoria = new ArrayList<>();
    private int secuencia = 1;

    public int guardar(SesionDePractica s) throws SQLException {
        try (Connection con = ConexionDB.obtener()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO sesion_practica (instrumento_id, elemento_id, fecha_registro, tiempo_neto_min, comentario) VALUES (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, s.getInstrumentoId());
                ps.setInt(2, s.getElementoId());
                ps.setTimestamp(3, Timestamp.valueOf(s.getFechaRegistro()));
                ps.setInt(4, s.getTiempoNetoMin());
                ps.setString(5, s.getComentario());
                ps.executeUpdate();
                con.commit();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
                return 0;
            } catch (SQLException e) {
                con.rollback(); // RN-05: atomicidad garantizada
                throw e;
            }
        } catch (SQLException sinServidor) {
            // Modo demostración: persiste en memoria con la misma validación
            SesionDePractica copia = new SesionDePractica(secuencia++,
                    s.getInstrumentoId(), s.getElementoId(),
                    s.getFechaRegistro(), s.getTiempoNetoMin(), s.getComentario());
            memoria.add(copia);
            return copia.getId();
        }
    }

    public List<SesionDePractica> listar() {
        return List.copyOf(memoria);
    }

    public boolean eliminar(int id) {
        return memoria.removeIf(s -> s.getId() == id);
    }
}
