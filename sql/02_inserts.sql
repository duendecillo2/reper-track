-- ============================================================
-- ReperTrack - Datos de prueba (refactor TP3)
--   - Mismos 3 instrumentos + 3 elementos + 3 sesiones que la version TP2,
--     pero adaptados al nuevo CHECK estricto (tipo 'percusion' sin tilde,
--     etc.). Las sesiones se cargan con NOW() para que el modulo de
--     estadisticas tenga datos 'recientes' al momento de probar.
-- ============================================================
USE repertrack;

INSERT INTO musico (musico_id, nombre, nivel, estilo) VALUES
  (1, 'Juan Martin Canguillen', 'avanzado', 'popular');

INSERT INTO instrumento (instrumento_id, musico_id, nombre, tipo, observaciones) VALUES
  (1, 1, 'PRS Custom 24',   'cuerda',  'guitarra electrica principal'),
  (2, 1, 'Taylor 814ce',    'cuerda',  'acustica para arreglos fingerstyle'),
  (3, 1, 'Piano digital Yamaha', 'teclado', NULL);

INSERT INTO elemento_estudio
  (elemento_id, instrumento_id, nombre, categoria, dificultad, autor, interprete_original, tonalidad, compas, tipo_ejercicio, bpm_sugerido)
VALUES
  (1, 1, 'Clair de Lune',      'Cancion',   8, 'C. Debussy', 'C. Debussy', 'Db',   '6/8', NULL,  NULL),
  (2, 1, 'Pentatonic alternate','Ejercicio', 4, NULL,        NULL,         NULL,   NULL, 'arpegio', 110),
  (3, 2, 'Little Wing',        'Cancion',   6, 'Jimi Hendrix','Jimi Hendrix','E',  '4/4', NULL,  NULL),
  (4, 1, 'Black Dog',          'Cancion',   7, 'J. Page / R. Plant','Led Zeppelin','A','4/4',NULL,NULL),
  (5, 3, 'Escala mayor 2 octavas','Ejercicio',3, NULL,      NULL,         NULL,   NULL, 'escala', 80);

INSERT INTO sesion_practica (instrumento_id, elemento_id, tiempo_neto_min, comentario, fecha_registro) VALUES
  (1, 1, 30, 'voicing del 2do tema',     NOW() - INTERVAL 1 DAY),
  (1, 2, 45, 'subiendo velocidad',       NOW()),
  (2, 3, 60, 'armonia de apertura',      NOW()),
  (1, 4, 30, 'riff principal',           NOW() - INTERVAL 45 DAY);