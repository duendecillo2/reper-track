package com.repertrack.dao;

import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.model.Instrumento;

import java.util.List;

public interface InstrumentoDAO {
    void insertar(Instrumento i) throws PersistenciaException;
    void actualizar(Instrumento i) throws PersistenciaException;
    void eliminarLogico(int instrumentoId) throws PersistenciaException;
    Instrumento obtener(int instrumentoId) throws PersistenciaException;
    List<Instrumento> listar() throws PersistenciaException;
    List<Instrumento> listarActivos() throws PersistenciaException;
}