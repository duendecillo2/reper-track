package com.repertrack.excepcion;

/** Error de persistencia (SQLException envuelta). */
public class PersistenciaException extends Exception {
    public PersistenciaException(String mensaje) { super(mensaje); }
    public PersistenciaException(String mensaje, Throwable causa) { super(mensaje, causa); }
}