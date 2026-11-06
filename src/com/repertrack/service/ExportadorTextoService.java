package com.repertrack.service;

import com.repertrack.excepcion.ArchivoException;
import com.repertrack.model.Estadistica;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Exporta y vuelve a leer el reporte de estadisticas como archivo de texto
 * plano UTF-8 (requisito de manejo de archivos de la consigna AP4).
 */
public class ExportadorTextoService {

    private static final String[] DIAS = {
        "lunes", "martes", "miercoles", "jueves", "viernes", "sabado", "domingo"
    };

    public void exportar(Estadistica est, String ruta) throws ArchivoException {
        if (est == null || est.estaVacia()) {
            throw new IllegalStateException("No hay datos para exportar (CU011).");
        }
        Path destino = Paths.get(ruta);
        try {
            if (destino.getParent() != null) Files.createDirectories(destino.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
                w.write("ReperTrack - Reporte de Estadisticas");
                w.newLine();
                w.write("Total de sesiones: " + est.getTotalSesiones());
                w.newLine();
                w.write("Frecuencia semanal (ult. 7 dias): " + est.getFrecuenciaSemanal());
                w.newLine();
                w.write(String.format("Promedio por sesion: %.1f min", est.getPromedioMinutosPorSesion()));
                w.newLine();
                w.newLine();
                w.write("Acumulado por instrumento (min):");
                w.newLine();
                for (Map.Entry<Integer, Long> kv : est.getMinutosPorInstrumento().entrySet()) {
                    w.write("  inst #" + kv.getKey() + " = " + kv.getValue());
                    w.newLine();
                }
                w.newLine();
                w.write("Distribucion semanal (min):");
                w.newLine();
                int[] minutos = est.getMinutosPorDiaSemana();
                for (int i = 0; i < DIAS.length; i++) {
                    w.write("  " + DIAS[i] + ": " + minutos[i]);
                    w.newLine();
                }
            }
        } catch (IOException ex) {
            throw new ArchivoException("No se pudo escribir el reporte de texto: " + ruta, ex);
        }
    }

    public String leer(String ruta) throws ArchivoException {
        Path origen = Paths.get(ruta);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = Files.newBufferedReader(origen, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = r.readLine()) != null) {
                sb.append(linea).append('\n');
            }
        } catch (IOException ex) {
            throw new ArchivoException("No se pudo leer el reporte de texto: " + ruta, ex);
        }
        return sb.toString();
    }
}
