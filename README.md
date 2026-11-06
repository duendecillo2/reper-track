# ReperTrack — Sistema Integrado de Gestión de Repertorio y Práctica Musical

Prototipo Java 11 + MySQL 8 en **aplicación de consola**, sin frameworks de UI ni de persistencia.
**Entrega 4 (AP4) — Proyecto integrador.** Licenciatura en Informática, Universidad Siglo 21.
Estudiante: **Juan Martín Canguillen** (DNI 44933871).

## Stack

- Java 11 LTS
- MySQL 8.0 (InnoDB, `utf8mb4`)
- JDBC oficial (`mysql-connector-j` 8.3.0)
- Maven (build alternativo) o `javac` directo
- Consola pura (`Scanner` + `System.out`), sin Swing/JavaFX

## Estructura

```
src/
  Main.java                      # menú interactivo
  TestRunner.java                # 18 casos (CP01..CP18)
  com/repertrack/
    model/                       # entidades, enums, Estadistica (int[7]) y Alerta
    excepcion/                   # 7 excepciones checked (incluye ArchivoException)
    util/                        # ConexionDB (JDBC) y Validador
    dao/                         # interfaces + impls JDBC con fallback a memoria
    service/                     # reglas de negocio, PDF/TXT, estadísticas y alertas
    controlador/                 # menú y subcontroladores de consola
    estructuras/                 # lista enlazada, pila, cola, búsqueda y ordenación
    legacy/                      # VistaSesion Swing del TP2 (@Deprecated)
sql/
  01_creacion.sql                # DDL MySQL 8
  02_inserts.sql                 # datos de prueba
  03_consultas.sql               # Q1..Q5
  04_borrado.sql
  05_pruebas_integridad.sql      # 10 pruebas FK/UNIQUE/CHECK en MySQL
docs/
  diagramas/                     # 5 diagramas (.puml + .png)
  capturas/                      # 10 capturas reales de ejecución
  assets/logo-siglo21.png
  guion-video.md                 # storyboard del video de 3 minutos
CANGUILLEN-JUAN_MARTIN-AP4.PDF   # documento integrador
pom.xml
```

## Requisitos

- JRE/JDK 11 o superior (probado con JDK 25 compilando `--release 11`).
- MySQL 8.0 con el esquema `repertrack`.
- Conector `mysql-connector-j-8.3.0.jar` en el classpath.
- Sin servidor MySQL, los DAO caen a memoria y lo informan por consola.

## Base de datos

```bash
mysql -u root -p < sql/01_creacion.sql
mysql -u root -p < sql/02_inserts.sql
mysql -u root -p < sql/03_consultas.sql
mysql -u root -p < sql/05_pruebas_integridad.sql
```

Variables de entorno opcionales:

| Variable | Default |
|---|---|
| `REPER_DB_URL` | `jdbc:mysql://localhost:3306/repertrack` |
| `REPER_DB_USER` | `root` |
| `REPER_DB_PASS` | (vacío) |

## Compilar y ejecutar

```bash
# build con javac (verificado)
javac --release 11 -encoding UTF-8 -cp mysql-connector-j-8.3.0.jar \
      -d build $(find src -name '*.java')

# pruebas
java -cp build:mysql-connector-j-8.3.0.jar TestRunner

# menú
java -cp build:mysql-connector-j-8.3.0.jar Main

# con MySQL real
export REPER_DB_URL='jdbc:mysql://127.0.0.1:3306/repertrack?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export REPER_DB_USER=root
export REPER_DB_PASS='TU_CLAVE'
java -cp build:mysql-connector-j-8.3.0.jar Main
```

### Con Maven

```bash
mvn clean package
java -cp target/repertrack-app.jar:target/lib/* com.repertrack.Main
```

## Resultado de las pruebas

```
CP01..CP14  sesiones, elementos, polimorfismo, estadísticas, alertas y PDF
CP15        arreglo semanal int[7] (lunes 30, miércoles 45)
CP16        reporte TXT: escribe y vuelve a leer
CP17        lectura de archivo inexistente rechazada
CP18        reporte TXT vacío rechazado
----
Resultado: 18 PASS, 0 FAIL
```

## Características de la Entrega 4

| Requisito | Dónde se cumple |
|---|---|
| Patrón MVC | `model` (modelo), `controlador` (vista), `service` (control), `dao` (persistencia) |
| MySQL por JDBC | `ConexionDB` + `*DAOImpl` con `PreparedStatement`, `commit`/`rollback` |
| Arreglos + ArrayList | `ArrayList` en los DAO; `int[7]` en `Estadistica` para la distribución semanal |
| Archivos de texto | `ExportadorTextoService` (`BufferedWriter`/`BufferedReader`, UTF-8) |
| Excepciones | 7 checked; `try-catch` en controladores y servicios; `SQLException` → `PersistenciaException` |
| Clases abstractas | `ElementoDeEstudio` con `calcularProgreso(int)` abstracto |
| Interfaces | `InstrumentoDAO`, `ElementoDAO`, `SesionDAO` (con stub para tests) |
| Menú de selección | 9 opciones + submenús, `switch` |
| Colecciones propias | lista enlazada, pila, cola, búsqueda lineal y ordenación |
| Video | guion en `docs/guion-video.md`; enlace en el PDF (pendiente de grabar) |

## Evidencia

- `docs/capturas/01..10`: menú, ABM, sesión, estadísticas con arreglo, TXT, errores y MySQL.
- `docs/diagramas/`: dominio, clases, secuencia, despliegue y DER.
- `sql/05_pruebas_integridad.sql`: ejecutado en MySQL 8.0.40; 7 rechazos esperados (CHECK/FK/UNIQUE) y 1 INSERT válido.
- Documento final: `CANGUILLEN-JUAN_MARTIN-AP4.PDF` (A4, 90 páginas: TP1, TP2, TP3 y TP4 con sus portadas, más una nota de correcciones. Las correcciones y la versión con 18 casos están en el TP4).

## Repositorio

https://github.com/duendecillo2/reper-track


## Presentación para el video

`docs/presentacion/CANGUILLEN-JUAN_MARTIN-AP4-PRESENTACION.pdf` (9 diapositivas, 16:9) para acompañar el video de 3 minutos. El guion de grabación está en `docs/guion-video.md`.


## Video de presentación

https://youtu.be/8ptGI8U8vLI
