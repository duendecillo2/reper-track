package com.repertrack.excepcion;

/** Error de lectura o escritura de archivos de texto (reportes). */
public class ArchivoException extends Exception {
    public ArchivoException(String mensaje) { super(mensaje); }
    public ArchivoException(String mensaje, Throwable causa) { super(mensaje, causa); }
}
