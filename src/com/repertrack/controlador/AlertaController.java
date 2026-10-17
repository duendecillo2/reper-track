package com.repertrack.controlador;

import com.repertrack.estructuras.Cola;
import com.repertrack.excepcion.PersistenciaException;
import com.repertrack.model.Alerta;
import com.repertrack.service.MotorAlertasService;

import java.util.Scanner;

/** Subcontrolador de Alertas (CU012) - drena la Cola FIFO. */
public class AlertaController {

    private final Scanner scanner;
    private final MotorAlertasService service;

    public AlertaController(Scanner scanner) {
        this.scanner = scanner;
        this.service = new MotorAlertasService();
    }

    public void menu() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("+==============================================================+");
            System.out.println("|          ALERTAS DE MANTENIMIENTO (umbral: 30 dias)          |");
            System.out.println("+==============================================================+");
            System.out.println("|  1) Ver alertas   2) Cambiar umbral   0) Volver             |");
            System.out.println("+==============================================================+");
            int op = leerEntero("Opcion");
            switch (op) {
                case 1: verAlertas();    break;
                case 2: cambiarUmbral(); break;
                case 0: volver = true;   break;
                default: System.out.println("[WARN] Opcion invalida.");
            }
        }
    }

    private void verAlertas() {
        try {
            Cola<Alerta> cola = service.calcular();
            if (cola.estaVacia()) { System.out.println("[OK] Sin alertas: todos los elementos fueron practicados recientemente."); return; }
            System.out.println("+--------------------------------------------------------------+");
            System.out.println("| Elemento                 Ultima practica    Dias   Motivo    |");
            System.out.println("+--------------------------------------------------------------+");
            while (!cola.estaVacia()) {
                Alerta a = cola.desencolar();
                String ult = a.getUltimaPractica() == null ? "(sin sesiones)" : a.getUltimaPractica().toString();
                System.out.printf("| %-23s %-18s %-6d %-9s |%n",
                    truncar(a.getNombreElemento(), 23), ult, a.getDiasSinPractica(),
                    truncar(a.getMotivo(), 9));
            }
            System.out.println("+--------------------------------------------------------------+");
        } catch (PersistenciaException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        }
    }

    private void cambiarUmbral() {
        int n = leerEntero("Nuevo umbral (dias, > 0)");
        if (n <= 0) { System.out.println("[WARN] Valor invalido."); return; }
        service.setUmbralDias(n);
        System.out.println("[OK] Umbral actualizado a " + n + " dias.");
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