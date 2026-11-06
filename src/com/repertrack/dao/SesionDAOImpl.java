package com.repertrack.dao;

import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.model.Cancion;
import com.repertrack.model.ElementoDeEstudio;
import com.repertrack.model.Instrumento;
import com.repertrack.model.SesionDePractica;
import com.repertrack.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SesionDAOImpl: cumple RNF05 (transacciones ACID con setAutoCommit/commit/rollback)
 * y mantiene replica en memoria para que el prototipo sea operacional sin MySQL.
 */
public class SesionDAOImpl implements SesionDAO {

    private static final String SQL_INSERT =
        "INSERT INTO sesion_practica (instrumento_id, elemento_id, fecha_registro, tiempo_neto_min, comentario) " +
        "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_DELETE = "DELETE FROM sesion_practica WHERE sesion_id=?";
    private static final String SQL_SELECT_ONE =
        "SELECT sesion_id, instrumento_id, elemento_id, fecha_registro, tiempo_neto_min, comentario FROM sesion_practica WHERE sesion_id=?";
    private static final String SQL_SELECT_ALL =
        "SELECT sesion_id, instrumento_id, elemento_id, fecha_registro, tiempo_neto_min, comentario FROM sesion_practica ORDER BY fecha_registro DESC";

    private final boolean enMemoria;
    private final List<SesionDePractica> cache = new ArrayList<>();
    private int secuencia = 1;

    public SesionDAOImpl() {
        this.enMemoria = !ConexionDB.mysqlDisponible();
        if (enMemoria) System.out.println("[WARN] MySQL no disponible. SesionDAO en modo memoria.");
    }

    @Override
    public int guardar(SesionDePractica s) throws PersistenciaException {
        if (enMemoria) {
            int id = secuencia++;
            s.setId(id);
            cache.add(clonar(s));
            return id;
        }
        try (Connection con = ConexionDB.obtener()) {
            boolean auto = con.getAutoCommit();
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, s.getInstrumentoId());
                ps.setInt(2, s.getElementoId());
                ps.setTimestamp(3, Timestamp.valueOf(s.getFechaRegistro()));
                ps.setInt(4, s.getTiempoNetoMin());
                ps.setString(5, s.getComentario());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) { int id = rs.getInt(1); s.setId(id); con.commit(); con.setAutoCommit(auto); return id; }
                }
                con.commit();
                con.setAutoCommit(auto);
                return 0;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo guardar la sesion.", ex);
        }
    }

    @Override
    public boolean eliminar(int sesionId) throws PersistenciaException {
        if (enMemoria) {
            boolean r = cache.removeIf(s -> s.getId() == sesionId);
            return r;
        }
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, sesionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo eliminar la sesion.", ex);
        }
    }

    @Override
    public SesionDePractica obtener(int sesionId) throws PersistenciaException {
        if (enMemoria) {
            for (SesionDePractica s : cache) if (s.getId() == sesionId) return clonar(s);
            return null;
        }
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_ONE)) {
            ps.setInt(1, sesionId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapear(rs); }
            return null;
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo obtener la sesion.", ex);
        }
    }

    @Override
    public List<SesionDePractica> listar() throws PersistenciaException {
        if (enMemoria) {
            List<SesionDePractica> r = new ArrayList<>();
            for (SesionDePractica s : cache) r.add(clonar(s));
            return r;
        }
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<SesionDePractica> r = new ArrayList<>();
            while (rs.next()) r.add(mapear(rs));
            return r;
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo listar sesiones.", ex);
        }
    }

    @Override
    public List<SesionDePractica> listarPorInstrumento(int instrumentoId) throws PersistenciaException {
        List<SesionDePractica> todas = listar();
        List<SesionDePractica> r = new ArrayList<>();
        for (SesionDePractica s : todas) if (s.getInstrumentoId() == instrumentoId) r.add(s);
        return r;
    }

    @Override
    public List<SesionDePractica> listarPorElemento(int elementoId) throws PersistenciaException {
        List<SesionDePractica> todas = listar();
        List<SesionDePractica> r = new ArrayList<>();
        for (SesionDePractica s : todas) if (s.getElementoId() == elementoId) r.add(s);
        return r;
    }

    @Override
    public Map<Integer, Long> acumuladoPorInstrumento() throws PersistenciaException {
        Map<Integer, Long> m = new LinkedHashMap<>();
        for (SesionDePractica s : listar()) m.merge(s.getInstrumentoId(), (long) s.getTiempoNetoMin(), Long::sum);
        return m;
    }

    @Override
    public Map<Integer, Long> acumuladoPorElemento() throws PersistenciaException {
        Map<Integer, Long> m = new LinkedHashMap<>();
        for (SesionDePractica s : listar()) m.merge(s.getElementoId(), (long) s.getTiempoNetoMin(), Long::sum);
        return m;
    }

    @Override
    public int frecuenciaUltimos7Dias() throws PersistenciaException {
        LocalDateTime limite = LocalDateTime.now().minusDays(7);
        int n = 0;
        for (SesionDePractica s : listar()) if (s.getFechaRegistro() != null && s.getFechaRegistro().isAfter(limite)) n++;
        return n;
    }

    @Override
    public double promedioTiempo() throws PersistenciaException {
        List<SesionDePractica> t = listar();
        if (t.isEmpty()) return 0.0;
        long total = 0;
        for (SesionDePractica s : t) total += s.getTiempoNetoMin();
        return (double) total / t.size();
    }

    @Override
    public Map<Integer, LocalDate> ultimaFechaPorElemento() throws PersistenciaException {
        Map<Integer, LocalDate> m = new LinkedHashMap<>();
        for (SesionDePractica s : listar()) {
            if (s.getFechaRegistro() == null) continue;
            LocalDate d = s.getFechaRegistro().toLocalDate();
            int id = s.getElementoId();
            LocalDate prev = m.get(id);
            if (prev == null || d.isAfter(prev)) m.put(id, d);
        }
        return m;
    }

    private SesionDePractica mapear(ResultSet rs) throws SQLException {
        SesionDePractica s = new SesionDePractica();
        s.setId(rs.getInt("sesion_id"));
        Instrumento i = new Instrumento();
        i.setId(rs.getInt("instrumento_id"));
        s.setInstrumento(i);
        ElementoDeEstudio e = new Cancion();
        e.setId(rs.getInt("elemento_id"));
        s.setElemento(e);
        Timestamp ts = rs.getTimestamp("fecha_registro");
        s.setFechaRegistro(ts == null ? LocalDateTime.now() : ts.toLocalDateTime());
        try { s.setTiempoNetoMin(rs.getInt("tiempo_neto_min")); } catch (Exception ex) {
            throw new SQLException("tiempo invalido", ex);
        }
        try { s.setComentario(rs.getString("comentario")); } catch (Exception ignored) {}
        return s;
    }

    private SesionDePractica clonar(SesionDePractica s) {
        SesionDePractica r = new SesionDePractica(s.getId(), s.getInstrumentoId(), s.getElementoId(),
                s.getFechaRegistro(), s.getTiempoNetoMin(), s.getComentario());
        return r;
    }
}