import com.repertrack.dao.SesionDAO;
import com.repertrack.model.*;
import com.repertrack.service.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Mini-runner de pruebas sin dependencias externas (sustituto de JUnit
 * para el prototipo). Cubre: RN-01, RN-02, RN-04, herencia, estadísticas y alertas.
 */
public class TestRunner {
    static int ok = 0, fail = 0;
    static void check(boolean cond, String nombre) {
        if (cond) { ok++; System.out.println("PASS " + nombre); }
        else { fail++; System.out.println("FAIL " + nombre); }
    }

    public static void main(String[] args) throws Exception {
        SesionService svc = new SesionService(new SesionDAO());
        // CP01: sesión válida
        int id = svc.registrarSesion(1, 2, 45, "ok");
        check(id > 0, "CP01 sesión válida persiste");
        // CP02: sesión huérfana (RN-01)
        try { svc.registrarSesion(0, 0, 10, "x"); check(false, "CP02 huérfana rechazada"); }
        catch (IllegalArgumentException e) { check(true, "CP02 huérfana rechazada"); }
        // CP03: tiempo cero (RN-02) — frontera 0/1
        try { svc.registrarSesion(1, 1, 0, "x"); check(false, "CP03 tiempo cero rechazado"); }
        catch (IllegalArgumentException e) { check(true, "CP03 tiempo cero rechazado"); }
        int id1 = svc.registrarSesion(1, 1, 1, "frontera 1 min");
        check(id1 > 0, "CP04 frontera 1 min aceptada");
        // CP05: comentario > 500 (RN-04)
        try { svc.registrarSesion(1, 1, 10, "x".repeat(501)); check(false, "CP05 comentario largo rechazado"); }
        catch (IllegalArgumentException e) { check(true, "CP05 comentario largo rechazado"); }
        // CP06: dificultad fuera de rango 1..10
        try { new Cancion(9, "X", 11, 1, "A", "C"); check(false, "CP06 dificultad inválida rechazada"); }
        catch (IllegalArgumentException e) { check(true, "CP06 dificultad inválida rechazada"); }
        // CP07: BPM fuera de rango 20..300
        try { new Ejercicio(9, "Y", 4, 1, "escala", 500); check(false, "CP07 BPM inválido rechazado"); }
        catch (IllegalArgumentException e) { check(true, "CP07 BPM inválido rechazado"); }
        // CP08: polimorfismo Cancion vs Ejercicio
        ElementoDeEstudio c = new Cancion(1, "Clair", 8, 2, "Debussy", "Db");
        ElementoDeEstudio e = new Ejercicio(2, "Pent", 4, 1, "escala", 120);
        check(Math.abs(c.calcularProgreso(480) - 0.5) < 1e-9, "CP08 progreso canción 480/960=0.5");
        check(Math.abs(e.calcularProgreso(120) - 0.5) < 1e-9, "CP09 progreso ejercicio 120/240=0.5");
        // CP10: estadísticas
        var sesiones = List.of(
            new SesionDePractica(1, 1, 2, LocalDateTime.now(), 45, ""),
            new SesionDePractica(2, 1, 2, LocalDateTime.now(), 15, ""));
        var est = new EstadisticaService();
        check(est.totalPorInstrumento(sesiones).get(1) == 60, "CP10 acumulado por instrumento=60");
        check(Math.abs(est.promedioPorSesion(sesiones) - 30.0) < 1e-9, "CP11 promedio=30");
        // CP12: alerta por inactividad (>30 días)
        var viejas = List.of(new SesionDePractica(1, 1, 5, LocalDateTime.now().minusDays(45), 20, ""));
        var al = new MotorAlertasService(30).calcularAlertas(viejas, Set.of(5));
        check(al.size() == 1, "CP12 alerta por 45 días sin práctica");
        // CP13: exportación a PDF válida (CU011 -> Sistema de Archivos)
        var ind = new java.util.LinkedHashMap<String, String>();
        ind.put("Sesiones registradas", "3");
        ind.put("Minutos totales", "135");
        java.io.File pdf = new ExportadorPdfService().exportarReporte(ind, java.io.File.createTempFile("repertrack-", ".pdf"));
        byte[] bytes = java.nio.file.Files.readAllBytes(pdf.toPath());
        String cabecera = new String(bytes, 0, Math.min(8, bytes.length), java.nio.charset.StandardCharsets.US_ASCII);
        String texto = new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1);
        check(cabecera.startsWith("%PDF-1.4") && texto.contains("ReperTrack") && bytes.length > 500, "CP13 PDF válido con estadísticas");
        pdf.delete();
        // CP14: sin indicadores no hay reporte
        try { new ExportadorPdfService().exportarReporte(new java.util.HashMap<>(), java.io.File.createTempFile("x-", ".pdf")); check(false, "CP14 reporte vacío rechazado"); }
        catch (IllegalArgumentException ex) { check(true, "CP14 reporte vacío rechazado"); }
        System.out.println("----\nResultado: " + ok + " PASS, " + fail + " FAIL");
        if (fail > 0) System.exit(1);
    }
}
