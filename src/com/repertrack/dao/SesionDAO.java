package com.repertrack.dao;

import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.model.SesionDePractica;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SesionDAO {
    int guardar(SesionDePractica s) throws PersistenciaException;
    boolean eliminar(int sesionId) throws PersistenciaException;
    SesionDePractica obtener(int sesionId) throws PersistenciaException;
    List<SesionDePractica> listar() throws PersistenciaException;
    List<SesionDePractica> listarPorInstrumento(int instrumentoId) throws PersistenciaException;
    List<SesionDePractica> listarPorElemento(int elementoId) throws PersistenciaException;
    Map<Integer, Long> acumuladoPorInstrumento() throws PersistenciaException;
    Map<Integer, Long> acumuladoPorElemento() throws PersistenciaException;
    int frecuenciaUltimos7Dias() throws PersistenciaException;
    double promedioTiempo() throws PersistenciaException;
    Map<Integer, LocalDate> ultimaFechaPorElemento() throws PersistenciaException;
}