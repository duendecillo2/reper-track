package com.repertrack.dao;

import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.model.ElementoDeEstudio;

import java.util.List;

public interface ElementoDAO {
    void insertar(ElementoDeEstudio e) throws PersistenciaException;
    void actualizar(ElementoDeEstudio e) throws PersistenciaException;
    void eliminarLogico(int elementoId) throws PersistenciaException;
    ElementoDeEstudio obtener(int elementoId) throws PersistenciaException;
    List<ElementoDeEstudio> listar() throws PersistenciaException;
    List<ElementoDeEstudio> listarPorInstrumento(int instrumentoId) throws PersistenciaException;
    List<ElementoDeEstudio> listarActivos() throws PersistenciaException;
}