package com.repertrack.excepcion;

/** Dificultad, BPM o tiempo fuera de rango (RN-02, RN-DIFICULTAD, RN-BPM). */
public class RangoInvalidoException extends Exception {
    public RangoInvalidoException(String mensaje) { super(mensaje); }
}