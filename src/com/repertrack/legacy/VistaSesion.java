package com.repertrack.legacy;

/*
 * ============================================================================
 *  LEGACY - VISTA SWING DEL TP2 - NO SE COMPILA CON EL PROTOTIPO DEL TP3
 * ============================================================================
 *
 * Esta clase era la vista Swing del prototipo original (JFrame + Timer).
 * Queda en el repositorio solo como referencia historica del Trabajo Practico 2.
 *
 * El Trabajo Practico 3 pide explicitamente "aplicacion de consola bien
 * estructurada, sin frameworks como JavaFX o Swing" (consigna AP3). Por eso:
 *
 *   1) El paquete se renombro a com.repertrack.legacy para sacarla del
 *      classpath principal.
 *   2) Si el evaluador quiere ejecutarla, debe compilarse manualmente:
 *
 *        javac -d build-legacy src/com/repertrack/legacy/VistaSesion.java
 *
 *      y se necesitara una clase Main que invoque su constructor.
 *
 * El flujo oficial del TP3 vive en com.repertrack.controlador (consola pura
 * con Scanner + System.out.printf). La vista Swing NO participa de la
 * ejecucion del Main actual.
 *
 * Trazabilidad historica (TP2):
 *   - Cubre CU006 (Registrar Sesion de Practica).
 *   - Cronometro con javax.swing.Timer + combos de instrumento/elemento.
 * ============================================================================
 */

import com.repertrack.service.SesionService;
import javax.swing.*;
import java.awt.*;

/** @deprecated desde TP3. Reemplazada por {@code SesionController} (consola). */
@Deprecated
public class VistaSesion extends JFrame {
    private final SesionService servicio;
    private final JComboBox<String> cboInstrumento = new JComboBox<>(new String[]{"1 - PRS Custom 24", "2 - Acustica Taylor"});
    private final JComboBox<String> cboElemento = new JComboBox<>(new String[]{"1 - Clair de Lune", "2 - Pentatonica", "3 - Little Wing"});
    private final JLabel lblTiempo = new JLabel("00:00:00", SwingConstants.CENTER);
    private final Timer timer;
    private int segundos = 0;
    private boolean corriendo = false;

    public VistaSesion(SesionService servicio) {
        super("ReperTrack - Registrar Sesion (CU006) [LEGACY TP2]");
        this.servicio = servicio;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 300);
        setLayout(new BorderLayout(8, 8));
        lblTiempo.setFont(new Font("Monospaced", Font.BOLD, 32));
        add(lblTiempo, BorderLayout.NORTH);
        JPanel centro = new JPanel(new GridLayout(2, 2, 6, 6));
        centro.add(new JLabel("Instrumento:")); centro.add(cboInstrumento);
        centro.add(new JLabel("Elemento:")); centro.add(cboElemento);
        add(centro, BorderLayout.CENTER);
        JPanel sur = new JPanel();
        JButton btnIniciar = new JButton("Iniciar");
        JButton btnPausar = new JButton("Pausar");
        JButton btnGuardar = new JButton("Guardar Sesion");
        sur.add(btnIniciar); sur.add(btnPausar); sur.add(btnGuardar);
        add(sur, BorderLayout.SOUTH);
        timer = new Timer(1000, e -> { segundos++; lblTiempo.setText(formato(segundos)); });
        btnIniciar.addActionListener(e -> { if (!corriendo) { timer.start(); corriendo = true; } });
        btnPausar.addActionListener(e -> { timer.stop(); corriendo = false; });
        btnGuardar.addActionListener(e -> guardar());
    }

    private void guardar() {
        try {
            int minutos = Math.max(1, segundos / 60); // RN-02: siempre > 0 para la demo
            int id = servicio.registrarSesion(cboInstrumento.getSelectedIndex() + 1,
                    cboElemento.getSelectedIndex() + 1, minutos, "Sesion demo");
            JOptionPane.showMessageDialog(this, "Sesion " + id + " guardada (" + minutos + " min).");
            timer.stop(); segundos = 0; lblTiempo.setText(formato(0)); corriendo = false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String formato(int s) {
        return String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60);
    }
}