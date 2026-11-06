-- ReperTrack · 03_consultas.sql — consultas del prototipo (CU007 / CU009 / CU012)
USE repertrack;

-- Q1. Historial completo legible (JOIN de las 4 tablas), el más usado en el informe
SELECT SP.sesion_id AS id_sesion, M.nombre AS musico, I.nombre AS instrumento,
       EE.nombre AS elemento, EE.categoria AS categoria,
       SP.tiempo_neto_min AS minutos, SP.fecha_registro AS fecha
FROM sesion_practica SP
JOIN instrumento I ON SP.instrumento_id = I.instrumento_id
JOIN musico M ON I.musico_id = M.musico_id
JOIN elemento_estudio EE ON SP.elemento_id = EE.elemento_id
ORDER BY SP.fecha_registro DESC;

-- Q2. Tiempo total acumulado por instrumento (RFS08 / CU009)
SELECT I.nombre AS instrumento, SUM(SP.tiempo_neto_min) AS minutos_totales,
       COUNT(*) AS sesiones
FROM sesion_practica SP JOIN instrumento I ON SP.instrumento_id = I.instrumento_id
GROUP BY I.nombre ORDER BY minutos_totales DESC;

-- Q3. Tiempo total acumulado por elemento (RFS08 / CU009)
SELECT EE.nombre AS elemento, EE.categoria, SUM(SP.tiempo_neto_min) AS minutos_totales
FROM sesion_practica SP JOIN elemento_estudio EE ON SP.elemento_id = EE.elemento_id
GROUP BY EE.nombre, EE.categoria ORDER BY minutos_totales DESC;

-- Q4. Frecuencia semanal y promedio por sesión (RFS09 / CU009)
SELECT COUNT(*) AS sesiones_7d, COALESCE(AVG(tiempo_neto_min),0) AS promedio_min
FROM sesion_practica WHERE fecha_registro >= NOW() - INTERVAL 7 DAY;

-- Q5. Alertas de mantenimiento: elementos sin práctica hace >30 días (RFS10 / CU012)
SELECT EE.elemento_id, EE.nombre, MAX(SP.fecha_registro) AS ultima_practica,
       DATEDIFF(NOW(), MAX(SP.fecha_registro)) AS dias_sin_practica
FROM elemento_estudio EE LEFT JOIN sesion_practica SP ON SP.elemento_id = EE.elemento_id
WHERE EE.activo = 1
GROUP BY EE.elemento_id, EE.nombre
HAVING ultima_practica IS NULL OR DATEDIFF(NOW(), ultima_practica) > 30;
