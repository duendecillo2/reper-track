package com.repertrack.model;

/** Especialización: canción del repertorio. */
public class Cancion extends ElementoDeEstudio {
    private String autor;
    private String interpreteOriginal;
    private String tonalidad;
    private String compas;

    public Cancion(int id, String nombre, int dificultad, int instrumentoId,
                   String autor, String tonalidad) {
        super(id, nombre, dificultad, instrumentoId);
        this.autor = autor;
        this.tonalidad = tonalidad;
    }

    @Override
    public double calcularProgreso(int minutosAcumulados) {
        int meta = getDificultad() * 120; // una canción difícil pide más horas
        return Math.min(1.0, minutosAcumulados / (double) meta);
    }

    public String getAutor() { return autor; }
    public String getInterpreteOriginal() { return interpreteOriginal; }
    public void setInterpreteOriginal(String i) { this.interpreteOriginal = i; }
    public String getTonalidad() { return tonalidad; }
    public String getCompas() { return compas; }
    public void setCompas(String compas) { this.compas = compas; }
}
