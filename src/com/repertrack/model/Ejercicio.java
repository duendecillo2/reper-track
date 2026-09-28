package com.repertrack.model;

/** Especialización: ejercicio técnico (escala, arpegio, étude, patrón rítmico). */
public class Ejercicio extends ElementoDeEstudio {
    private String tipoEjercicio;
    private int bpmSugerido;

    public Ejercicio(int id, String nombre, int dificultad, int instrumentoId,
                     String tipoEjercicio, int bpmSugerido) {
        super(id, nombre, dificultad, instrumentoId);
        if (bpmSugerido < 20 || bpmSugerido > 300)
            throw new IllegalArgumentException("El BPM sugerido debe estar entre 20 y 300.");
        this.tipoEjercicio = tipoEjercicio;
        this.bpmSugerido = bpmSugerido;
    }

    @Override
    public double calcularProgreso(int minutosAcumulados) {
        int meta = getDificultad() * 60; // el ejercicio se domina con menos horas
        return Math.min(1.0, minutosAcumulados / (double) meta);
    }

    public String getTipoEjercicio() { return tipoEjercicio; }
    public int getBpmSugerido() { return bpmSugerido; }
}
