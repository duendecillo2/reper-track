package com.repertrack.model;

import java.time.LocalDate;

/** Alerta generada por MotorAlertasService (CU012). */
public class Alerta {

    private int elementoId;
    private String nombreElemento;
    private LocalDate ultimaPractica;
    private int diasSinPractica;
    private String motivo;

    public Alerta() {}

    public Alerta(int elementoId, String nombreElemento, LocalDate ultimaPractica,
                  int diasSinPractica, String motivo) {
        this.elementoId = elementoId;
        this.nombreElemento = nombreElemento;
        this.ultimaPractica = ultimaPractica;
        this.diasSinPractica = diasSinPractica;
        this.motivo = motivo;
    }

    public int getElementoId() { return elementoId; }
    public void setElementoId(int id) { this.elementoId = id; }

    public String getNombreElemento() { return nombreElemento; }
    public void setNombreElemento(String n) { this.nombreElemento = n; }

    public LocalDate getUltimaPractica() { return ultimaPractica; }
    public void setUltimaPractica(LocalDate u) { this.ultimaPractica = u; }

    public int getDiasSinPractica() { return diasSinPractica; }
    public void setDiasSinPractica(int d) { this.diasSinPractica = d; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String m) { this.motivo = m; }

    @Override
    public String toString() {
        return String.format("Alerta{elemento=%s, dias=%d, motivo='%s'}",
                nombreElemento, diasSinPractica, motivo);
    }
}