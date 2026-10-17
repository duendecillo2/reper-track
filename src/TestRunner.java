import com.repertrack.dao.SesionDAO;
import com.repertrack.excepcion.LongitudExcedidaException;
import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.excepcion.RangoInvalidoException;
import com.repertrack.excepcion.SesionSinReferenciaException;
import com.repertrack.model.Alerta;
import com.repertrack.model.Cancion;
import com.repertrack.model.ElementoDeEstudio;
import com.repertrack.model.Ejercicio;
import com.repertrack.model.Estadistica;
import com.repertrack.model.Instrumento;
import com.repertrack.model.Musico;
import com.repertrack.model.Nivel;
import com.repertrack.model.SesionDePractica;
import com.repertrack.model.TipoEjercicio;
import com.repertrack.model.TipoInstrumento;
import com.repertrack.service.ExportadorPdfService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mini-runner de pruebas sin dependencias externas (sustituto de JUnit
 * para el prototipo). Cubre: RN-01, RN-02, RN-04, herencia, polimorfismo,
 * estadisticas, alertas, exportacion a PDF y validacion de errores.
 *
 * Refactor TP3: 14 casos (CP01..CP14) alineados con el plan de pruebas unificado.
 */
public class TestRunner {
    static int ok = 0, fail = 0;

    static void check(boolean cond, String nombre) {
        if (cond) { ok++; System.out.println("PASS " + nombre); }
        else { fail++; System.out.println("FAIL " + nombre); }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("===== ReperTrack - TestRunner (14 casos) =====");

        Musico musico = new Musico();
        musico.setId(1);

        Instrumento inst1 = new Instrumento();
        inst1.setId(1);
        inst1.setMusico(musico);
        silenciar(() -> inst1.setNombre("PRS Custom 24"));
        inst1.setTipo(TipoInstrumento.CUERDA);

        Instrumento inst2 = new Instrumento();
        inst2.setId(2);
        inst2.setMusico(musico);
        silenciar(() -> inst2.setNombre("Taylor 814ce"));
        inst2.setTipo(TipoInstrumento.CUERDA);

        Cancion cancion = new Cancion();
        cancion.setId(1);
        cancion.setInstrumento(inst1);
        silenciar(() -> cancion.setNombre("Clair de Lune"));
        silenciar(() -> cancion.setDificultad(8));
        silenciar(() -> cancion.setTiempoEstimadoDominioMin(120));
        silenciar(() -> cancion.setAutor("Debussy"));
        silenciar(() -> cancion.setTonalidad("Db"));
        silenciar(() -> cancion.setCompas("6/8"));

        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setId(2);
        ejercicio.setInstrumento(inst1);
        silenciar(() -> ejercicio.setNombre("Pentatonic alternate"));
        silenciar(() -> ejercicio.setDificultad(4));
        silenciar(() -> ejercicio.setBpmSugerido(110));
        ejercicio.setTipoEjercicio(TipoEjercicio.ARPEGIO);

        SesionDAOStub sesionDAO = new SesionDAOStub();
        com.repertrack.service.SesionService sesionService = new com.repertrack.service.SesionService(sesionDAO);

        try {
            // CP01: sesion valida persiste
            int id = sesionService.registrarSesion(inst1, cancion, 30, "");
            check(id > 0, "CP01 sesion valida persiste");

            // CP02: sesion sin instrumento rechazada (RN-01)
            try {
                sesionService.registrarSesion(null, cancion, 30, "");
                check(false, "CP02 hurfana rechazada");
            } catch (SesionSinReferenciaException ex) {
                check(true, "CP02 hurfana rechazada");
            }

            // CP03: tiempo cero rechazado (RN-02)
            try {
                new SesionDePractica(inst1, cancion, 0, "");
                check(false, "CP03 tiempo cero rechazado");
            } catch (RangoInvalidoException ex) {
                check(true, "CP03 tiempo cero rechazado");
            }

            // CP04: frontera 1 min aceptada
            try {
                int id1 = sesionService.registrarSesion(inst1, cancion, 1, "");
                check(id1 > 0, "CP04 frontera 1 min aceptada");
            } catch (Exception ex) {
                check(false, "CP04 frontera 1 min aceptada");
            }

            // CP05: comentario 501 chars rechazado (RN-04)
            try {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 501; i++) sb.append('x');
                new SesionDePractica(inst1, cancion, 30, sb.toString());
                check(false, "CP05 comentario largo rechazado");
            } catch (LongitudExcedidaException ex) {
                check(true, "CP05 comentario largo rechazado");
            }

            // CP06: dificultad invalida rechazada
            try {
                Cancion c0 = new Cancion(99, inst1, "x", 0, 60, "a", "a", "C", "4/4");
                check(false, "CP06 dificultad invalida rechazada");
            } catch (RangoInvalidoException ex) {
                check(true, "CP06 dificultad invalida rechazada");
            }

            // CP07: BPM invalido rechazado
            try {
                Ejercicio x0 = new Ejercicio(98, inst1, "y", 4, 60, TipoEjercicio.ARPEGIO, 19);
                check(false, "CP07 BPM invalido rechazado");
            } catch (RangoInvalidoException ex) {
                check(true, "CP07 BPM invalido rechazado");
            }

            // CP08: Cancion.polimorfismo -> 480 min en dif 8 = 0.5
            check(Math.abs(cancion.calcularProgreso(480) - 0.5) < 1e-6,
                "CP08 progreso cancion 480/960=0.5");

            // CP09: Ejercicio.polimorfismo -> 120 min en dif 4 = 0.5
            check(Math.abs(ejercicio.calcularProgreso(120) - 0.5) < 1e-6,
                "CP09 progreso ejercicio 120/240=0.5");

            // CP10: acumulado por instrumento (dos sesiones sobre inst1, una sobre inst2)
            sesionDAO.limpiar();
            try {
                sesionService.registrarSesion(inst1, cancion, 30, "");
                sesionService.registrarSesion(inst1, ejercicio, 45, "");
                sesionService.registrarSesion(inst2, cancion, 60, "");
            } catch (Exception ignored) {}
            Map<Integer, Long> acum = sesionDAO.acumuladoPorInstrumento();
            long t1 = acum.getOrDefault(1, 0L);
            long t2 = acum.getOrDefault(2, 0L);
            check(t1 == 75 && t2 == 60, "CP10 acumulado por instrumento=75");

            // CP11: promedio global = (30+45+60)/3 = 45
            check(Math.abs(sesionDAO.promedioTiempo() - 45.0) < 1e-6, "CP11 promedio=45.0");

            // CP12: alerta por 45 dias sin practica
            sesionDAO.limpiar();
            try {
                SesionDePractica sVieja = new SesionDePractica(inst1, cancion, 30, "");
                sVieja.setFechaRegistro(LocalDateTime.now().minusDays(45));
                sesionService.registrarSesion(inst1, cancion, 30, "");
                SesionDePractica guardada = sesionDAO.listar().get(0);
                guardada.setFechaRegistro(LocalDateTime.now().minusDays(45));
            } catch (Exception ignored) {}
            Map<Integer, LocalDate> ultimas = sesionDAO.ultimaFechaPorElemento();
            int dias = -1;
            if (ultimas.containsKey(cancion.getId())) {
                dias = (int) ChronoUnit.DAYS.between(ultimas.get(cancion.getId()), LocalDate.now());
            }
            check(dias >= 45, "CP12 alerta por 45 dias sin practica");

            // CP13: PDF valido con estadisticas
            try {
                ExportadorPdfService pdf = new ExportadorPdfService();
                Estadistica est = new Estadistica();
                est.setTotalSesiones(3);
                est.agregarMinutosInstrumento(1, 75L);
                est.agregarMinutosInstrumento(2, 60L);
                est.agregarMinutosElemento(1, 60L);
                est.setFrecuenciaSemanal(3);
                est.setPromedioMinutosPorSesion(45.0);
                byte[] bytes = pdf.exportarBytes(est);
                String header = new String(bytes, 0, 8, "ISO-8859-1");
                check(bytes.length > 200 && header.startsWith("%PDF-1.4"),
                    "CP13 PDF valido con estadisticas");
            } catch (Exception ex) {
                check(false, "CP13 PDF valido con estadisticas");
            }

            // CP14: reporte vacio rechazado
            try {
                ExportadorPdfService pdf2 = new ExportadorPdfService();
                Estadistica estVacia = new Estadistica();
                pdf2.exportarBytes(estVacia);
                check(false, "CP14 reporte vacio rechazado");
            } catch (IllegalStateException ex) {
                check(ex.getMessage() != null && ex.getMessage().contains("No hay datos"),
                    "CP14 reporte vacio rechazado");
            }

        } catch (Exception ex) {
            System.out.println("[FATAL] " + ex.getMessage());
            ex.printStackTrace();
        }

        System.out.println("----");
        System.out.println("Resultado: " + ok + " PASS, " + fail + " FAIL");
        if (fail > 0) System.exit(1);
    }

    private static void silenciar(SetupBloque b) { try { b.run(); } catch (Exception ignored) {} }
    @FunctionalInterface private interface SetupBloque { void run() throws Exception; }

    /** Stub en memoria del SesionDAO para que las pruebas sean deterministicas. */
    static class SesionDAOStub implements SesionDAO {
        private final List<SesionDePractica> sesiones = new ArrayList<>();
        private int nextId = 1;
        public void limpiar() { sesiones.clear(); nextId = 1; }

        @Override
        public int guardar(SesionDePractica s) throws PersistenciaException {
            s.setId(nextId++);
            sesiones.add(s);
            return s.getId();
        }

        @Override
        public boolean eliminar(int sesionId) throws PersistenciaException {
            return sesiones.removeIf(s -> s.getId() == sesionId);
        }

        @Override
        public SesionDePractica obtener(int sesionId) throws PersistenciaException {
            for (SesionDePractica s : sesiones) if (s.getId() == sesionId) return s;
            return null;
        }

        @Override
        public List<SesionDePractica> listar() throws PersistenciaException { return new ArrayList<>(sesiones); }

        @Override
        public List<SesionDePractica> listarPorInstrumento(int id) throws PersistenciaException {
            List<SesionDePractica> r = new ArrayList<>();
            for (SesionDePractica s : sesiones) if (s.getInstrumentoId() == id) r.add(s);
            return r;
        }

        @Override
        public List<SesionDePractica> listarPorElemento(int id) throws PersistenciaException {
            List<SesionDePractica> r = new ArrayList<>();
            for (SesionDePractica s : sesiones) if (s.getElementoId() == id) r.add(s);
            return r;
        }

        @Override
        public Map<Integer, Long> acumuladoPorInstrumento() throws PersistenciaException {
            Map<Integer, Long> m = new LinkedHashMap<>();
            for (SesionDePractica s : sesiones) m.merge(s.getInstrumentoId(), (long) s.getTiempoNetoMin(), Long::sum);
            return m;
        }

        @Override
        public Map<Integer, Long> acumuladoPorElemento() throws PersistenciaException {
            Map<Integer, Long> m = new LinkedHashMap<>();
            for (SesionDePractica s : sesiones) m.merge(s.getElementoId(), (long) s.getTiempoNetoMin(), Long::sum);
            return m;
        }

        @Override
        public int frecuenciaUltimos7Dias() throws PersistenciaException {
            LocalDateTime limite = LocalDateTime.now().minusDays(7);
            int n = 0;
            for (SesionDePractica s : sesiones) if (s.getFechaRegistro() != null && s.getFechaRegistro().isAfter(limite)) n++;
            return n;
        }

        @Override
        public double promedioTiempo() throws PersistenciaException {
            if (sesiones.isEmpty()) return 0.0;
            long total = 0;
            for (SesionDePractica s : sesiones) total += s.getTiempoNetoMin();
            return (double) total / sesiones.size();
        }

        @Override
        public Map<Integer, LocalDate> ultimaFechaPorElemento() throws PersistenciaException {
            Map<Integer, LocalDate> m = new LinkedHashMap<>();
            for (SesionDePractica s : sesiones) {
                if (s.getFechaRegistro() == null) continue;
                LocalDate d = s.getFechaRegistro().toLocalDate();
                int id = s.getElementoId();
                LocalDate prev = m.get(id);
                if (prev == null || d.isAfter(prev)) m.put(id, d);
            }
            return m;
        }
    }
}