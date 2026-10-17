package com.repertrack.service;

import com.repertrack.dao.SesionDAO;
import com.repertrack.dao.SesionDAOImpl;
import com.repertrack.estructuras.Cola;
import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.model.Alerta;
import com.repertrack.model.ElementoDeEstudio;
import com.repertrack.model.SesionDePractica;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Motor de alertas de mantenimiento (CU012): avisa si un elemento lleva más de N días sin practicarse.
 *
 * Refactor TP3:
 *   - Procesa alertas en una Cola FIFO implementada a puntero (requisito TP3).
 *   - Mantiene API antigua (calcularAlertas) para no romper consumidores existentes.
 */
public class MotorAlertasService {

    private final SesionDAO sesionDAO;
    private final com.repertrack.dao.ElementoDAO elementoDAO;
    private int umbralDias;

    public MotorAlertasService() {
        this.sesionDAO = new SesionDAOImpl();
        this.elementoDAO = new com.repertrack.dao.ElementoDAOImpl();
        this.umbralDias = 30;
    }

    public MotorAlertasService(int umbralDias) {
        this();
        this.umbralDias = umbralDias;
    }

    public MotorAlertasService(com.repertrack.dao.ElementoDAO elem, SesionDAO dao) {
        this.elementoDAO = elem;
        this.sesionDAO = dao;
        this.umbralDias = 30;
    }

    /** API antigua (TP2): devuelve Strings. */
    public List<String> calcularAlertas(List<SesionDePractica> sesiones, Set<Integer> elementosActivos) {
        Map<Integer, LocalDateTime> ultima = new java.util.HashMap<>();
        for (SesionDePractica s : sesiones) {
            ultima.merge(s.getElementoId(), s.getFechaRegistro(),
                    (a, b) -> a.isAfter(b) ? a : b);
        }
        List<String> alertas = new java.util.ArrayList<>();
        for (int id : elementosActivos) {
            LocalDateTime u = ultima.get(id);
            long dias = (u == null) ? Long.MAX_VALUE : ChronoUnit.DAYS.between(u, LocalDateTime.now());
            if (dias >= umbralDias) alertas.add("Elemento " + id + " lleva " + (u == null ? "nunca practicado" : dias + " dias") + " sin practica.");
        }
        return alertas;
    }

    /** API nueva (TP3): devuelve una Cola<Alerta> lista para drenar en UI. */
    public Cola<Alerta> calcular() throws PersistenciaException {
        Cola<Alerta> cola = new Cola<>();
        List<ElementoDeEstudio> elementos = elementoDAO.listarActivos();
        Map<Integer, LocalDate> ultimas = sesionDAO.ultimaFechaPorElemento();
        LocalDate hoy = LocalDate.now();
        for (ElementoDeEstudio e : elementos) {
            LocalDate ult = ultimas.get(e.getId());
            if (ult == null) {
                cola.encolar(new Alerta(e.getId(), e.getNombre(), null, Integer.MAX_VALUE, "sin sesiones registradas"));
            } else {
                long dias = ChronoUnit.DAYS.between(ult, hoy);
                if (dias > umbralDias) {
                    cola.encolar(new Alerta(e.getId(), e.getNombre(), ult, (int) dias, "supera umbral de " + umbralDias + " dias"));
                }
            }
        }
        return cola;
    }

    public int getUmbralDias() { return umbralDias; }
    public void setUmbralDias(int u) { this.umbralDias = u; }
}