package com.repertrack.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Lista enlazada simple genérica, implementada a puntero (sin librerías).
 * Se usa en SesionController para mantener el historial reciente de sesiones
 * y en SesionDAOImpl para almacenar sesiones en modo memoria.
 */
public class ListaEnlazadaSimple<T> implements Iterable<T> {

    private static final class Nodo<E> {
        E valor;
        Nodo<E> siguiente;
        Nodo(E v, Nodo<E> s) { valor = v; siguiente = s; }
    }

    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tam;

    public ListaEnlazadaSimple() { cabeza = null; cola = null; tam = 0; }

    public boolean estaVacia() { return tam == 0; }
    public int tam() { return tam; }

    public void agregarAlFinal(T valor) {
        Nodo<T> n = new Nodo<>(valor, null);
        if (cola == null) {
            cabeza = n;
        } else {
            cola.siguiente = n;
        }
        cola = n;
        tam++;
    }

    public void agregarAlInicio(T valor) {
        Nodo<T> n = new Nodo<>(valor, cabeza);
        if (cabeza == null) cola = n;
        cabeza = n;
        tam++;
    }

    public T obtener(int indice) {
        if (indice < 0 || indice >= tam) {
            throw new IndexOutOfBoundsException("indice=" + indice + " tam=" + tam);
        }
        Nodo<T> cur = cabeza;
        for (int i = 0; i < indice; i++) cur = cur.siguiente;
        return cur.valor;
    }

    public T quitarPrimero() {
        if (cabeza == null) throw new NoSuchElementException("lista vacia");
        T v = cabeza.valor;
        cabeza = cabeza.siguiente;
        if (cabeza == null) cola = null;
        tam--;
        return v;
    }

    public void limpiar() { cabeza = null; cola = null; tam = 0; }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Nodo<T> cur = cabeza;
            @Override public boolean hasNext() { return cur != null; }
            @Override public T next() {
                if (cur == null) throw new NoSuchElementException();
                T v = cur.valor;
                cur = cur.siguiente;
                return v;
            }
        };
    }
}