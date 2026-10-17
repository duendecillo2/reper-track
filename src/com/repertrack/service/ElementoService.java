package com.repertrack.service;

import com.repertrack.dao.ElementoDAO;
import com.repertrack.dao.ElementoDAOImpl;
import com.repertrack.estructuras.Pila;
import com.repertrack.excepcion.LongitudExcedidaException;
import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.excepcion.RangoInvalidoException;
import com.repertrack.model.ElementoDeEstudio;

import java.util.List;

/**
 * Servicio de Elementos (Canciones y Ejercicios).
 * Refactor TP3: usa Pila propia para soportar "deshacer ultimo alta".
 */
public class ElementoService {

    private final ElementoDAO dao;
    private final Pila<ElementoDeEstudio> undo;

    public ElementoService() { this.dao = new ElementoDAOImpl(); this.undo = new Pila<>(); }
    public ElementoService(ElementoDAO dao) { this.dao = dao; this.undo = new Pila<>(); }

    public void alta(ElementoDeEstudio e) throws RangoInvalidoException, LongitudExcedidaException, PersistenciaException {
        if (e == null) throw new IllegalArgumentException("Elemento nulo.");
        if (e.getInstrumento() == null) throw new IllegalArgumentException("El elemento debe pertenecer a un instrumento.");
        if (e.getDificultad() < 1 || e.getDificultad() > 10) {
            throw new RangoInvalidoException("La dificultad debe estar entre 1 y 10.");
        }
        dao.insertar(e);
        undo.apilar(e);
    }

    public void modificacion(ElementoDeEstudio e) throws PersistenciaException {
        if (e == null) throw new IllegalArgumentException("Elemento nulo.");
        dao.actualizar(e);
    }

    public void bajaLogica(int elementoId) throws PersistenciaException { dao.eliminarLogico(elementoId); }
    public ElementoDeEstudio obtener(int elementoId) throws PersistenciaException { return dao.obtener(elementoId); }
    public List<ElementoDeEstudio> listar() throws PersistenciaException { return dao.listar(); }
    public List<ElementoDeEstudio> listarPorInstrumento(int id) throws PersistenciaException { return dao.listarPorInstrumento(id); }
    public List<ElementoDeEstudio> listarActivos() throws PersistenciaException { return dao.listarActivos(); }

    public boolean puedeDeshacer() { return !undo.estaVacia(); }
    public ElementoDeEstudio deshacerUltimo() { return undo.desapilar(); }
}