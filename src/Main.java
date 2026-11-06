import com.repertrack.controlador.MenuPrincipal;

import java.util.Scanner;

/**
 * Punto de entrada del prototipo ReperTrack (TP3).
 *
 * Refactor TP3:
 *   - Arranca el menu de consola (MenuPrincipal).
 *   - Ya no usa Swing ni VistaSesion (esa quedo en com.repertrack.legacy como referencia historica).
 *   - Cierra el Scanner al salir.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Bienvenido a ReperTrack v1.0");
        try (Scanner scanner = new Scanner(System.in)) {
            MenuPrincipal menu = new MenuPrincipal(scanner);
            menu.iniciar();
        }
    }
}