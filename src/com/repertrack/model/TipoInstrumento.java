package com.repertrack.model;

/** Tipos de instrumento aceptados (enum cerrado). */
public enum TipoInstrumento {
    CUERDA("cuerda"),
    VIENTO("viento"),
    PERCUSION("percusion"),
    TECLADO("teclado"),
    OTRO("otro");

    private final String valor;

    TipoInstrumento(String valor) { this.valor = valor; }

    public String getValor() { return valor; }

    public static TipoInstrumento desde(String s) {
        if (s == null) return OTRO;
        for (TipoInstrumento t : values()) if (t.valor.equalsIgnoreCase(s.trim())) return t;
        return OTRO;
    }

    @Override public String toString() { return valor; }
}