package com.repertrack.model;

import java.time.LocalDateTime;

/** Sesión de práctica cronometrada. Entidad transaccional del sistema. */
public class SesionDePractica {
    private int id;
    private int instrumentoId;
    private int elementoId;
    private LocalDateTime fechaRegistro;
    private int tiempoNetoMin;
    private String comentario;

    public SesionDePractica(int id, int instrumentoId, int elementoId,
                            LocalDateTime fechaRegistro, int tiempoNetoMin, String comentario) {
        this.id = id;
        this.instrumentoId = instrumentoId;
        this.elementoId = elementoId;
        this.fechaRegistro = fechaRegistro;
        this.tiempoNetoMin = tiempoNetoMin;
        this.comentario = comentario;
    }

    /** Tiempo en minutos derivado del cronómetro (para CU006). */
    public static int calcularTiempo(long segundosAcumulados) {
        return (int) Math.max(0, segundosAcumulados / 60);
    }

    public int getId() { return id; }
    public int getInstrumentoId() { return instrumentoId; }
    public int getElementoId() { return elementoId; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public int getTiempoNetoMin() { return tiempoNetoMin; }
    public String getComentario() { return comentario; }
}
