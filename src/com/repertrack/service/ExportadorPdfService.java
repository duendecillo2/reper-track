package com.repertrack.service;

import com.repertrack.model.Estadistica;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Exporta el reporte de estadísticas a PDF (CU011 / RFS11), dirigido al
 * actor Sistema de Archivos. Generador mínimo sin dependencias externas:
 * escribe un PDF 1.4 válido con fuente base Helvetica (sin embedding).
 *
 * Refactor TP3:
 *   - Mantiene API antigua (exportarReporte con Map<String,String>) para compat.
 *   - Agrega exportar(Estadistica, String ruta) que toma el agregado del TP3.
 *   - Lanza IllegalStateException si los indicadores están vacios (RN-07).
 */
public class ExportadorPdfService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** API antigua (TP2). */
    public File exportarReporte(Map<String, String> indicadores, File destino) throws IOException {
        if (indicadores == null || indicadores.isEmpty())
            throw new IllegalArgumentException("No hay indicadores para exportar (CU009 vacio).");
        List<String> lineas = new ArrayList<>();
        lineas.add("ReperTrack - Reporte de estadisticas de progreso (CU011)");
        lineas.add("Generado por el prototipo operacional - Seminario de Practica");
        lineas.add("");
        for (Map.Entry<String, String> e : indicadores.entrySet())
            lineas.add(ascii(e.getKey()) + ": " + ascii(e.getValue()));
        Files.write(destino.toPath(), construirPdf(lineas));
        return destino;
    }

    /** API nueva (TP3). */
    public void exportar(Estadistica est, String ruta) throws IOException {
        if (est == null || est.estaVacia()) {
            throw new IllegalStateException("No hay datos para exportar (CU011 / RN-07).");
        }
        Path p = Paths.get(ruta);
        if (p.getParent() != null) Files.createDirectories(p.getParent());
        Files.write(p, construirPdf(from(est)));
    }

    /** API nueva (TP3) en memoria. */
    public byte[] exportarBytes(Estadistica est) throws IOException {
        if (est == null || est.estaVacia()) {
            throw new IllegalStateException("No hay datos para exportar (CU011 / RN-07).");
        }
        return construirPdf(from(est));
    }

    private List<String> from(Estadistica est) {
        List<String> lineas = new ArrayList<>();
        lineas.add("ReperTrack - Reporte de Estadisticas de Progreso");
        lineas.add("Emitido: " + LocalDateTime.now().format(FMT));
        lineas.add("");
        lineas.add("Total de sesiones: " + est.getTotalSesiones());
        lineas.add("");
        lineas.add("Acumulado por instrumento (min):");
        for (Map.Entry<Integer, Long> kv : est.getMinutosPorInstrumento().entrySet()) {
            lineas.add("  inst #" + kv.getKey() + " = " + kv.getValue() + " min");
        }
        lineas.add("");
        lineas.add("Acumulado por elemento (min):");
        for (Map.Entry<Integer, Long> kv : est.getMinutosPorElemento().entrySet()) {
            lineas.add("  elem #" + kv.getKey() + " = " + kv.getValue() + " min");
        }
        lineas.add("");
        lineas.add("Frecuencia semanal (ult. 7 dias): " + est.getFrecuenciaSemanal());
        lineas.add("Promedio por sesion: " + String.format("%.1f", est.getPromedioMinutosPorSesion()) + " min");
        return lineas;
    }

    private static String ascii(String s) {
        return s.replace('á','a').replace('é','e').replace('í','i')
                .replace('ó','o').replace('ú','u').replace('ñ','n')
                .replace('Á','A').replace('É','E').replace('Í','I')
                .replace('Ó','O').replace('Ú','U').replace('Ñ','N');
    }

    private static String escapar(String s) {
        return s.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    static byte[] construirPdf(List<String> lineas) throws IOException {
        StringBuilder contenido = new StringBuilder("BT /F1 12 Tf 50 770 Td 15 TL ");
        for (String l : lineas) {
            contenido.append('(').append(escapar(l.length() > 95 ? l.substring(0, 95) : l)).append(") Tj T* ");
        }
        contenido.append("ET");
        byte[] stream = contenido.toString().getBytes(StandardCharsets.ISO_8859_1);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();
        out.write("%PDF-1.4\n".getBytes(StandardCharsets.US_ASCII));
        String[] objs = {
            "<< /Type /Catalog /Pages 2 0 R >>",
            "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
            "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>",
            "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>"
        };
        for (int i = 0; i < objs.length; i++) {
            offsets.add(out.size());
            out.write(((i + 1) + " 0 obj\n" + objs[i] + "\nendobj\n").getBytes(StandardCharsets.US_ASCII));
        }
        offsets.add(out.size());
        out.write(("5 0 obj\n<< /Length " + stream.length + " >>\nstream\n").getBytes(StandardCharsets.US_ASCII));
        out.write(stream);
        out.write("\nendstream\nendobj\n".getBytes(StandardCharsets.US_ASCII));
        int xref = out.size();
        out.write(("xref\n0 6\n0000000000 65535 f \n").getBytes(StandardCharsets.US_ASCII));
        for (int off : offsets) out.write(String.format("%010d 00000 n \n", off).getBytes(StandardCharsets.US_ASCII));
        out.write(("trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF").getBytes(StandardCharsets.US_ASCII));
        return out.toByteArray();
    }
}