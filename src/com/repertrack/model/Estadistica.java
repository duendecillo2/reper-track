package com.repertrack.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Agregado de estadísticas calculado por EstadisticaService (CU009). */
public class Estadistica {

    private final Map<Integer, Long> minutosPorInstrumento = new LinkedHashMap<>();
    private final Map<Integer, Long> minutosPorElemento = new LinkedHashMap<>();
    private int frecuenciaSemanal;
    private double promedioMinutosPorSesion;
    private int totalSesiones;

    public Map<Integer, Long> getMinutosPorInstrumento() { return Collections.unmodifiableMap(minutosPorInstrumento); }

    public void agregarMinutosInstrumento(int instrumentoId, long minutos) {
        minutosPorInstrumento.merge(instrumentoId, minutos, Long::sum);
    }

    public Map<Integer, Long> getMinutosPorElemento() { return Collections.unmodifiableMap(minutosPorElemento); }

    public void agregarMinutosElemento(int elementoId, long minutos) {
        minutosPorElemento.merge(elementoId, minutos, Long::sum);
    }

    public int getFrecuenciaSemanal() { return frecuenciaSemanal; }
    public void setFrecuenciaSemanal(int n) { this.frecuenciaSemanal = n; }

    public double getPromedioMinutosPorSesion() { return promedioMinutosPorSesion; }
    public void setPromedioMinutosPorSesion(double p) { this.promedioMinutosPorSesion = p; }

    public int getTotalSesiones() { return totalSesiones; }
    public void setTotalSesiones(int t) { this.totalSesiones = t; }

    public boolean estaVacia() { return totalSesiones == 0; }
}