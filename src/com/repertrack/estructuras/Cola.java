package com.repertrack.estructuras;

import java.util.NoSuchElementException;

/**
 * Cola (FIFO) genérica implementada a puntero.
 * Usada en MotorAlertasService para despachar alertas en orden de generación.
 */
public class Cola<T> {

    private static final class Nodo<E> {
        E valor;
        Nodo<E> siguiente;
        Nodo(E v, Nodo<E> s) { valor = v; siguiente = s; }
    }

    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tam;

    public Cola() { cabeza = null; cola = null; tam = 0; }
    public boolean estaVacia() { return tam == 0; }
    public int tam() { return tam; }

    public void encolar(T valor) {
        Nodo<T> n = new Nodo<>(valor, null);
        if (cola == null) {
            cabeza = n;
        } else {
            cola.siguiente = n;
        }
        cola = n;
        tam++;
    }

    public T desencolar() {
        if (cabeza == null) throw new NoSuchElementException("cola vacia");
        T v = cabeza.valor;
        cabeza = cabeza.siguiente;
        if (cabeza == null) cola = null;
        tam--;
        return v;
    }

    public T frente() {
        if (cabeza == null) throw new NoSuchElementException("cola vacia");
        return cabeza.valor;
    }
}