package com.repertrack.controlador;

import com.repertrack.excepcion.LongitudExcedidaException;
import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.excepcion.RangoInvalidoException;
import com.repertrack.model.Cancion;
import com.repertrack.model.ElementoDeEstudio;
import com.repertrack.model.Ejercicio;
import com.repertrack.model.Instrumento;
import com.repertrack.model.TipoEjercicio;
import com.repertrack.service.ElementoService;
import com.repertrack.service.InstrumentoService;

import java.util.List;
import java.util.Scanner;

/** Subcontrolador de Elementos (Canciones y Ejercicios). */
public class ElementoController {

    private final Scanner scanner;
    private final ElementoService service;
    private final InstrumentoService instrumentoService;

    public ElementoController(Scanner scanner) {
        this.scanner = scanner;
        this.service = new ElementoService();
        this.instrumentoService = new InstrumentoService();
    }

    public void menu() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("+==============================================================+");
            System.out.println("|            ELEMENTOS DE ESTUDIO  (ABM)                       |");
            System.out.println("+==============================================================+");
            System.out.println("|  1) Alta   2) Listar   3) Modificar   4) Baja logica        |");
            System.out.println("|  5) Deshacer ultimo alta    0) Volver                        |");
            System.out.println("+==============================================================+");
            int op = leerEntero("Opcion");
            switch (op) {
                case 1: alta();        break;
                case 2: listar();      break;
                case 3: modificar();   break;
                case 4: bajaLogica();  break;
                case 5: deshacer();    break;
                case 0: volver = true; break;
                default: System.out.println("[WARN] Opcion invalida.");
            }
        }
    }

    private void alta() {
        try {
            listarInstrumentos();
            int idInstrumento = leerEntero("ID del instrumento (0 cancela)");
            if (idInstrumento <= 0) return;
            Instrumento inst = instrumentoService.obtener(idInstrumento);
            if (inst == null) { System.out.println("[ERROR] No existe el instrumento " + idInstrumento); return; }
            System.out.print("Nombre del elemento: ");
            String nombre = scanner.nextLine();
            System.out.print("Categoria (1=Cancion, 2=Ejercicio): ");
            String cat = scanner.nextLine().trim();
            int dif = leerEntero("Dificultad (1-10)");
            int tdo = leerEntero("Tiempo estimado de dominio (min)");
            ElementoDeEstudio e;
            if ("1".equals(cat) || "cancion".equalsIgnoreCase(cat)) {
                Cancion c = new Cancion();
                c.setInstrumento(inst);
                c.setNombre(nombre);
                c.setDificultad(dif);
                c.setTiempoEstimadoDominioMin(tdo);
                System.out.print("Autor: "); c.setAutor(scanner.nextLine());
                System.out.print("Interprete original (opcional): "); c.setInterpreteOriginal(scanner.nextLine());
                System.out.print("Tonalidad (opcional): "); c.setTonalidad(scanner.nextLine());
                System.out.print("Compas (opcional): "); c.setCompas(scanner.nextLine());
                e = c;
            } else {
                Ejercicio x = new Ejercicio();
                x.setInstrumento(inst);
                x.setNombre(nombre);
                x.setDificultad(dif);
                x.setTiempoEstimadoDominioMin(tdo);
                System.out.print("Tipo de ejercicio (escala/arpegio/etude/patron ritmico): ");
                x.setTipoEjercicio(TipoEjercicio.desde(scanner.nextLine()));
                int bpm = leerEntero("Tempo sugerido (BPM, 20-300)");
                x.setBpmSugerido(bpm);
                e = x;
            }
            service.alta(e);
            System.out.println("[OK] Elemento creado con id=" + e.getId() + " (categoria=" + e.getCategoria() + ")");
        } catch (RangoInvalidoException | LongitudExcedidaException | PersistenciaException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("[ERROR] Inesperado: " + ex.getMessage());
        }
    }

    private void listar() {
        try {
            List<ElementoDeEstudio> todos = service.listar();
            System.out.println("+--------------------------------------------------------------+");
            System.out.println("| ID  Nombre               Categoria    Dif.   Instrumento      |");
            System.out.println("+--------------------------------------------------------------+");
            for (ElementoDeEstudio e : todos) {
                String instNombre = e.getInstrumento() == null ? "?" : e.getInstrumento().getNombre();
                System.out.printf("| %-3d %-20s %-12s %-5d %-17s |%n",
                    e.getId(), truncar(e.getNombre(), 20),
                    e.getCategoria(), e.getDificultad(), truncar(instNombre, 17));
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
            ElementoDeEstudio e = service.obtener(id);
            if (e == null) { System.out.println("[ERROR] No existe el elemento " + id); return; }
            System.out.print("Nuevo nombre (vacio=mantiene): ");
            String n = scanner.nextLine();
            if (!n.trim().isEmpty()) e.setNombre(n);
            int dif = leerEntero("Nueva dificultad (0=mantiene)");
            if (dif > 0) e.setDificultad(dif);
            if (e instanceof Cancion) {
                Cancion c = (Cancion) e;
                System.out.print("Nuevo autor (vacio=mantiene): ");
                String a = scanner.nextLine();
                if (!a.trim().isEmpty()) c.setAutor(a);
            } else if (e instanceof Ejercicio) {
                Ejercicio x = (Ejercicio) e;
                System.out.print("Nuevo tipo (vacio=mantiene): ");
                String t = scanner.nextLine();
                if (!t.trim().isEmpty()) x.setTipoEjercicio(TipoEjercicio.desde(t));
                int bpm = leerEntero("Nuevo BPM (0=mantiene)");
                if (bpm > 0) x.setBpmSugerido(bpm);
            }
            service.modificacion(e);
            System.out.println("[OK] Elemento modificado.");
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
            System.out.println("[OK] Elemento " + id + " dado de baja.");
        } catch (PersistenciaException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        }
    }

    private void deshacer() {
        if (!service.puedeDeshacer()) { System.out.println("[WARN] No hay altas pendientes de deshacer."); return; }
        ElementoDeEstudio e = service.deshacerUltimo();
        System.out.println("[INFO] Ultimo alta en pila: id=" + e.getId() + " nombre=" + e.getNombre() + " (no se persiste el undo).");
    }

    private void listarInstrumentos() {
        try {
            for (Instrumento i : instrumentoService.listar()) {
                System.out.println("  " + i.getId() + " - " + i.getNombre() + " (" + i.getTipo() + ")");
            }
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