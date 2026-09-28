-- ReperTrack · 01_creacion.sql — DDL normalizado (1FN–3FN), MySQL 8.0
-- Decisión: herencia de Cancion/Ejercicio con single-table inheritance en
-- elemento_estudio (columna categoria + columnas específicas nulables).
-- Evita joins por cada lectura y refleja que ambas son "elementos" para la sesión.
CREATE DATABASE IF NOT EXISTS repertrack
  DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_0900_ai_ci;
USE repertrack;

CREATE TABLE musico (
    musico_id      INT PRIMARY KEY AUTO_INCREMENT,
    nombre         VARCHAR(255) NOT NULL,
    nivel          VARCHAR(50) NOT NULL DEFAULT 'intermedio',
    estilo         VARCHAR(50) NULL,
    CONSTRAINT chk_nivel CHECK (nivel IN ('principiante','intermedio','avanzado'))
) ENGINE=InnoDB;

CREATE TABLE instrumento (
    instrumento_id INT PRIMARY KEY AUTO_INCREMENT,
    musico_id      INT NOT NULL,
    nombre         VARCHAR(255) NOT NULL,
    tipo           VARCHAR(50) NOT NULL,
    observaciones  VARCHAR(500) NULL,
    activo         TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_inst_musico FOREIGN KEY (musico_id) REFERENCES musico(musico_id),
    CONSTRAINT uq_inst_nombre UNIQUE (musico_id, nombre),
    CONSTRAINT chk_tipo CHECK (tipo IN ('cuerda','viento','percusión','teclado','otro'))
) ENGINE=InnoDB;

CREATE TABLE elemento_estudio (
    elemento_id        INT PRIMARY KEY AUTO_INCREMENT,
    instrumento_id     INT NOT NULL,
    nombre             VARCHAR(255) NOT NULL,
    categoria          VARCHAR(20) NOT NULL,
    dificultad         INT NOT NULL,
    autor              VARCHAR(255) NULL,
    interprete_original VARCHAR(255) NULL,
    tonalidad          VARCHAR(20) NULL,
    compas             VARCHAR(10) NULL,
    tipo_ejercicio     VARCHAR(50) NULL,
    bpm_sugerido       INT NULL,
    activo             TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_elem_inst FOREIGN KEY (instrumento_id) REFERENCES instrumento(instrumento_id),
    CONSTRAINT uq_elem_nombre UNIQUE (instrumento_id, nombre),
    CONSTRAINT chk_cat CHECK (categoria IN ('Cancion','Ejercicio')),
    CONSTRAINT chk_dif CHECK (dificultad BETWEEN 1 AND 10),
    CONSTRAINT chk_bpm CHECK (bpm_sugerido IS NULL OR bpm_sugerido BETWEEN 20 AND 300)
) ENGINE=InnoDB;

CREATE TABLE sesion_practica (
    sesion_id       INT PRIMARY KEY AUTO_INCREMENT,
    instrumento_id  INT NOT NULL,
    elemento_id     INT NOT NULL,
    fecha_registro  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tiempo_neto_min INT NOT NULL,
    comentario      VARCHAR(500) NULL,
    CONSTRAINT fk_ses_inst FOREIGN KEY (instrumento_id) REFERENCES instrumento(instrumento_id),
    CONSTRAINT fk_ses_elem FOREIGN KEY (elemento_id) REFERENCES elemento_estudio(elemento_id),
    CONSTRAINT chk_tiempo CHECK (tiempo_neto_min > 0)
) ENGINE=InnoDB;

-- Índices compuestos para el módulo de estadísticas (CU009)
CREATE INDEX idx_sesion_instrumento_ts ON sesion_practica (instrumento_id, fecha_registro);
CREATE INDEX idx_sesion_elemento_ts ON sesion_practica (elemento_id, fecha_registro);
