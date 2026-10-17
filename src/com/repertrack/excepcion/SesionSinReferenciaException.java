package com.repertrack.excepcion;

/** Sesion sin instrumento o sin elemento (RN-01: sin sesiones huerfanas). */
public class SesionSinReferenciaException extends Exception {
    public SesionSinReferenciaException(String mensaje) { super(mensaje); }
}