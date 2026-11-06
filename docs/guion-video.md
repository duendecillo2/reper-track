# Guion del video de presentación — ReperTrack (3 minutos)

**Objetivo:** mostrar el “core” del sistema funcionando contra MySQL real, no explicar todo el proyecto.
**Duración:** 2:50 a 3:10. **Audio:** obligatorio y claro. **Cámara:** opcional.
**Subida:** YouTube como *no listado* (o Drive/repo) y pegar el enlace en el PDF de la Entrega 4.

---

## Preparación (antes de grabar, no se graba)

1. Base de datos limpia y cargada:
   ```bash
   mysql -u root -p < sql/01_creacion.sql
   mysql -u root -p < sql/02_inserts.sql
   ```
2. Compilar:
   ```bash
   javac --release 11 -encoding UTF-8 -cp mysql-connector-j-8.3.0.jar \
         -d build $(find src -name '*.java')
   ```
3. Abrir **dos terminales** lado a lado:
   - Terminal A: el programa.
   - Terminal B: el cliente MySQL para mostrar la fila guardada.
4. Tener a mano el archivo `sql/02_inserts.sql` para mostrar los datos de prueba.
5. Fuente de la terminal en tamaño grande (16–18 pt) para que se lea en el video.

Comando de la Terminal A (ajustar usuario/clave/puerto a tu MySQL):

```bash
export REPER_DB_URL='jdbc:mysql://127.0.0.1:3306/repertrack?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export REPER_DB_USER=root
export REPER_DB_PASS='TU_CLAVE'
java -cp build:mysql-connector-j-8.3.0.jar Main
```

---

## Storyboard

### 0:00 – 0:15 · Presentación
**Pantalla:** terminal con el programa recién abierto (menú principal).
**Narración:**
> “Hola, soy Juan Martín Canguillen. Este es ReperTrack, un sistema de consola en Java 11 con MySQL 8 para gestionar repertorio y sesiones de práctica musical. En los próximos tres minutos voy a mostrar el flujo principal: alta de un instrumento, alta de una obra, registro de una sesión y verificación en la base de datos.”

### 0:15 – 0:40 · Menú y arquitectura
**Pantalla:** menú principal, señalar con el mouse las 9 opciones.
**Narración:**
> “El sistema está separado en Modelo, Vista y Controlador. La vista es este menú de consola; los controladores orquestan; los servicios aplican las reglas de negocio; y los DAO hablan con MySQL por JDBC, sin frameworks de persistencia.”

**Acción:** elegir `1` (Instrumentos).

### 0:40 – 1:15 · Alta de instrumento
**Pantalla:** submenú Instrumentos → `1) Alta`.
**Acción (teclear):**
```
1
Stratocaster 1979
cuerda
vintage
0
```
**Narración:**
> “Doy de alta una Stratocaster. El sistema valida el nombre y el tipo, y persiste el registro en la tabla `instrumento` con su clave foránea al músico. El `[OK] Instrumento creado con id=4` confirma el INSERT.”

### 1:15 – 1:50 · Alta de obra
**Pantalla:** menú → `2` (Elementos) → `1` (Alta).
**Acción (teclear):**
```
1
4
Sultans of Swing
1
8
120
Mark Knopfler
Dire Straits
D
4/4
0
```
**Narración:**
> “Ahora cargo una canción asociada a ese instrumento. La categoría `Canción` hereda de `ElementoDeEstudio` y agrega autor, intérprete, tonalidad y compás. La dificultad se valida entre 1 y 10; si pongo 11, el sistema la rechaza con una excepción controlada.”

(Opcional: mostrar el rechazo tecleando `11` y luego corregir; si el tiempo apremia, omitirlo.)

### 1:50 – 2:30 · Registrar sesión y verificar en MySQL
**Pantalla:** menú → `3` (Registrar Sesión) → `1`.
**Acción (teclear):**
```
3
1
4
6
1
40
probar el solo
0
```
**Narración:**
> “Registro una sesión de 40 minutos vinculada al instrumento 4 y a la obra 6. El servicio valida RN-01 (sin sesiones huérfanas) y RN-02 (tiempo mayor a cero), y el DAO ejecuta el INSERT dentro de una transacción: `setAutoCommit(false)`, `commit()` y `rollback()` ante cualquier error.”

**Cambiar a la Terminal B** y ejecutar:
```sql
mysql -u root -p -e "USE repertrack; SELECT sesion_id, instrumento_id, elemento_id, tiempo_neto_min, fecha_registro FROM sesion_practica ORDER BY sesion_id DESC LIMIT 3;"
```
**Narración:**
> “Acá está la fila que acaba de guardar el programa: sesión 5, instrumento 4, elemento 6, 40 minutos. La persistencia es real, no una maqueta en memoria.”

### 2:30 – 2:50 · Estadísticas y archivo de texto
**Pantalla:** volver a la Terminal A → `5` (Estadísticas).
**Narración:**
> “Las estadísticas se calculan desde las sesiones: acumulados por instrumento y por elemento, promedio y frecuencia semanal. La distribución por día se arma con un arreglo fijo de siete posiciones, complementario al `ArrayList` que devuelve el DAO.”

**Acción:** `8` (Exportar TXT) → ENTER; luego `9` (Leer TXT) → ENTER.
**Narración:**
> “Exporto el reporte a un archivo de texto y lo vuelvo a leer. El manejo de archivos usa `BufferedWriter` y `BufferedReader` con `try-with-resources`, y los errores de E/S se reportan con una excepción propia.”

### 2:50 – 3:00 · Cierre
**Pantalla:** `0` para salir; luego el repositorio en el navegador.
**Narración:**
> “El código completo, los scripts SQL y los diagramas están en el repositorio. Muchas gracias.”

---

## Checklist antes de subir

- [ ] El video dura entre 2:50 y 3:10.
- [ ] Se escucha la voz claramente (sin música que la tape).
- [ ] Se ve el menú, al menos un alta, el registro de sesión y la consulta a MySQL.
- [ ] Se ve el `[OK]` y, si se muestra, el `[ERROR]` de una validación.
- [ ] La resolución es 1080p o 720p y la letra se lee.
- [ ] Subido como *no listado*; enlace copiado.
- [ ] Enlace pegado en el PDF de la Entrega 4 (sección “Video de presentación”).

## Enlace del video

`[PEGAR AQUÍ EL ENLACE DEL VIDEO ANTES DE ENTREGAR]`
