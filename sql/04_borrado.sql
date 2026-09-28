-- ReperTrack · 04_borrado.sql — orden hijo->padre por integridad referencial
USE repertrack;
DELETE FROM sesion_practica;
DELETE FROM elemento_estudio;
DELETE FROM instrumento;
DELETE FROM musico;
-- Verificación (deben devolver 0 filas):
SELECT * FROM sesion_practica;
SELECT * FROM elemento_estudio;
SELECT * FROM instrumento;
SELECT * FROM musico;
