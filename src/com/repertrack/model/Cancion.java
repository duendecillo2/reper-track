package com.repertrack.model;

import com.repertrack.excepcion.LongitudExcedidaException;
import com.repertrack.excepcion.RangoInvalidoException;

/**
 * Especialización: canción del repertorio.
 * Refactor TP3: asociación Instrumento + setters validados con checked exceptions.
 */
public class Cancion extends ElementoDeEstudio {

    private String autor;
    private String interpreteOriginal;
    private String tonalidad;
    private String compas;

    public Cancion() { super(); this.autor = ""; this.interpreteOriginal = ""; this.tonalidad = ""; this.compas = ""; }

    public Cancion(int id, Instrumento instrumento, String nombre, int dificultad,
                   int tiempoEstimadoDominioMin, String autor, String interpreteOriginal,
                   String tonalidad, String compas)
            throws RangoInvalidoException, LongitudExcedidaException {
        super(id, instrumento, nombre, dificultad, tiempoEstimadoDominioMin);
        setAutor(autor);
        setInterpreteOriginal(interpreteOriginal);
        setTonalidad(tonalidad);
        setCompas(compas);
    }

    /** Compatibilidad hacia atras (TestRunner original). */
    public Cancion(int id, String nombre, int dificultad, int instrumentoId,
                   String autor, String tonalidad)
            throws RangoInvalidoException, LongitudExcedidaException {
        super(id, nombre, dificultad, instrumentoId);
        setAutor(autor);
        setTonalidad(tonalidad);
    }

    @Override
    public String getCategoria() { return CategoriaElemento.CANCION.getValor(); }

    @Override
    public double calcularProgreso(int minutosAcumulados) {
        if (minutosAcumulados <= 0) return 0.0;
        int meta = getDificultad() * 120; // una canción difícil pide más horas
        if (meta == 0) return 0.0;
        return Math.min(1.0, minutosAcumulados / (double) meta);
    }

    public String getAutor() { return autor; }
    public void setAutor(String autor) throws LongitudExcedidaException {
        com.repertrack.util.Validador.exigirNoVacio("autor", autor);
        com.repertrack.util.Validador.exigirLongitudMaxima("autor", autor, 100);
        this.autor = autor.trim();
    }

    public String getInterpreteOriginal() { return interpreteOriginal; }

    public void setInterpreteOriginal(String i) throws LongitudExcedidaException {
        com.repertrack.util.Validador.exigirLongitudMaxima("interpreteOriginal", i, 100);
        this.interpreteOriginal = i == null ? "" : i.trim();
    }

    public String getTonalidad() { return tonalidad; }

    public void setTonalidad(String tonalidad) throws LongitudExcedidaException {
        com.repertrack.util.Validador.exigirLongitudMaxima("tonalidad", tonalidad, 20);
        this.tonalidad = tonalidad == null ? "" : tonalidad.trim();
    }

    public String getCompas() { return compas; }

    public void setCompas(String compas) throws LongitudExcedidaException {
        com.repertrack.util.Validador.exigirLongitudMaxima("compas", compas, 20);
        this.compas = compas == null ? "" : compas.trim();
    }
}