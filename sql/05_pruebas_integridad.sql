-- ============================================================
-- ReperTrack - Pruebas de integridad en MySQL 8
-- Cada prueba debe FALLAR (esperado) o tener exito (esperado).
-- NO se ejecuta contra SQLite (no enforza CHECK antes de 3.32 y
-- no soporta ENGINE=InnoDB ni ON UPDATE/DELETE CASCADE).
-- ============================================================
USE repertrack;

-- 0) Datos base
INSERT INTO musico (musico_id, nombre, nivel, estilo)
 VALUES (1, 'Juan', 'avanzado', 'popular');

INSERT INTO instrumento (instrumento_id, musico_id, nombre, tipo)
  VALUES (1, 1, 'PRS Custom 24', 'cuerda');

INSERT INTO elemento_estudio (elemento_id, instrumento_id, nombre,
 categoria, dificultad, autor)
  VALUES (1, 1, 'Clair de Lune', 'Cancion', 8, 'Debussy');

-- PRUEBA 1: CHECK de tiempo_neto_min > 0  (debe FALLAR)
INSERT INTO sesion_practica (instrumento_id, elemento_id, tiempo_neto_min)
  VALUES (1, 1, 0);

-- PRUEBA 2: CHECK de dificultad BETWEEN 1 AND 10  (debe FALLAR)
INSERT INTO elemento_estudio (instrumento_id, nombre, categoria,
  dificultad, autor)
  VALUES (1, 'X', 'Cancion', 11, 'Y');

-- PRUEBA 3: CHECK de BPM BETWEEN 20 AND 300  (debe FALLAR)
INSERT INTO elemento_estudio (instrumento_id, nombre, categoria,
  dificultad, tipo_ejercicio, bpm_sugerido)
  VALUES (1, 'Y', 'Ejercicio', 4, 'arpegio', 19);

-- PRUEBA 4: FK inexistente en sesion_practica  (debe FALLAR)
INSERT INTO sesion_practica (instrumento_id, elemento_id, tiempo_neto_min)
  VALUES (9999, 1, 30);

-- PRUEBA 5: UNIQUE (instrumento_id, nombre)  (debe FALLAR)
INSERT INTO instrumento (musico_id, nombre, tipo)
  VALUES (1, 'PRS Custom 24', 'cuerda');

-- PRUEBA 6: DELETE de musico con instrumentos  (debe FALLAR por FK)
DELETE FROM musico WHERE musico_id = 1;

-- PRUEBA 7: comentario > 500 chars  (debe FALLAR por CHECK)
INSERT INTO sesion_practica (instrumento_id, elemento_id, tiempo_neto_min,
  comentario)
  VALUES (1, 1, 30, REPEAT('x', 501));

-- PRUEBA 8: INSERT valido  (debe PASAR)
INSERT INTO sesion_practica (instrumento_id, elemento_id, tiempo_neto_min,
  comentario)
  VALUES (1, 1, 30, 'ensayando cambios de voicing');

-- PRUEBA 9: verificacion por conteo
SELECT
  (SELECT COUNT(*) FROM musico)            AS musicos,
  (SELECT COUNT(*) FROM instrumento)       AS instrumentos,
  (SELECT COUNT(*) FROM elemento_estudio)  AS elementos,
  (SELECT COUNT(*) FROM sesion_practica)   AS sesiones;

-- PRUEBA 10: limpieza
DELETE FROM sesion_practica;
DELETE FROM elemento_estudio;
DELETE FROM instrumento;
DELETE FROM musico;