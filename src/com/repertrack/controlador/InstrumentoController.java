package com.repertrack.controlador;

import com.repertrack.excepcion.LongitudExcedidaException;
import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.model.Instrumento;
import com.repertrack.model.Musico;
import com.repertrack.model.TipoInstrumento;
import com.repertrack.service.InstrumentoService;

import java.util.Scanner;

/** Subcontrolador de Instrumentos (ABM). */
public class InstrumentoController {

    private final Scanner scanner;
    private final InstrumentoService service;
    private final Musico musicoActual;

    public InstrumentoController(Scanner scanner) {
        this.scanner = scanner;
        this.service = new InstrumentoService();
        this.musicoActual = new Musico();
        this.musicoActual.setId(1);
    }

    public void menu() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("+==============================================================+");
            System.out.println("|                  INSTRUMENTOS  (ABM)                         |");
            System.out.println("+==============================================================+");
            System.out.println("|  1) Alta   2) Listar   3) Modificar   4) Baja logica        |");
            System.out.println("|  0) Volver al menu principal                                  |");
            System.out.println("+==============================================================+");
            int op = leerEntero("Opcion");
            switch (op) {
                case 1: alta();        break;
                case 2: listar();      break;
                case 3: modificar();   break;
                case 4: bajaLogica();  break;
                case 0: volver = true; break;
                default: System.out.println("[WARN] Opcion invalida.");
            }
        }
    }

    private void alta() {
        try {
            Instrumento i = new Instrumento();
            i.setMusico(musicoActual);
            System.out.print("Nombre del instrumento: ");
            i.setNombre(scanner.nextLine());
            System.out.print("Tipo (cuerda/viento/percusion/teclado/otro): ");
            i.setTipo(TipoInstrumento.desde(scanner.nextLine()));
            System.out.print("Observaciones (opcional): ");
            i.setObservaciones(scanner.nextLine());
            service.alta(i);
            System.out.println("[OK] Instrumento creado con id=" + i.getId());
        } catch (LongitudExcedidaException | PersistenciaException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("[ERROR] Inesperado: " + ex.getMessage());
        }
    }

    private void listar() {
        try {
            var lista = service.listar();
            System.out.println("+--------------------------------------------------------------+");
            System.out.println("| ID  Nombre                  Tipo           Activo  Obs.       |");
            System.out.println("+--------------------------------------------------------------+");
            for (Instrumento i : lista) {
                System.out.printf("| %-3d %-22s %-13s %-6s | %s%n",
                    i.getId(), truncar(i.getNombre(), 22),
                    i.getTipo(), i.isActivo() ? "Si" : "No",
                    truncar(i.getObservaciones(), 9));
            }
            System.out.println("+--------------------------------------------------------------+");
        } catch (PersistenciaException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        }
    }

    private void modificar() {
        try {
            listar();
            int id = leerEntero("ID a modificar (0 cancela)");
            if (id <= 0) return;
            Instrumento i = service.obtener(id);
            if (i == null) { System.out.println("[ERROR] No existe el instrumento " + id); return; }
            System.out.print("Nuevo nombre (vacio = mantiene): ");
            String nombre = scanner.nextLine();
            if (!nombre.trim().isEmpty()) i.setNombre(nombre);
            System.out.print("Nuevo tipo (vacio = mantiene): ");
            String tipo = scanner.nextLine();
            if (!tipo.trim().isEmpty()) i.setTipo(TipoInstrumento.desde(tipo));
            System.out.print("Nueva observacion (vacio = mantiene): ");
            String obs = scanner.nextLine();
            if (!obs.trim().isEmpty()) i.setObservaciones(obs);
            service.modificacion(i);
            System.out.println("[OK] Instrumento modificado.");
        } catch (Exception ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        }
    }

    private void bajaLogica() {
        try {
            listar();
            int id = leerEntero("ID a dar de baja (0 cancela)");
            if (id <= 0) return;
            if (!confirmar("Confirma baja logica? (S/N)")) return;
            service.bajaLogica(id);
            System.out.println("[OK] Instrumento " + id + " dado de baja.");
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

    private boolean confirmar(String prompt) {
        System.out.print(prompt + ": ");
        String r = scanner.nextLine().trim().toUpperCase();
        return r.equals("S") || r.equals("SI") || r.equals("Y") || r.equals("YES");
    }

    private static String truncar(String s, int n) {
        if (s == null) return "";
        return s.length() <= n ? s : s.substring(0, n - 1) + "~";
    }
}