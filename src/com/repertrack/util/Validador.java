package com.repertrack.util;

import com.repertrack.excepcion.LongitudExcedidaException;
import com.repertrack.excepcion.RangoInvalidoException;

/**
 * Validador centralizado. Lanza las excepciones checked del paquete
 * com.repertrack.excepcion cuando una regla de negocio no se cumple.
 */
public final class Validador {

    private Validador() {}

    public static void exigirNoVacio(String campo, String valor) throws LongitudExcedidaException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new LongitudExcedidaException("El campo '" + campo + "' no puede estar vacio.");
        }
    }

    public static void exigirLongitudMaxima(String campo, String valor, int max) throws LongitudExcedidaException {
        if (valor == null) return;
        if (valor.length() > max) {
            throw new LongitudExcedidaException(
                "El campo '" + campo + "' no puede superar los " + max + " caracteres.");
        }
    }

    public static void exigirRangoEntero(String campo, int valor, int min, int max) throws RangoInvalidoException {
        if (valor < min || valor > max) {
            throw new RangoInvalidoException(
                "El campo '" + campo + "' debe estar entre " + min + " y " + max + ".");
        }
    }
}