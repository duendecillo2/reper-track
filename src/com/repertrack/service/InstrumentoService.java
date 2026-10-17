package com.repertrack.service;

import com.repertrack.dao.InstrumentoDAO;
import com.repertrack.dao.InstrumentoDAOImpl;
import com.repertrack.excepcion.LongitudExcedidaException;
import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.model.Instrumento;

import java.util.List;

/** Servicio de Instrumentos. Encapsula DAOs y validaciones. */
public class InstrumentoService {

    private final InstrumentoDAO dao;

    public InstrumentoService() { this.dao = new InstrumentoDAOImpl(); }
    public InstrumentoService(InstrumentoDAO dao) { this.dao = dao; }

    public void alta(Instrumento i) throws LongitudExcedidaException, PersistenciaException {
        if (i == null) throw new IllegalArgumentException("Instrumento nulo.");
        if (i.getNombre() == null || i.getNombre().trim().isEmpty()) {
            throw new LongitudExcedidaException("El nombre del instrumento no puede estar vacio.");
        }
        if (i.getMusico() == null) throw new IllegalArgumentException("El instrumento debe pertenecer a un musico.");
        dao.insertar(i);
    }

    public void modificacion(Instrumento i) throws PersistenciaException {
        if (i == null) throw new IllegalArgumentException("Instrumento nulo.");
        dao.actualizar(i);
    }

    public void bajaLogica(int instrumentoId) throws PersistenciaException {
        dao.eliminarLogico(instrumentoId);
    }

    public Instrumento obtener(int instrumentoId) throws PersistenciaException { return dao.obtener(instrumentoId); }
    public List<Instrumento> listar() throws PersistenciaException { return dao.listar(); }
    public List<Instrumento> listarActivos() throws PersistenciaException { return dao.listarActivos(); }
}