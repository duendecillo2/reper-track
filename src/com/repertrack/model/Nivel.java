package com.repertrack.model;

/** Niveles de experiencia del músico (enum para tipar el dominio). */
public enum Nivel {
    PRINCIPIANTE("principiante"),
    INTERMEDIO("intermedio"),
    AVANZADO("avanzado");

    private final String valor;

    Nivel(String valor) { this.valor = valor; }

    public String getValor() { return valor; }

    public static Nivel desde(String s) {
        if (s == null) return INTERMEDIO;
        for (Nivel n : values()) if (n.valor.equalsIgnoreCase(s.trim())) return n;
        return INTERMEDIO;
    }

    @Override public String toString() { return valor; }
}