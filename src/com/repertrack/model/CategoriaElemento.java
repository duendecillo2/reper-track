package com.repertrack.model;

/** Categoría del ElementoDeEstudio: Cancion o Ejercicio. */
public enum CategoriaElemento {
    CANCION("Cancion"),
    EJERCICIO("Ejercicio");

    private final String valor;

    CategoriaElemento(String valor) { this.valor = valor; }

    public String getValor() { return valor; }

    public static CategoriaElemento desde(String s) {
        if (s == null) return CANCION;
        for (CategoriaElemento c : values()) if (c.valor.equalsIgnoreCase(s.trim())) return c;
        return CANCION;
    }

    @Override public String toString() { return valor; }
}