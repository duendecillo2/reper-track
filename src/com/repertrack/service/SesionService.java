package com.repertrack.service;

import com.repertrack.dao.SesionDAO;
import com.repertrack.model.SesionDePractica;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Controlador/Servicio de sesiones (CU006). Aplica las reglas de negocio
 * RN-01 a RN-05 antes de invocar al DAO. Equivale a la clase de control
 * <<ControladorSesion>> del diagrama de secuencia de análisis.
 */
public class SesionService {
    private final SesionDAO dao;

    public SesionService(SesionDAO dao) { this.dao = dao; }

    public int registrarSesion(int instrumentoId, int elementoId, int tiempoMin, String comentario) throws SQLException {
        if (instrumentoId <= 0 || elementoId <= 0)
            throw new IllegalArgumentException("Debe seleccionar un instrumento y un elemento de estudio antes de iniciar la sesión (RN-01).");
        if (tiempoMin <= 0)
            throw new IllegalArgumentException("El tiempo neto debe ser mayor a cero (RN-02).");
        LocalDateTime ahora = LocalDateTime.now();
        if (comentario != null && comentario.length() > 500)
            throw new IllegalArgumentException("El comentario no puede superar los 500 caracteres (RN-04).");
        SesionDePractica s = new SesionDePractica(0, instrumentoId, elementoId, ahora, tiempoMin, comentario);
        return dao.guardar(s); // transacción ACID dentro del DAO (RN-05)
    }
}
