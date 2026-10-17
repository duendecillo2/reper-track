# ReperTrack — Sistema Integrado de Gestión de Repertorio y Práctica Musical

Prototipo Java 11 + MySQL 8 en **aplicación de consola** (sin frameworks de UI).
Trabajo Práctico 3 — Licenciatura en Informática, Universidad Siglo 21.
Estudiante: **Juan Martín Canguillen** (DNI 44933871).

## Stack

- **Java 11 LTS**
- **MySQL 8.0** (InnoDB, `utf8mb4`)
- **JDBC oficial** (`mysql-connector-j` 8.3.0)
- **Maven** (build)
- **Consola pura** (sin Swing/JavaFX)

## Estructura

```
src/
  com/repertrack/
    modelo/                    # Capa Modelo (POO pura)
      Musico.java
      Instrumento.java
      ElementoDeEstudio.java   (abstract)
      Cancion.java             (extends ElementoDeEstudio)
      Ejercicio.java           (extends ElementoDeEstudio)
      SesionDePractica.java
      Estadistica.java
      Alerta.java
      Nivel.java TipoInstrumento.java TipoEjercicio.java CategoriaElemento.java
    excepcion/                 # 6 excepciones checked
      RangoInvalidoException.java LongitudExcedidaException.java
      SesionSinReferenciaException.java ElementoInexistenteException.java
      InstrumentoInexistenteException.java PersistenciaException.java
    util/
      ConexionDB.java          # JDBC + deteccion de disponibilidad
      Validador.java           # Disparador central de excepciones
    dao/                       # Interfaces + impls JDBC/memoria
      InstrumentoDAO.java InstrumentoDAOImpl.java
      ElementoDAO.java ElementoDAOImpl.java
      SesionDAO.java SesionDAOImpl.java
    service/
      InstrumentoService.java ElementoService.java SesionService.java
      EstadisticaService.java MotorAlertasService.java ExportadorPdfService.java
    controlador/               # Capa Vista (consola)
      MenuPrincipal.java
      InstrumentoController.java ElementoController.java
      SesionController.java EstadisticaController.java AlertaController.java
    estructuras/               # Estructuras de datos + algoritmos
      ListaEnlazadaSimple.java Pila.java Cola.java
      Busqueda.java Ordenacion.java
    legacy/                    # Codigo del TP2 (no se compila en TP3)
      VistaSesion.java         (Swing JFrame, deprecated)
  Main.java
  TestRunner.java
sql/
  01_creacion.sql              # DDL puro MySQL 8
  02_inserts.sql               # Datos de prueba
  03_consultas.sql             # Q1..Q5 (CU007/CU009/CU012)
  04_borrado.sql
  05_pruebas_integridad.sql    # 10 pruebas FK / UNIQUE / CHECK
docs/
  diagramas/                   # PNGs del TP2 (trazabilidad historica)
pom.xml
```

## Compilación y ejecución

### Con Maven (recomendado)

```bash
mvn clean package
java -cp target/repertrack-app.jar:target/lib/* com.repertrack.Main
java -cp target/repertrack-app.jar:target/lib/* com.repertrack.TestRunner
```

### Sin Maven (verificado con ECJ 3.37)

```bash
java -jar ecj.jar -source 11 -target 11 -d build $(find src -name '*.java')
java -cp build:mysql-connector-j-8.3.0.jar com.repertrack.TestRunner
java -cp build:mysql-connector-j-8.3.0.jar com.repertrack.Main
```

## Base de datos

```bash
mysql -u root -p < sql/01_creacion.sql
mysql -u root -p < sql/02_inserts.sql
mysql -u root -p < sql/03_consultas.sql
mysql -u root -p < sql/05_pruebas_integridad.sql   # 10 pruebas FK/UNIQUE/CHECK
```

### Variables de entorno opcionales

| Variable | Default | Descripción |
|---|---|---|
| `REPER_DB_URL` | `jdbc:mysql://localhost:3306/repertrack` | URL JDBC |
| `REPER_DB_USER` | `root` | Usuario |
| `REPER_DB_PASS` | (vacío) | Password |

Si MySQL no responde, los DAOs hacen **fallback automático a memoria** (lo informan por consola con prefijo `[WARN]`).

## Resultado del TestRunner

```
===== ReperTrack - TestRunner (14 casos) =====
PASS CP01 sesion valida persiste
PASS CP02 hurfana rechazada
PASS CP03 tiempo cero rechazado
PASS CP04 frontera 1 min aceptada
PASS CP05 comentario largo rechazado
PASS CP06 dificultad invalida rechazada
PASS CP07 BPM invalido rechazado
PASS CP08 progreso cancion 480/960=0.5
PASS CP09 progreso ejercicio 120/240=0.5
PASS CP10 acumulado por instrumento=75
PASS CP11 promedio=45.0
PASS CP12 alerta por 45 dias sin practica
PASS CP13 PDF valido con estadisticas
PASS CP14 reporte vacio rechazado
----
Resultado: 14 PASS, 0 FAIL
```

## Trazabilidad contra la rúbrica del TP3

| Característica | Dónde se cumple |
|---|---|
| Encapsulamiento | Atributos `private` + getters/setters con `Validador` en `modelo/` |
| Herencia | `ElementoDeEstudio` (abstract) ← `Cancion`, `Ejercicio` |
| Polimorfismo | `calcularProgreso()` redefinido en `Cancion` (×120) y `Ejercicio` (×60) |
| Abstracción | `ElementoDeEstudio` abstract + DAO interfaces |
| Menú de selección | `MenuPrincipal` con `switch` |
| Condicionales / repetitivas | `if`/`switch` en controladores y DAOs; `for`/`while` en listados, alertas, ordenación |
| Excepciones | 6 personalizadas en `excepcion/` + `try-catch` en cada servicio/controlador |
| Constructores | Vacío + completo en cada clase del modelo |
| Colecciones | `ListaEnlazadaSimple`, `Pila`, `Cola` en `estructuras/` |
| Algoritmos | `Busqueda.lineal` + `Ordenacion.seleccion` / `Ordenacion.burbuja` |
| Persistencia JDBC | `ConexionDB` + `PreparedStatement` + transacciones ACID |
| Consola pura | `MenuPrincipal` + `Scanner` + `System.out.printf` (sin Swing/JavaFX) |

## Histórico

- **TP1** — Inicio del PUD: problemática, objetivos, modelo de dominio, CU006.
- **TP2** — Análisis y diseño completos: clases de diseño, DER, prototipo con Swing.
- **TP3 (esta entrega)** — Migración a consola, refactor del modelo (asociaciones en vez de IDs), 6 excepciones personalizadas, 3 DAOs, menú interactivo, 14/14 PASS, DER con tipos SQL, script de pruebas de integridad.

El archivo `VistaSesion.java` del TP2 se conserva en `com/repertrack/legacy/` como referencia histórica (marcado `@Deprecated`, renombrado el paquete para sacarlo del classpath principal).