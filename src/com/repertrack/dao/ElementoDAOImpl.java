package com.repertrack.dao;

import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.model.Cancion;
import com.repertrack.model.ElementoDeEstudio;
import com.repertrack.model.Ejercicio;
import com.repertrack.model.Instrumento;
import com.repertrack.model.TipoEjercicio;
import com.repertrack.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** ElementoDAO: single-table inheritance mapeada por la columna categoria. */
public class ElementoDAOImpl implements ElementoDAO {

    private static final String SQL_INSERT =
        "INSERT INTO elemento_estudio (instrumento_id, nombre, categoria, dificultad, " +
        "autor, interprete_original, tonalidad, compas, tipo_ejercicio, bpm_sugerido, activo) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_DELETE_LOG =
        "UPDATE elemento_estudio SET activo=0 WHERE elemento_id=?";

    private static final String SQL_SELECT_ONE =
        "SELECT elemento_id, instrumento_id, nombre, categoria, dificultad, autor, " +
        "interprete_original, tonalidad, compas, tipo_ejercicio, bpm_sugerido, activo " +
        "FROM elemento_estudio WHERE elemento_id=?";

    private static final String SQL_SELECT_ALL =
        "SELECT elemento_id, instrumento_id, nombre, categoria, dificultad, autor, " +
        "interprete_original, tonalidad, compas, tipo_ejercicio, bpm_sugerido, activo " +
        "FROM elemento_estudio ORDER BY nombre";

    private static final String SQL_SELECT_BY_INST =
        "SELECT elemento_id, instrumento_id, nombre, categoria, dificultad, autor, " +
        "interprete_original, tonalidad, compas, tipo_ejercicio, bpm_sugerido, activo " +
        "FROM elemento_estudio WHERE instrumento_id=? ORDER BY nombre";

    private static final String SQL_SELECT_ACTIVOS =
        "SELECT elemento_id, instrumento_id, nombre, categoria, dificultad, autor, " +
        "interprete_original, tonalidad, compas, tipo_ejercicio, bpm_sugerido, activo " +
        "FROM elemento_estudio WHERE activo=1 ORDER BY nombre";

    private final boolean enMemoria;
    private final List<ElementoDeEstudio> cache = new ArrayList<>();

    public ElementoDAOImpl() {
        this.enMemoria = !ConexionDB.mysqlDisponible();
        if (enMemoria) System.out.println("[WARN] MySQL no disponible. ElementoDAO en modo memoria.");
    }

    @Override
    public void insertar(ElementoDeEstudio e) throws PersistenciaException {
        if (enMemoria) {
            int nuevoId = cache.stream().mapToInt(ElementoDeEstudio::getId).max().orElse(0) + 1;
            e.setId(nuevoId);
            cache.add(clonar(e));
            return;
        }
        try (Connection con = ConexionDB.obtener()) {
            boolean auto = con.getAutoCommit();
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, e.getInstrumento().getId());
                ps.setString(2, e.getNombre());
                ps.setString(3, e.getCategoria());
                ps.setInt(4, e.getDificultad());
                if (e instanceof Cancion) {
                    Cancion c = (Cancion) e;
                    ps.setString(5, c.getAutor());
                    ps.setString(6, c.getInterpreteOriginal());
                    ps.setString(7, c.getTonalidad());
                    ps.setString(8, c.getCompas());
                    ps.setNull(9, Types.VARCHAR);
                    ps.setNull(10, Types.SMALLINT);
                } else if (e instanceof Ejercicio) {
                    Ejercicio x = (Ejercicio) e;
                    ps.setNull(5, Types.VARCHAR);
                    ps.setNull(6, Types.VARCHAR);
                    ps.setNull(7, Types.VARCHAR);
                    ps.setNull(8, Types.VARCHAR);
                    ps.setString(9, x.getTipoEjercicioEnum().getValor());
                    ps.setInt(10, x.getBpmSugerido());
                } else {
                    ps.setNull(5, Types.VARCHAR);
                    ps.setNull(6, Types.VARCHAR);
                    ps.setNull(7, Types.VARCHAR);
                    ps.setNull(8, Types.VARCHAR);
                    ps.setNull(9, Types.VARCHAR);
                    ps.setNull(10, Types.SMALLINT);
                }
                ps.setInt(11, e.isActivo() ? 1 : 0);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) e.setId(keys.getInt(1));
                }
            }
            con.commit();
            con.setAutoCommit(auto);
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo insertar el elemento.", ex);
        } catch (Exception ex) {
            throw new PersistenciaException("Error de mapeo al insertar el elemento.", ex);
        }
    }

    @Override
    public void actualizar(ElementoDeEstudio e) throws PersistenciaException {
        if (enMemoria) {
            for (int k = 0; k < cache.size(); k++) {
                if (cache.get(k).getId() == e.getId()) { cache.set(k, clonar(e)); return; }
            }
            throw new PersistenciaException("Elemento " + e.getId() + " no existe.");
        }
        // (Para brevedad del prototipo se conserva la version JDBC completa en una iteracion futura.)
        try (Connection con = ConexionDB.obtener()) {
            con.createStatement().executeUpdate("UPDATE elemento_estudio SET activo=" + (e.isActivo()?1:0) + " WHERE elemento_id=" + e.getId());
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo actualizar el elemento.", ex);
        }
    }

    @Override
    public void eliminarLogico(int elementoId) throws PersistenciaException {
        if (enMemoria) {
            for (ElementoDeEstudio e : cache) {
                if (e.getId() == elementoId) { e.darDeBaja(); return; }
            }
            throw new PersistenciaException("Elemento " + elementoId + " no existe.");
        }
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE_LOG)) {
            ps.setInt(1, elementoId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo eliminar el elemento.", ex);
        }
    }

    @Override
    public ElementoDeEstudio obtener(int elementoId) throws PersistenciaException {
        if (enMemoria) {
            for (ElementoDeEstudio e : cache) if (e.getId() == elementoId) return clonar(e);
            return null;
        }
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_ONE)) {
            ps.setInt(1, elementoId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapear(rs); }
            return null;
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo obtener el elemento.", ex);
        } catch (Exception ex) {
            throw new PersistenciaException("Error de mapeo del elemento.", ex);
        }
    }

    @Override
    public List<ElementoDeEstudio> listar() throws PersistenciaException {
        if (enMemoria) {
            List<ElementoDeEstudio> r = new ArrayList<>();
            for (ElementoDeEstudio e : cache) r.add(clonar(e));
            return r;
        }
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<ElementoDeEstudio> r = new ArrayList<>();
            while (rs.next()) r.add(mapear(rs));
            return r;
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo listar elementos.", ex);
        } catch (Exception ex) {
            throw new PersistenciaException("Error de mapeo listando elementos.", ex);
        }
    }

    @Override
    public List<ElementoDeEstudio> listarPorInstrumento(int instrumentoId) throws PersistenciaException {
        if (enMemoria) {
            List<ElementoDeEstudio> r = new ArrayList<>();
            for (ElementoDeEstudio e : cache) {
                if (e.getInstrumento() != null && e.getInstrumento().getId() == instrumentoId) r.add(clonar(e));
            }
            return r;
        }
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_BY_INST)) {
            ps.setInt(1, instrumentoId);
            try (ResultSet rs = ps.executeQuery()) {
                List<ElementoDeEstudio> r = new ArrayList<>();
                while (rs.next()) r.add(mapear(rs));
                return r;
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo listar elementos por instrumento.", ex);
        } catch (Exception ex) {
            throw new PersistenciaException("Error de mapeo listando elementos por instrumento.", ex);
        }
    }

    @Override
    public List<ElementoDeEstudio> listarActivos() throws PersistenciaException {
        if (enMemoria) {
            List<ElementoDeEstudio> r = new ArrayList<>();
            for (ElementoDeEstudio e : cache) if (e.isActivo()) r.add(clonar(e));
            return r;
        }
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_ACTIVOS);
             ResultSet rs = ps.executeQuery()) {
            List<ElementoDeEstudio> r = new ArrayList<>();
            while (rs.next()) { ElementoDeEstudio e = mapear(rs); if (e.isActivo()) r.add(e); }
            return r;
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo listar elementos activos.", ex);
        } catch (Exception ex) {
            throw new PersistenciaException("Error de mapeo listando elementos activos.", ex);
        }
    }

    private ElementoDeEstudio mapear(ResultSet rs) throws SQLException {
        Instrumento inst = new Instrumento();
        inst.setId(rs.getInt("instrumento_id"));
        String cat = rs.getString("categoria");
        ElementoDeEstudio e;
        try {
            if ("Cancion".equalsIgnoreCase(cat)) {
                Cancion c = new Cancion();
                c.setInstrumento(inst);
                c.setId(rs.getInt("elemento_id"));
                c.setNombre(rs.getString("nombre"));
                c.setDificultad(rs.getInt("dificultad"));
                c.setAutor(rs.getString("autor") == null ? "" : rs.getString("autor"));
                c.setInterpreteOriginal(rs.getString("interprete_original"));
                c.setTonalidad(rs.getString("tonalidad"));
                c.setCompas(rs.getString("compas"));
                c.setActivo(rs.getInt("activo") == 1);
                e = c;
            } else {
                Ejercicio x = new Ejercicio();
                x.setInstrumento(inst);
                x.setId(rs.getInt("elemento_id"));
                x.setNombre(rs.getString("nombre"));
                x.setDificultad(rs.getInt("dificultad"));
                x.setTipoEjercicio(TipoEjercicio.desde(rs.getString("tipo_ejercicio")));
                int bpm = rs.getInt("bpm_sugerido");
                if (rs.wasNull()) x.setBpmSugerido(80); else x.setBpmSugerido(bpm);
                x.setActivo(rs.getInt("activo") == 1);
                e = x;
            }
        } catch (Exception ex) {
            throw new SQLException("Mapeo invalido desde fila: " + ex.getMessage(), ex);
        }
        return e;
    }

    private ElementoDeEstudio clonar(ElementoDeEstudio e) {
        try {
            if (e instanceof Cancion) {
                Cancion o = (Cancion) e;
                return new Cancion(o.getId(), o.getInstrumento(), o.getNombre(), o.getDificultad(),
                        o.getTiempoEstimadoDominioMin(), o.getAutor(), o.getInterpreteOriginal(),
                        o.getTonalidad(), o.getCompas());
            } else {
                Ejercicio o = (Ejercicio) e;
                return new Ejercicio(o.getId(), o.getInstrumento(), o.getNombre(), o.getDificultad(),
                        o.getTiempoEstimadoDominioMin(), o.getTipoEjercicioEnum(), o.getBpmSugerido());
            }
        } catch (Exception ex) {
            return e;
        }
    }
}