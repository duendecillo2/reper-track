package com.repertrack.dao;

import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.model.Instrumento;
import com.repertrack.model.Musico;
import com.repertrack.model.TipoInstrumento;
import com.repertrack.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** InstrumentoDAO: JDBC real con fallback automatico a memoria si MySQL no responde. */
public class InstrumentoDAOImpl implements InstrumentoDAO {

    private static final String SQL_INSERT =
        "INSERT INTO instrumento (musico_id, nombre, tipo, observaciones, activo) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
        "UPDATE instrumento SET nombre=?, tipo=?, observaciones=?, activo=? WHERE instrumento_id=?";

    private static final String SQL_DELETE_LOG =
        "UPDATE instrumento SET activo=0 WHERE instrumento_id=?";

    private static final String SQL_SELECT_ONE =
        "SELECT instrumento_id, musico_id, nombre, tipo, observaciones, activo FROM instrumento WHERE instrumento_id=?";

    private static final String SQL_SELECT_ALL =
        "SELECT instrumento_id, musico_id, nombre, tipo, observaciones, activo FROM instrumento ORDER BY nombre";

    private static final String SQL_SELECT_ACTIVOS =
        "SELECT instrumento_id, musico_id, nombre, tipo, observaciones, activo FROM instrumento WHERE activo=1 ORDER BY nombre";

    private final boolean enMemoria;
    private final List<Instrumento> cache = new ArrayList<>();

    public InstrumentoDAOImpl() {
        this.enMemoria = !ConexionDB.mysqlDisponible();
        if (enMemoria) System.out.println("[WARN] MySQL no disponible. InstrumentoDAO en modo memoria.");
    }

    @Override
    public void insertar(Instrumento i) throws PersistenciaException {
        if (enMemoria) {
            int nuevoId = cache.stream().mapToInt(Instrumento::getId).max().orElse(0) + 1;
            i.setId(nuevoId);
            cache.add(clonar(i));
            return;
        }
        try (Connection con = ConexionDB.obtener()) {
            boolean auto = con.getAutoCommit();
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, i.getMusico() == null ? 1 : i.getMusico().getId());
                ps.setString(2, i.getNombre());
                ps.setString(3, i.getTipo().getValor());
                ps.setString(4, i.getObservaciones());
                ps.setInt(5, i.isActivo() ? 1 : 0);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) i.setId(keys.getInt(1));
                }
            }
            con.commit();
            con.setAutoCommit(auto);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo insertar el instrumento.", e);
        }
    }

    @Override
    public void actualizar(Instrumento i) throws PersistenciaException {
        if (enMemoria) {
            for (int k = 0; k < cache.size(); k++) {
                if (cache.get(k).getId() == i.getId()) { cache.set(k, clonar(i)); return; }
            }
            throw new PersistenciaException("Instrumento " + i.getId() + " no existe.");
        }
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, i.getNombre());
            ps.setString(2, i.getTipo().getValor());
            ps.setString(3, i.getObservaciones());
            ps.setInt(4, i.isActivo() ? 1 : 0);
            ps.setInt(5, i.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo actualizar el instrumento.", e);
        }
    }

    @Override
    public void eliminarLogico(int instrumentoId) throws PersistenciaException {
        if (enMemoria) {
            for (Instrumento it : cache) {
                if (it.getId() == instrumentoId) { it.darDeBaja(); return; }
            }
            throw new PersistenciaException("Instrumento " + instrumentoId + " no existe.");
        }
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE_LOG)) {
            ps.setInt(1, instrumentoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar el instrumento.", e);
        }
    }

    @Override
    public Instrumento obtener(int instrumentoId) throws PersistenciaException {
        if (enMemoria) {
            for (Instrumento it : cache) if (it.getId() == instrumentoId) return clonar(it);
            return null;
        }
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_ONE)) {
            ps.setInt(1, instrumentoId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapear(rs); }
            return null;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo obtener el instrumento.", e);
        }
    }

    @Override
    public List<Instrumento> listar() throws PersistenciaException {
        if (enMemoria) {
            List<Instrumento> r = new ArrayList<>();
            for (Instrumento it : cache) r.add(clonar(it));
            return r;
        }
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<Instrumento> r = new ArrayList<>();
            while (rs.next()) r.add(mapear(rs));
            return r;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo listar instrumentos.", e);
        }
    }

    @Override
    public List<Instrumento> listarActivos() throws PersistenciaException {
        if (enMemoria) {
            List<Instrumento> r = new ArrayList<>();
            for (Instrumento it : cache) if (it.isActivo()) r.add(clonar(it));
            return r;
        }
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_ACTIVOS);
             ResultSet rs = ps.executeQuery()) {
            List<Instrumento> r = new ArrayList<>();
            while (rs.next()) { Instrumento i = mapear(rs); if (i.isActivo()) r.add(i); }
            return r;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo listar instrumentos activos.", e);
        }
    }

    private Instrumento mapear(ResultSet rs) throws SQLException {
        Instrumento i = new Instrumento();
        i.setId(rs.getInt("instrumento_id"));
        Musico m = new Musico();
        m.setId(rs.getInt("musico_id"));
        i.setMusico(m);
        try { i.setNombre(rs.getString("nombre")); } catch (Exception ignored) {}
        try { i.setObservaciones(rs.getString("observaciones")); } catch (Exception ignored) {}
        i.setTipo(TipoInstrumento.desde(rs.getString("tipo")));
        i.setActivo(rs.getInt("activo") == 1);
        return i;
    }

    private Instrumento clonar(Instrumento i) {
        Instrumento c = new Instrumento();
        c.setId(i.getId());
        c.setMusico(i.getMusico());
        try {
            c.setNombre(i.getNombre());
            c.setObservaciones(i.getObservaciones());
        } catch (Exception ignored) {}
        c.setTipo(i.getTipo());
        c.setActivo(i.isActivo());
        return c;
    }
}