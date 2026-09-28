# ReperTrack — Sistema Integrado de Gestión de Repertorio y Práctica Musical

Prototipo operacional del Trabajo Práctico 2 (PUD completo) — Licenciatura en Informática, Universidad Siglo 21.
Estudiante: Juan Martín Canguillen (DNI 44933871).

## Estructura

- `src/com/repertrack/model/` — Musico, Instrumento, ElementoDeEstudio (abstracta), Cancion, Ejercicio, SesionDePractica
- `src/com/repertrack/dao/` — SesionDAO (JDBC transaccional + réplica en memoria para demo sin servidor)
- `src/com/repertrack/service/` — SesionService (RN-01..RN-05), EstadisticaService (CU009), MotorAlertasService (CU012), ExportadorPdfService (CU011)
- `src/com/repertrack/ui/` — VistaSesion (Swing: combos + cronómetro, CU006)
- `src/com/repertrack/util/` — ConexionDB (`jdbc:mysql://localhost:3306/repertrack`)
- `src/Main.java` — demo con 3 sesiones (135 min, promedio 45.0)
- `src/TestRunner.java` — 14 casos (equivalencia + frontera), sin dependencias externas
- `sql/` — 01_creacion.sql · 02_inserts.sql · 03_consultas.sql (Q1–Q5) · 04_borrado.sql
- `docs/diagramas/` — fig7_secuencia, fig8_clases, fig9_despliegue, fig10_der (+ scripts generadores)
- `../CANGUILLEN-JUAN_MARTIN-AP2.pdf` — informe de la entrega

## Requisitos

JRE 11+ y MySQL 8.0 (esquema `repertrack`). Sin servidor MySQL el prototipo igual demuestra el flujo en memoria.

## Compilar y correr (verificado con ECJ 3.37, nivel 11: 14 PASS, 0 FAIL)

```bash
java -jar ecj.jar -source 11 -target 11 -d build $(find src -name '*.java')
java -cp build TestRunner
java -cp build Main
java -cp build Main --gui   # abre la VistaSesion Swing
```

## Base de datos

```bash
mysql -u root < sql/01_creacion.sql
mysql -u root < sql/02_inserts.sql
mysql -u root < sql/03_consultas.sql
mysql -u root < sql/04_borrado.sql
```
