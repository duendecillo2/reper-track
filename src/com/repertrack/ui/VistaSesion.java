package com.repertrack.ui;

import com.repertrack.service.SesionService;
import javax.swing.*;
import java.awt.*;

/**
 * Vista de registro de sesión (clase de interfaz <<VistaSesion>>).
 * Cronómetro con javax.swing.Timer + combos de instrumento/elemento.
 */
public class VistaSesion extends JFrame {
    private final SesionService servicio;
    private final JComboBox<String> cboInstrumento = new JComboBox<>(new String[]{"1 - PRS Custom 24", "2 - Acústica Taylor"});
    private final JComboBox<String> cboElemento = new JComboBox<>(new String[]{"1 - Clair de Lune", "2 - Pentatónica", "3 - Little Wing"});
    private final JLabel lblTiempo = new JLabel("00:00:00", SwingConstants.CENTER);
    private final Timer timer;
    private int segundos = 0;
    private boolean corriendo = false;

    public VistaSesion(SesionService servicio) {
        super("ReperTrack - Registrar Sesión (CU006)");
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
        JButton btnGuardar = new JButton("Guardar Sesión");
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
                    cboElemento.getSelectedIndex() + 1, minutos, "Sesión demo");
            JOptionPane.showMessageDialog(this, "Sesión " + id + " guardada (" + minutos + " min).");
            timer.stop(); segundos = 0; lblTiempo.setText(formato(0)); corriendo = false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String formato(int s) {
        return String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60);
    }
}
