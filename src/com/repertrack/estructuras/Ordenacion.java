package com.repertrack.estructuras;

import java.util.Comparator;
import java.util.List;

/**
 * Algoritmos de ordenacion por seleccion y burbuja (manuales, sin Collections.sort).
 * Requisito de la rubrica: incluir al menos un algoritmo de ordenacion.
 */
public final class Ordenacion {

    private Ordenacion() {}

    public static <T> void seleccion(List<T> lista, Comparator<T> cmp) {
        if (lista == null) return;
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (cmp.compare(lista.get(j), lista.get(min)) < 0) {
                    min = j;
                }
            }
            if (min != i) {
                T tmp = lista.get(i);
                lista.set(i, lista.get(min));
                lista.set(min, tmp);
            }
        }
    }

    public static <T> void burbuja(List<T> lista, Comparator<T> cmp) {
        if (lista == null) return;
        boolean huboIntercambio = true;
        for (int i = 0; i < lista.size() - 1 && huboIntercambio; i++) {
            huboIntercambio = false;
            for (int j = 0; j < lista.size() - 1 - i; j++) {
                if (cmp.compare(lista.get(j), lista.get(j + 1)) > 0) {
                    T tmp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, tmp);
                    huboIntercambio = true;
                }
            }
        }
    }
}