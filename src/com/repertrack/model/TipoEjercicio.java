package com.repertrack.model;

/** Tipos de ejercicio técnico (escala, arpegio, étude, patrón rítmico). */
public enum TipoEjercicio {
    ESCALA("escala"),
    ARPEGIO("arpegio"),
    ETUDE("etude"),
    PATRON_RITMICO("patron ritmico");

    private final String valor;

    TipoEjercicio(String valor) { this.valor = valor; }

    public String getValor() { return valor; }

    public static TipoEjercicio desde(String s) {
        if (s == null) return ESCALA;
        for (TipoEjercicio t : values()) if (t.valor.equalsIgnoreCase(s.trim())) return t;
        return ESCALA;
    }

    @Override public String toString() { return valor; }
}