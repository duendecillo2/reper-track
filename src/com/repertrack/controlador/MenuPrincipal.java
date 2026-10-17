package com.repertrack.controlador;

import java.util.Scanner;

/**
 * Menu principal del prototipo TP3 (consola pura, sin frameworks de UI).
 * Despacha a los subcontroladores por numero de opcion con un switch clasico.
 */
public class MenuPrincipal {

    private final Scanner scanner;
    private final InstrumentoController instrumentoController;
    private final ElementoController   elementoController;
    private final SesionController     sesionController;
    private final EstadisticaController estadisticaController;
    private final AlertaController     alertaController;

    public MenuPrincipal(Scanner scanner) {
        this.scanner = scanner;
        this.instrumentoController = new InstrumentoController(scanner);
        this.elementoController   = new ElementoController(scanner);
        this.sesionController     = new SesionController(scanner);
        this.estadisticaController = new EstadisticaController(scanner);
        this.alertaController     = new AlertaController(scanner);
    }

    public void iniciar() {
        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            int opcion = leerEntero("Opcion");
            switch (opcion) {
                case 1: instrumentoController.menu();   break;
                case 2: elementoController.menu();     break;
                case 3: sesionController.menu();       break;
                case 4: sesionController.menuListar(); break;
                case 5: estadisticaController.menu();  break;
                case 6: alertaController.menu();       break;
                case 7: estadisticaController.menuExportarPdf(); break;
                case 0: salir = true;                   break;
                default: System.out.println("[WARN] Opcion invalida. Intente nuevamente.");
            }
        }
        System.out.println("Gracias por usar ReperTrack. Hasta luego.");
    }

    public void mostrarMenu() {
        System.out.println();
        System.out.println("+==============================================================+");
        System.out.println("|                    REPERTRACK v1.0  (consola)                |");
        System.out.println("|        Sistema de Gestion de Repertorio y Practica            |");
        System.out.println("+==============================================================+");
        System.out.println("|  1) Instrumentos                                              |");
        System.out.println("|  2) Elementos de Estudio                                      |");
        System.out.println("|  3) Registrar Sesion de Practica                              |");
        System.out.println("|  4) Listar / Filtrar Sesiones                                 |");
        System.out.println("|  5) Estadisticas de Progreso                                  |");
        System.out.println("|  6) Alertas de Mantenimiento                                  |");
        System.out.println("|  7) Exportar Reporte a PDF (CU011)                            |");
        System.out.println("|  0) Salir                                                     |");
        System.out.println("+==============================================================+");
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
}