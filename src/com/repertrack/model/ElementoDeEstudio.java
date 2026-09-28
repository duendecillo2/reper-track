package com.repertrack.model;

/**
 * Superclase abstracta del repertorio. Aplica herencia: una obra no se
 * estudia igual que un ejercicio técnico, y el modelo lo reconoce.
 */
public abstract class ElementoDeEstudio {
    protected int id;
    protected String nombre;
    protected int dificultad; // 1 a 10 (RN)
    protected int instrumentoId; // FK obligatoria al instrumento asociado
    protected boolean activo = true;

    protected ElementoDeEstudio(int id, String nombre, int dificultad, int instrumentoId) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre del elemento es obligatorio.");
        if (dificultad < 1 || dificultad > 10) throw new IllegalArgumentException("La dificultad debe estar entre 1 y 10.");
        this.id = id;
        this.nombre = nombre;
        this.dificultad = dificultad;
        this.instrumentoId = instrumentoId;
    }

    /** Progreso estimado 0..1 según minutos acumulados vs. una meta. Polimórfico. */
    public abstract double calcularProgreso(int minutosAcumulados);

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getDificultad() { return dificultad; }
    public int getInstrumentoId() { return instrumentoId; }
    public boolean isActivo() { return activo; }
    public void darDeBaja() { this.activo = false; }
}
