package com.repertrack.service;

import com.repertrack.model.SesionDePractica;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Servicio de estadísticas (CU009): acumulados, frecuencia y promedios. */
public class EstadisticaService {

    public Map<Integer, Integer> totalPorInstrumento(List<SesionDePractica> sesiones) {
        Map<Integer, Integer> m = new HashMap<>();
        for (SesionDePractica s : sesiones) m.merge(s.getInstrumentoId(), s.getTiempoNetoMin(), Integer::sum);
        return m;
    }

    public Map<Integer, Integer> totalPorElemento(List<SesionDePractica> sesiones) {
        Map<Integer, Integer> m = new HashMap<>();
        for (SesionDePractica s : sesiones) m.merge(s.getElementoId(), s.getTiempoNetoMin(), Integer::sum);
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
}
