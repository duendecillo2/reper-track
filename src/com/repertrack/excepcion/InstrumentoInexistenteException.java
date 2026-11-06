package com.repertrack.excepcion;

/** Instrumento no encontrado por id. */
public class InstrumentoInexistenteException extends Exception {
    public InstrumentoInexistenteException(String mensaje) { super(mensaje); }
}