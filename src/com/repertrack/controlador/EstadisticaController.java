package com.repertrack.controlador;

import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.model.Estadistica;
import com.repertrack.service.ExportadorPdfService;
import com.repertrack.service.EstadisticaService;

import java.io.IOException;
import java.util.Scanner;

/** Subcontrolador de Estadisticas + Exportacion PDF (CU009 / CU011). */
public class EstadisticaController {

    private final Scanner scanner;
    private final EstadisticaService service;
    private final ExportadorPdfService pdfService;

    public EstadisticaController(Scanner scanner) {
        this.scanner = scanner;
        this.service = new EstadisticaService();
        this.pdfService = new ExportadorPdfService();
    }

    public void menu() {
        try {
            Estadistica e = service.calcular();
            if (e.estaVacia()) { System.out.println("[WARN] Aun no hay sesiones registradas."); return; }
            System.out.println("+--------------------------------------------------------------+");
            System.out.println("|              ESTADISTICAS DE PROGRESO                         |");
            System.out.println("+--------------------------------------------------------------+");
            System.out.println("| Total de sesiones: " + e.getTotalSesiones());
            System.out.println("| Acumulado por instrumento (min):");
            e.getMinutosPorInstrumento().forEach((k, v) -> System.out.println("|    inst #" + k + " = " + v));
            System.out.println("| Acumulado por elemento (min):");
            e.getMinutosPorElemento().forEach((k, v) -> System.out.println("|    elem #" + k + " = " + v));
            System.out.println("| Frecuencia semanal (ult. 7 dias): " + e.getFrecuenciaSemanal());
            System.out.printf("| Promedio por sesion: %.1f min%n", e.getPromedioMinutosPorSesion());
            System.out.println("+--------------------------------------------------------------+");
        } catch (PersistenciaException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        }
    }

    public void menuExportarPdf() {
        try {
            System.out.print("Ruta del archivo PDF (ENTER = reportes/reporte-estadisticas.pdf): ");
            String ruta = scanner.nextLine().trim();
            if (ruta.isEmpty()) {
                ruta = "reportes/reporte-estadisticas.pdf";
                java.io.File carpeta = new java.io.File("reportes");
                if (!carpeta.exists()) carpeta.mkdirs();
            }
            Estadistica e = service.calcular();
            pdfService.exportar(e, ruta);
            System.out.println("[OK] Reporte PDF exportado a " + ruta);
            java.io.File f = new java.io.File(ruta);
            System.out.println("     Tamano: " + f.length() + " bytes");
        } catch (IllegalStateException ex) {
            System.out.println("[WARN] " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("[ERROR] No se pudo escribir el PDF: " + ex.getMessage());
        } catch (PersistenciaException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        }
    }
}