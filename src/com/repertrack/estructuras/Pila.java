package com.repertrack.estructuras;

import java.util.NoSuchElementException;

/**
 * Pila (LIFO) genérica implementada a puntero.
 * Usada en ElementoService para soportar el "deshacer último alta".
 */
public class Pila<T> {

    private static final class Nodo<E> {
        E valor;
        Nodo<E> siguiente;
        Nodo(E v, Nodo<E> s) { valor = v; siguiente = s; }
    }

    private Nodo<T> tope;
    private int tam;

    public Pila() { tope = null; tam = 0; }
    public boolean estaVacia() { return tam == 0; }
    public int tam() { return tam; }

    public void apilar(T valor) { tope = new Nodo<>(valor, tope); tam++; }

    public T desapilar() {
        if (tope == null) throw new NoSuchElementException("pila vacia");
        T v = tope.valor;
        tope = tope.siguiente;
        tam--;
        return v;
    }

    public T tope() {
        if (tope == null) throw new NoSuchElementException("pila vacia");
        return tope.valor;
    }
}