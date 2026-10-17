package com.repertrack.service;

import com.repertrack.dao.SesionDAO;
import com.repertrack.dao.SesionDAOImpl;
import com.repertrack.estructuras.Busqueda;
import com.repertrack.estructuras.Ordenacion;
import com.repertrack.excepcion.LongitudExcedidaException;
import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.excepcion.RangoInvalidoException;
import com.repertrack.excepcion.SesionSinReferenciaException;
import com.repertrack.model.Instrumento;
import com.repertrack.model.ElementoDeEstudio;
import com.repertrack.model.SesionDePractica;

import java.util.Comparator;
import java.util.List;

/**
 * Servicio de sesiones (CU006). Aplica las reglas de negocio RN-01..RN-05
 * antes de invocar al DAO.
 *
 * Refactor TP3:
 *   - Trabaja con asociaciones Instrumento + ElementoDeEstudio (no con IDs sueltos).
 *   - Mantiene compatibilidad con la firma antigua (int,int,int,String).
 *   - Lanza SesionSinReferenciaException para RN-01, RangoInvalidoException para RN-02
 *     y LongitudExcedidaException para RN-04.
 *   - Ofrece metodos de busqueda (Busqueda.lineal) y ordenacion (Ordenacion.*)
 *     que son requisitos de la rubrica.
 */
public class SesionService {

    private final SesionDAO dao;

    public SesionService() { this.dao = new SesionDAOImpl(); }
    public SesionService(SesionDAO dao) { this.dao = dao; }

    /** API nueva (TP3): recibe asociaciones y aplica todas las validaciones. */
    public int registrarSesion(Instrumento instrumento, ElementoDeEstudio elemento,
                               int tiempoMin, String comentario)
            throws SesionSinReferenciaException, RangoInvalidoException, LongitudExcedidaException, PersistenciaException {
        if (instrumento == null || elemento == null) {
            throw new SesionSinReferenciaException(
                "Debe seleccionar un instrumento y un elemento de estudio antes de iniciar la sesion (RN-01).");
        }
        if (tiempoMin <= 0) {
            throw new RangoInvalidoException("El tiempo neto debe ser mayor a cero (RN-02).");
        }
        if (comentario != null && comentario.length() > 500) {
            throw new LongitudExcedidaException("El comentario no puede superar los 500 caracteres (RN-04).");
        }
        SesionDePractica s = new SesionDePractica(instrumento, elemento, tiempoMin, comentario);
        return dao.guardar(s);
    }

    /** API compatible hacia atras (TP2). */
    public int registrarSesion(int instrumentoId, int elementoId, int tiempoMin, String comentario)
            throws PersistenciaException {
        if (instrumentoId <= 0 || elementoId <= 0) {
            throw new com.repertrack.excepcion.PersistenciaException(
                "Debe seleccionar un instrumento y un elemento de estudio antes de iniciar la sesion (RN-01).");
        }
        if (tiempoMin <= 0) {
            throw new com.repertrack.excepcion.PersistenciaException(
                "El tiempo neto debe ser mayor a cero (RN-02).");
        }
        if (comentario != null && comentario.length() > 500) {
            throw new com.repertrack.excepcion.PersistenciaException(
                "El comentario no puede superar los 500 caracteres (RN-04).");
        }
        SesionDePractica s = new SesionDePractica(0, instrumentoId, elementoId,
                java.time.LocalDateTime.now(), tiempoMin, comentario);
        return dao.guardar(s);
    }

    public boolean eliminar(int sesionId) throws PersistenciaException { return dao.eliminar(sesionId); }

    public SesionDePractica obtener(int sesionId) throws PersistenciaException { return dao.obtener(sesionId); }

    public List<SesionDePractica> listar() throws PersistenciaException { return dao.listar(); }

    public List<SesionDePractica> listarPorInstrumento(int instrumentoId) throws PersistenciaException {
        return dao.listarPorInstrumento(instrumentoId);
    }

    public List<SesionDePractica> listarPorElemento(int elementoId) throws PersistenciaException {
        return dao.listarPorElemento(elementoId);
    }

    // --- Busqueda y ordenacion (requisitos de la rubrica TP3) ---

    public SesionDePractica buscarPorId(List<SesionDePractica> lista, int sesionId) {
        return Busqueda.lineal(lista, s -> s.getId() == sesionId);
    }

    public List<SesionDePractica> ordenarPorFechaDesc(List<SesionDePractica> lista) {
        Ordenacion.seleccion(lista, Comparator.comparing(SesionDePractica::getFechaRegistro).reversed());
        return lista;
    }

    public List<SesionDePractica> ordenarPorTiempoDesc(List<SesionDePractica> lista) {
        Ordenacion.burbuja(lista, Comparator.comparingInt(SesionDePractica::getTiempoNetoMin).reversed());
        return lista;
    }
}