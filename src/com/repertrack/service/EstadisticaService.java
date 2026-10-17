package com.repertrack.service;

import com.repertrack.dao.SesionDAO;
import com.repertrack.dao.SesionDAOImpl;
import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.model.Estadistica;
import com.repertrack.model.SesionDePractica;

import java.util.List;
import java.util.Map;

/**
 * Servicio de estadísticas (CU009): acumulados, frecuencia y promedios.
 * Refactor TP3: además de las APIs históricas (Map<Integer,Integer>) expone
 * un agregado Estadistica inmutable.
 */
public class EstadisticaService {

    private final SesionDAO sesionDAO;

    public EstadisticaService() { this.sesionDAO = new SesionDAOImpl(); }
    public EstadisticaService(SesionDAO sesionDAO) { this.sesionDAO = sesionDAO; }

    // --- API compatible hacia atras (TP2) ---

    public Map<Integer, Long> totalPorInstrumento(List<SesionDePractica> sesiones) {
        java.util.Map<Integer, Long> m = new java.util.HashMap<>();
        for (SesionDePractica s : sesiones) m.merge(s.getInstrumentoId(), (long) s.getTiempoNetoMin(), Long::sum);
        return m;
    }

    public Map<Integer, Long> totalPorElemento(List<SesionDePractica> sesiones) {
        java.util.Map<Integer, Long> m = new java.util.HashMap<>();
        for (SesionDePractica s : sesiones) m.merge(s.getElementoId(), (long) s.getTiempoNetoMin(), Long::sum);
        return m;
    }

    public double promedioPorSesion(List<SesionDePractica> sesiones) {
        if (sesiones.isEmpty()) return 0.0;
        return sesiones.stream().mapToInt(SesionDePractica::getTiempoNetoMin).average().orElse(0.0);
    }

    public long frecuenciaSemanal(List<SesionDePractica> sesiones, int diasAtras) {
        var corte = java.time.LocalDateTime.now().minusDays(diasAtras);
        return sesiones.stream().filter(s -> s.getFechaRegistro().isAfter(corte)).count();
    }

    // --- API nueva (TP3) ---

    public Estadistica calcular() throws PersistenciaException {
        Estadistica e = new Estadistica();
        List<SesionDePractica> todas = sesionDAO.listar();
        e.setTotalSesiones(todas.size());
        for (Map.Entry<Integer, Long> kv : sesionDAO.acumuladoPorInstrumento().entrySet()) {
            e.agregarMinutosInstrumento(kv.getKey(), kv.getValue());
        }
        for (Map.Entry<Integer, Long> kv : sesionDAO.acumuladoPorElemento().entrySet()) {
            e.agregarMinutosElemento(kv.getKey(), kv.getValue());
        }
        e.setFrecuenciaSemanal(sesionDAO.frecuenciaUltimos7Dias());
        e.setPromedioMinutosPorSesion(sesionDAO.promedioTiempo());
        return e;
    }
}