package com.repertrack.controlador;

import com.repertrack.estructuras.ListaEnlazadaSimple;
import com.repertrack.excepcion.LongitudExcedidaException;
import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.excepcion.RangoInvalidoException;
import com.repertrack.excepcion.SesionSinReferenciaException;
import com.repertrack.model.ElementoDeEstudio;
import com.repertrack.model.Instrumento;
import com.repertrack.model.SesionDePractica;
import com.repertrack.service.ElementoService;
import com.repertrack.service.InstrumentoService;
import com.repertrack.service.SesionService;

import java.util.List;
import java.util.Scanner;

/** Subcontrolador de Sesiones (CU006). Reemplaza a VistaSesion (Swing) en TP3. */
public class SesionController {

    private final Scanner scanner;
    private final SesionService sesionService;
    private final InstrumentoService instrumentoService;
    private final ElementoService elementoService;
    private final ListaEnlazadaSimple<SesionDePractica> recientes;

    public SesionController(Scanner scanner) {
        this.scanner = scanner;
        this.sesionService = new SesionService();
        this.instrumentoService = new InstrumentoService();
        this.elementoService = new ElementoService();
        this.recientes = new ListaEnlazadaSimple<>();
    }

    public void menu() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("+==============================================================+");
            System.out.println("|              REGISTRAR SESION DE PRACTICA                    |");
            System.out.println("+==============================================================+");
            System.out.println("|  1) Registrar nueva sesion (manual o con cronometro)         |");
            System.out.println("|  2) Ver ultimas sesiones registradas                          |");
            System.out.println("|  0) Volver al menu principal                                  |");
            System.out.println("+==============================================================+");
            int op = leerEntero("Opcion");
            switch (op) {
                case 1: registrar();      break;
                case 2: listarRecientes(); break;
                case 0: volver = true;     break;
                default: System.out.println("[WARN] Opcion invalida.");
            }
        }
    }

    public void menuListar() { listarRecientes(); }

    private void registrar() {
        try {
            Instrumento inst = seleccionarInstrumento();
            if (inst == null) return;
            ElementoDeEstudio elem = seleccionarElemento(inst.getId());
            if (elem == null) return;

            System.out.println();
            System.out.println("Cronometro:");
            System.out.println("  1) Ingresar tiempo manualmente");
            System.out.println("  2) Usar cronometro en vivo");
            int modo = leerEntero("Modo (1 o 2)");

            int minutos = 0;
            if (modo == 2) {
                minutos = cronometrar();
                System.out.println("[OK] Cronometro finalizado: " + minutos + " min");
            } else {
                minutos = leerEntero("Tiempo neto (min, entero > 0)");
            }

            System.out.print("Comentario (opcional, max. 500 caracteres): ");
            String comentario = scanner.nextLine();

            SesionDePractica ses = new SesionDePractica(inst, elem, minutos, comentario);
            int id = sesionService.registrarSesion(inst, elem, minutos, comentario);
            ses.setId(id);
            recientes.agregarAlFinal(ses);
            System.out.println("[OK] Sesion registrada con id=" + ses.getId() + " (" + ses.getTiempoNetoMin() + " min, " + ses.getFechaFormateada() + ")");
        } catch (SesionSinReferenciaException | RangoInvalidoException | LongitudExcedidaException | PersistenciaException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("[ERROR] Inesperado: " + ex.getMessage());
        }
    }

    private int cronometrar() {
        long acumuladoMs = 0;
        long pausaInicio = 0;
        boolean enPausa = false;
        boolean seguir = true;
        while (seguir) {
            System.out.println("[I]niciar  [P]ausar/Reanudar  [F]inalizar  -> ");
            String op = scanner.nextLine().trim().toUpperCase();
            switch (op) {
                case "I":
                    if (pausaInicio == 0) { pausaInicio = System.currentTimeMillis(); enPausa = true; }
                    break;
                case "P":
                    if (enPausa) { acumuladoMs += System.currentTimeMillis() - pausaInicio; enPausa = false; pausaInicio = 0; }
                    else { pausaInicio = System.currentTimeMillis(); enPausa = true; }
                    break;
                case "F":
                    if (enPausa) acumuladoMs += System.currentTimeMillis() - pausaInicio;
                    seguir = false;
                    break;
                default: break;
            }
        }
        long segundos = acumuladoMs / 1000;
        return (int) Math.max(1, segundos / 60);
    }

    private Instrumento seleccionarInstrumento() {
        try {
            List<Instrumento> instrumentos = instrumentoService.listarActivos();
            if (instrumentos.isEmpty()) { System.out.println("[ERROR] No hay instrumentos activos. Cree uno primero."); return null; }
            System.out.println("Instrumentos disponibles:");
            for (Instrumento i : instrumentos) {
                System.out.println("  " + i.getId() + " - " + i.getNombre() + " (" + i.getTipo() + ")");
            }
            int id = leerEntero("ID del instrumento");
            if (id <= 0) return null;
            Instrumento inst = instrumentos.stream().filter(i -> i.getId() == id).findFirst().orElse(null);
            if (inst == null) { System.out.println("[ERROR] Instrumento invalido."); return null; }
            return inst;
        } catch (PersistenciaException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
            return null;
        }
    }

    private ElementoDeEstudio seleccionarElemento(int idInstrumento) {
        try {
            List<ElementoDeEstudio> elems = elementoService.listarPorInstrumento(idInstrumento);
            elems.removeIf(e -> !e.isActivo());
            if (elems.isEmpty()) { System.out.println("[ERROR] El instrumento no tiene elementos activos."); return null; }
            System.out.println("Elementos disponibles:");
            for (ElementoDeEstudio e : elems) {
                System.out.println("  " + e.getId() + " - " + e.getNombre() + " [" + e.getCategoria() + "]");
            }
            int id = leerEntero("ID del elemento");
            if (id <= 0) return null;
            ElementoDeEstudio el = elems.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
            if (el == null) { System.out.println("[ERROR] Elemento invalido."); return null; }
            return el;
        } catch (PersistenciaException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
            return null;
        }
    }

    private void listarRecientes() {
        try {
            List<SesionDePractica> sesiones = sesionService.listar();
            sesionService.ordenarPorFechaDesc(sesiones);
            System.out.println("+--------------------------------------------------------------+");
            System.out.println("| ID  Instrumento         Elemento              Min  Fecha       |");
            System.out.println("+--------------------------------------------------------------+");
            for (SesionDePractica s : sesiones) {
                String instN = s.getInstrumento() == null ? "?" : s.getInstrumento().getNombre();
                String elemN = s.getElemento() == null ? "?" : s.getElemento().getNombre();
                System.out.printf("| %-3d %-19s %-21s %-4d %-13s |%n",
                    s.getId(), truncar(instN, 19), truncar(elemN, 21),
                    s.getTiempoNetoMin(), s.getFechaFormateada());
            }
            System.out.println("+--------------------------------------------------------------+");
        } catch (PersistenciaException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        }
    }

    public int leerEntero(String campo) {
        while (true) {
            System.out.print(campo + ": ");
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) return -1;
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { System.out.println("[ERROR] Ingrese un numero entero valido."); }
        }
    }

    private static String truncar(String s, int n) {
        if (s == null) return "";
        return s.length() <= n ? s : s.substring(0, n - 1) + "~";
    }
}