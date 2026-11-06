package com.repertrack.controlador;

import com.repertrack.excepcion.ArchivoException;
import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.model.Estadistica;
import com.repertrack.service.EstadisticaService;
import com.repertrack.service.ExportadorPdfService;
import com.repertrack.service.ExportadorTextoService;

import java.io.IOException;
import java.util.Scanner;

/** Subcontrolador de Estadísticas + exportación PDF/TXT (CU009 / CU011). */
public class EstadisticaController {

    private static final String RUTA_PDF = "reportes/reporte-estadisticas.pdf";
    private static final String RUTA_TXT = "reportes/reporte-estadisticas.txt";
    private static final String[] DIAS = {
        "lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"
    };

    private final Scanner scanner;
    private final EstadisticaService service;
    private final ExportadorPdfService pdfService;
    private final ExportadorTextoService txtService;

    public EstadisticaController(Scanner scanner) {
        this.scanner = scanner;
        this.service = new EstadisticaService();
        this.pdfService = new ExportadorPdfService();
        this.txtService = new ExportadorTextoService();
    }

    public void menu() {
        try {
            Estadistica e = service.calcular();
            if (e.estaVacia()) { System.out.println("[WARN] Aún no hay sesiones registradas."); return; }
            System.out.println("+--------------------------------------------------------------+");
            System.out.println("|              ESTADÍSTICAS DE PROGRESO                        |");
            System.out.println("+--------------------------------------------------------------+");
            System.out.println("| Total de sesiones: " + e.getTotalSesiones());
            System.out.println("| Acumulado por instrumento (min):");
            e.getMinutosPorInstrumento().forEach((k, v) -> System.out.println("|    inst #" + k + " = " + v));
            System.out.println("| Acumulado por elemento (min):");
            e.getMinutosPorElemento().forEach((k, v) -> System.out.println("|    elem #" + k + " = " + v));
            System.out.println("| Frecuencia semanal (últ. 7 días): " + e.getFrecuenciaSemanal());
            System.out.printf("| Promedio por sesión: %.1f min%n", e.getPromedioMinutosPorSesion());
            imprimirDistribucionSemanal(e.getMinutosPorDiaSemana());
            System.out.println("+--------------------------------------------------------------+");
        } catch (PersistenciaException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        }
    }

    /** Distribución semanal usando el arreglo fijo int[7] de Estadistica. */
    private void imprimirDistribucionSemanal(int[] minutos) {
        int max = 0;
        for (int m : minutos) if (m > max) max = m;
        System.out.println("| Distribución semanal (min):");
        for (int i = 0; i < DIAS.length; i++) {
            int largo = max == 0 ? 0 : (int) Math.round(minutos[i] * 20.0 / max);
            StringBuilder barra = new StringBuilder();
            for (int j = 0; j < largo; j++) barra.append('#');
            System.out.printf("|   %-10s %-20s %d%n", DIAS[i], barra.toString(), minutos[i]);
        }
    }

    public void menuExportarPdf() {
        try {
            System.out.print("Ruta del archivo PDF (ENTER = " + RUTA_PDF + "): ");
            String ruta = scanner.nextLine().trim();
            if (ruta.isEmpty()) { ruta = RUTA_PDF; crearCarpetaReportes(); }
            Estadistica e = service.calcular();
            pdfService.exportar(e, ruta);
            System.out.println("[OK] Reporte PDF exportado a " + ruta);
            System.out.println("     Tamaño: " + new java.io.File(ruta).length() + " bytes");
        } catch (IllegalStateException ex) {
            System.out.println("[WARN] " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("[ERROR] No se pudo escribir el PDF: " + ex.getMessage());
        } catch (PersistenciaException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        }
    }

    public void menuExportarTxt() {
        try {
            System.out.print("Ruta del archivo TXT (ENTER = " + RUTA_TXT + "): ");
            String ruta = scanner.nextLine().trim();
            if (ruta.isEmpty()) { ruta = RUTA_TXT; crearCarpetaReportes(); }
            Estadistica e = service.calcular();
            txtService.exportar(e, ruta);
            System.out.println("[OK] Reporte TXT exportado a " + ruta);
            System.out.println("     Tamaño: " + new java.io.File(ruta).length() + " bytes");
        } catch (IllegalStateException ex) {
            System.out.println("[WARN] " + ex.getMessage());
        } catch (ArchivoException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        } catch (PersistenciaException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        }
    }

    public void menuLeerTxt() {
        System.out.print("Ruta del archivo TXT a leer (ENTER = " + RUTA_TXT + "): ");
        String ruta = scanner.nextLine().trim();
        if (ruta.isEmpty()) ruta = RUTA_TXT;
        try {
            String contenido = txtService.leer(ruta);
            System.out.println("+---------------- CONTENIDO DEL ARCHIVO ----------------+");
            System.out.print(contenido);
            System.out.println("+------------------------------------------------------+");
        } catch (ArchivoException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        }
    }

    private void crearCarpetaReportes() {
        java.io.File carpeta = new java.io.File("reportes");
        if (!carpeta.exists()) carpeta.mkdirs();
    }
}
