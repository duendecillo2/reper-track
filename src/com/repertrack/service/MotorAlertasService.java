package com.repertrack.service;

import com.repertrack.model.SesionDePractica;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/** Motor de alertas de mantenimiento (CU012): avisa si un elemento lleva más de N días sin practicarse. */
public class MotorAlertasService {
    private final int umbralDias;

    public MotorAlertasService(int umbralDias) { this.umbralDias = umbralDias; }

    public List<String> calcularAlertas(List<SesionDePractica> sesiones, Set<Integer> elementosActivos) {
        Map<Integer, LocalDateTime> ultima = new HashMap<>();
        for (SesionDePractica s : sesiones) {
            ultima.merge(s.getElementoId(), s.getFechaRegistro(),
                    (a, b) -> a.isAfter(b) ? a : b);
        }
        List<String> alertas = new ArrayList<>();
        for (int id : elementosActivos) {
            LocalDateTime u = ultima.get(id);
            long dias = (u == null) ? Long.MAX_VALUE : ChronoUnit.DAYS.between(u, LocalDateTime.now());
            if (dias >= umbralDias) alertas.add("Elemento " + id + " lleva " + (u == null ? "nunca practicado" : dias + " días") + " sin práctica.");
        }
        return alertas;
    }
}
