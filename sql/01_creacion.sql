-- ============================================================
-- ReperTrack - Creacion del esquema
-- Motor: MySQL 8.0+ / InnoDB / utf8mb4
-- Refactor TP3: tipos SQL explicitos, CHECK enforcement, ENUM,
--               restricciones UNIQUE y FK declarativas, indices
--               compuestos para estadisticas (CU009).
-- ============================================================

DROP DATABASE IF EXISTS repertrack;
CREATE DATABASE repertrack
 DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
USE repertrack;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- Tabla: musico
-- =========================
CREATE TABLE musico (
  musico_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre    VARCHAR(100) NOT NULL,
  nivel     ENUM('principiante','intermedio','avanzado') NOT NULL,
  estilo    VARCHAR(50)  NOT NULL,
  PRIMARY KEY (musico_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- Tabla: instrumento
-- =========================
CREATE TABLE instrumento (
  instrumento_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  musico_id      INT UNSIGNED NOT NULL,
  nombre         VARCHAR(100) NOT NULL,
  tipo           VARCHAR(30)  NOT NULL,
  observaciones  TEXT,
  activo         TINYINT(1)   NOT NULL DEFAULT 1,
  PRIMARY KEY (instrumento_id),
  UNIQUE KEY uq_inst_musico_nombre (musico_id, nombre),
  KEY idx_inst_musico (musico_id),
  CONSTRAINT fk_inst_musico FOREIGN KEY (musico_id)
    REFERENCES musico (musico_id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT chk_inst_tipo CHECK (tipo IN ('cuerda','viento','percusion','teclado','otro')),
  CONSTRAINT chk_inst_activo CHECK (activo IN (0,1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- Tabla: elemento_estudio (single-table inheritance)
-- =========================
CREATE TABLE elemento_estudio (
  elemento_id         INT UNSIGNED NOT NULL AUTO_INCREMENT,
  instrumento_id      INT UNSIGNED NOT NULL,
  nombre              VARCHAR(150) NOT NULL,
  categoria           ENUM('Cancion','Ejercicio') NOT NULL,
  dificultad          TINYINT      NOT NULL,
  autor               VARCHAR(100) NULL,
  interprete_original VARCHAR(100) NULL,
  tonalidad           VARCHAR(20)  NULL,
  compas              VARCHAR(20)  NULL,
  tipo_ejercicio      VARCHAR(30)  NULL,
  bpm_sugerido        SMALLINT     NULL,
  activo              TINYINT(1)   NOT NULL DEFAULT 1,
  PRIMARY KEY (elemento_id),
  UNIQUE KEY uq_elem_inst_nombre (instrumento_id, nombre),
  KEY idx_elem_inst (instrumento_id),
  KEY idx_elem_categoria (categoria),
  CONSTRAINT fk_elem_inst FOREIGN KEY (instrumento_id)
    REFERENCES instrumento (instrumento_id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT chk_elem_dif CHECK (dificultad BETWEEN 1 AND 10),
  CONSTRAINT chk_elem_bpm CHECK (bpm_sugerido IS NULL OR bpm_sugerido BETWEEN 20 AND 300),
  CONSTRAINT chk_elem_cancion CHECK (
    (categoria = 'Cancion' AND autor IS NOT NULL)
    OR (categoria = 'Ejercicio' AND tipo_ejercicio IS NOT NULL)
  ),
  CONSTRAINT chk_elem_activo CHECK (activo IN (0,1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- Tabla: sesion_practica
-- =========================
CREATE TABLE sesion_practica (
  sesion_id        INT UNSIGNED NOT NULL AUTO_INCREMENT,
  instrumento_id   INT UNSIGNED NOT NULL,
  elemento_id      INT UNSIGNED NOT NULL,
  fecha_registro   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tiempo_neto_min  INT          NOT NULL,
  comentario       VARCHAR(500) NULL,
  PRIMARY KEY (sesion_id),
  KEY idx_sesion_inst_ts (instrumento_id, fecha_registro),
  KEY idx_sesion_elem_ts (elemento_id, fecha_registro),
  KEY idx_sesion_fecha   (fecha_registro),
  CONSTRAINT fk_ses_inst FOREIGN KEY (instrumento_id)
    REFERENCES instrumento (instrumento_id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_ses_elem FOREIGN KEY (elemento_id)
    REFERENCES elemento_estudio (elemento_id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT chk_ses_tiempo CHECK (tiempo_neto_min > 0),
  CONSTRAINT chk_ses_coment CHECK (comentario IS NULL OR CHAR_LENGTH(comentario) <= 500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;