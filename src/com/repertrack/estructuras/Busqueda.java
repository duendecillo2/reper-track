package com.repertrack.estructuras;

import java.util.List;
import java.util.function.Predicate;

/**
 * Algoritmos de búsqueda lineal genéricos.
 * Requisito de la rubrica: incluir al menos un algoritmo de busqueda.
 */
public final class Busqueda {

    private Busqueda() {}

    public static <T> T lineal(List<T> lista, Predicate<T> criterio) {
        if (lista == null) return null;
        for (T item : lista) {
            if (criterio.test(item)) return item;
        }
        return null;
    }

    public static <T> int linealIndice(List<T> lista, Predicate<T> criterio) {
        if (lista == null) return -1;
        for (int i = 0; i < lista.size(); i++) {
            if (criterio.test(lista.get(i))) return i;
        }
        return -1;
    }

    public static <T> int linealIndice(Iterable<T> iterable, Predicate<T> criterio) {
        if (iterable == null) return -1;
        int i = 0;
        for (T item : iterable) {
            if (criterio.test(item)) return i;
            i++;
        }
        return -1;
    }
}