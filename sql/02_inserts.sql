-- ReperTrack · 02_inserts.sql — datos de prueba (los mismos que usa Main.java)
USE repertrack;

INSERT INTO musico (musico_id, nombre, nivel, estilo) VALUES
(1, 'Juan Martin Canguillen', 'avanzado', 'popular')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO instrumento (instrumento_id, musico_id, nombre, tipo, observaciones) VALUES
(1, 1, 'Guitarra PRS Custom 24', 'cuerda', 'Eléctrica principal'),
(2, 1, 'Guitarra Acústica Taylor', 'cuerda', 'Para fingerstyle')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO elemento_estudio (elemento_id, instrumento_id, nombre, categoria, dificultad, autor, tonalidad, tipo_ejercicio, bpm_sugerido) VALUES
(1, 2, 'Clair de Lune (arreglo fingerstyle)', 'Cancion', 8, 'Debussy', 'Reb mayor', NULL, NULL),
(2, 1, 'Escala pentatónica alternada', 'Ejercicio', 4, NULL, NULL, 'escala', 120),
(3, 1, 'Little Wing - Jimi Hendrix', 'Cancion', 7, 'J. Hendrix', 'Mi mayor', NULL, NULL)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO sesion_practica (sesion_id, instrumento_id, elemento_id, fecha_registro, tiempo_neto_min, comentario) VALUES
(1, 1, 2, '2026-09-27 15:30:00', 45, 'Pentatónica alternada'),
(2, 2, 1, '2026-09-28 10:00:00', 60, 'Clair de Lune fingerstyle'),
(3, 1, 3, '2026-09-28 14:00:00', 30, 'Intro Little Wing');
