package com.repertrack.excepcion;

/** Elemento de estudio no encontrado por id. */
public class ElementoInexistenteException extends Exception {
    public ElementoInexistenteException(String mensaje) { super(mensaje); }
}