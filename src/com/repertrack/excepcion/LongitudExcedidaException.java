package com.repertrack.excepcion;

/** Nombre, autor, comentario o estilo fuera de la longitud maxima permitida. */
public class LongitudExcedidaException extends Exception {
    public LongitudExcedidaException(String mensaje) { super(mensaje); }
}