import com.repertrack.dao.SesionDAO;
import com.repertrack.service.EstadisticaService;
import com.repertrack.service.ExportadorPdfService;
import com.repertrack.service.MotorAlertasService;
import com.repertrack.service.SesionService;
import com.repertrack.ui.VistaSesion;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.SwingUtilities;

/** Punto de entrada del prototipo operacional ReperTrack. */
public class Main {
    public static void main(String[] args) throws Exception {
        SesionDAO dao = new SesionDAO();
        SesionService servicio = new SesionService(dao);
        // Datos de demostración (los mismos del script SQL de prueba)
        servicio.registrarSesion(1, 2, 45, "Pentatónica alternada");
        servicio.registrarSesion(2, 1, 60, "Clair de Lune fingerstyle");
        servicio.registrarSesion(1, 3, 30, "Little Wing");
        var sesiones = dao.listar();
        var est = new EstadisticaService();
        System.out.println("Sesiones: " + sesiones.size());
        System.out.println("Total por instrumento: " + est.totalPorInstrumento(sesiones));
        System.out.println("Total por elemento: " + est.totalPorElemento(sesiones));
        System.out.println("Promedio/sesión: " + est.promedioPorSesion(sesiones) + " min");
        var alertas = new MotorAlertasService(30).calcularAlertas(sesiones, java.util.Set.of(1, 2, 3));
        System.out.println("Alertas: " + alertas);
        Map<String, String> indicadores = new LinkedHashMap<>();
        indicadores.put("Sesiones registradas", String.valueOf(sesiones.size()));
        indicadores.put("Minutos PRS Custom 24", String.valueOf(est.totalPorInstrumento(sesiones).getOrDefault(1, 0)));
        indicadores.put("Minutos Acustica Taylor", String.valueOf(est.totalPorInstrumento(sesiones).getOrDefault(2, 0)));
        indicadores.put("Promedio por sesion (min)", String.valueOf(est.promedioPorSesion(sesiones)));
        var pdf = new ExportadorPdfService().exportarReporte(indicadores, new java.io.File("reporte-estadisticas.pdf"));
        System.out.println("Reporte PDF (CU011): " + pdf.getName() + " (" + pdf.length() + " bytes)");
        if (args.length > 0 && args[0].equals("--gui")) {
            SwingUtilities.invokeLater(() -> new VistaSesion(servicio).setVisible(true));
        }
    }
}
