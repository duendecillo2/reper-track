#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Genera CANGUILLEN-JUAN_MARTIN-AP4.PDF: documento integrador TP1+TP2+TP3+TP4.
A4, Calibri 11 (Carlito), espaciado simple, tildes UTF-8.
"""
import os
from pathlib import Path
from reportlab.lib.pagesizes import A4, landscape
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import cm
from reportlab.lib.enums import TA_JUSTIFY, TA_CENTER, TA_LEFT
from reportlab.lib.colors import HexColor, white, black
from reportlab.platypus import (BaseDocTemplate, PageTemplate, Frame, Paragraph,
                                Spacer, Image, PageBreak, Table, TableStyle,
                                KeepTogether, NextPageTemplate)
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont

ROOT = Path(__file__).resolve().parent.parent
DOCS = ROOT / "docs"
DIAG = DOCS / "diagramas"
CAPS = DOCS / "capturas"
LOGO = DOCS / "assets" / "logo-siglo21.png"
OUT = ROOT / "CANGUILLEN-JUAN_MARTIN-AP4.PDF"

VERDE = HexColor("#1c5c3c")
VERDE_CLARO = HexColor("#e8f1ea")
GRIS = HexColor("#f5f5f5")

# Fuentes (Carlito = metrica de Calibri)
pdfmetrics.registerFont(TTFont("Calibri", "/usr/share/fonts/google-carlito-fonts/Carlito-Regular.ttf"))
pdfmetrics.registerFont(TTFont("Calibri-Bold", "/usr/share/fonts/google-carlito-fonts/Carlito-Bold.ttf"))
pdfmetrics.registerFont(TTFont("Calibri-It", "/usr/share/fonts/google-carlito-fonts/Carlito-Italic.ttf"))
F, FB, FI = "Calibri", "Calibri-Bold", "Calibri-It"
FM = "Courier"

ss = getSampleStyleSheet()
body = ParagraphStyle("body", parent=ss["Normal"], fontName=F, fontSize=11, leading=14,
                      alignment=TA_JUSTIFY, spaceAfter=6)
h1 = ParagraphStyle("h1", parent=ss["Heading1"], fontName=FB, fontSize=14, leading=17,
                    textColor=VERDE, spaceBefore=12, spaceAfter=6)
h2 = ParagraphStyle("h2", parent=ss["Heading2"], fontName=FB, fontSize=12, leading=15,
                    spaceBefore=9, spaceAfter=4, keepWithNext=1)
h3 = ParagraphStyle("h3", parent=ss["Heading3"], fontName=FB, fontSize=11, leading=14,
                    spaceBefore=7, spaceAfter=3, keepWithNext=1)
bullet = ParagraphStyle("bullet", parent=body, leftIndent=16, bulletIndent=6, spaceAfter=2)
code = ParagraphStyle("code", parent=ss["Normal"], fontName=FM, fontSize=8.5, leading=11,
                      leftIndent=8, rightIndent=8, backColor=GRIS, borderColor=HexColor("#dddddd"),
                      borderWidth=0.5, borderPadding=5, spaceAfter=4)
cap = ParagraphStyle("cap", parent=ss["Normal"], fontName=FI, fontSize=9, leading=11,
                     alignment=TA_CENTER, textColor=HexColor("#666666"), spaceBefore=3, spaceAfter=10)

def P(t, s=body): return Paragraph(t, s)
def H1(t): return P(t, h1)
def H2(t): return P(t, h2)
def H3(t): return P(t, h3)
def B(items):
    return [Paragraph(i, bullet, bulletText="•") for i in items]
def CODE(t):
    return P(t.replace("\n", "<br/>").replace(" ", "&nbsp;"), code)
def SP(h=0.25): return Spacer(1, h*cm)

def FIG(path, caption, width_cm=16):
    img = Image(str(path))
    w = width_cm*cm
    h = w * img.imageHeight / img.imageWidth
    max_h = 22*cm
    if h > max_h:
        h = max_h
        w = h * img.imageWidth / img.imageHeight
    return KeepTogether([Image(str(path), width=w, height=h), P(caption, cap)])


def tabla_paquetes():
    data = [
        ["Paquete", "Responsabilidad"],
        ["model", "Entidades del dominio, enums, Estadística y Alerta."],
        ["excepcion", "Excepciones checked: rango, longitud, referencia, inexistencia, persistencia y archivo."],
        ["util", "Conexión JDBC y validaciones centralizadas."],
        ["dao", "Interfaces e implementaciones JDBC con fallback a memoria."],
        ["service", "Reglas de negocio, estadísticas, alertas y exportadores PDF/TXT."],
        ["controlador", "Menú de consola y subcontroladores por módulo."],
        ["estructuras", "Lista enlazada, pila, cola, búsqueda y ordenación."],
    ]
    return TABLE(data, [3.2, 20.0], font_size=9)

def TABLE(data, widths, header=True, font_size=9):
    rows = []
    for i, row in enumerate(data):
        cells = []
        for c in row:
            st = ParagraphStyle("tc", parent=body, fontSize=font_size, leading=11,
                                alignment=TA_LEFT, spaceAfter=0)
            cells.append(Paragraph(str(c), st))
        rows.append(cells)
    t = Table(rows, colWidths=[w*cm for w in widths], repeatRows=1 if header else 0)
    cmds = [("GRID", (0,0), (-1,-1), 0.4, HexColor("#cccccc")),
            ("VALIGN", (0,0), (-1,-1), "TOP"),
            ("LEFTPADDING", (0,0), (-1,-1), 4),
            ("RIGHTPADDING", (0,0), (-1,-1), 4),
            ("TOPPADDING", (0,0), (-1,-1), 3),
            ("BOTTOMPADDING", (0,0), (-1,-1), 3)]
    if header:
        cmds += [("BACKGROUND", (0,0), (-1,0), VERDE_CLARO),
                 ("FONTNAME", (0,0), (-1,0), FB)]
    t.setStyle(TableStyle(cmds))
    return t

# ---------------- Portada ----------------
def cover(c, doc):
    c.saveState()
    W, H = A4
    y = H - 3.4*cm
    c.setFont(FB, 18); c.drawCentredString(W/2, y, "UNIVERSIDAD SIGLO 21"); y -= 0.85*cm
    c.setFont(F, 12); c.drawCentredString(W/2, y, "Seminario Práctico de Informática"); y -= 0.65*cm
    c.drawCentredString(W/2, y, "Módulo 4"); y -= 0.65*cm
    c.line(3*cm, y, W-3*cm, y); y -= 0.85*cm
    c.setFont(FB, 14); c.drawCentredString(W/2, y, "Trabajo Práctico 4"); y -= 0.65*cm
    c.line(3*cm, y, W-3*cm, y)

    logo_w = 5.6*cm
    logo_h = logo_w * (950/700)
    c.drawImage(str(LOGO), (W-logo_w)/2, y - 9.4*cm, width=logo_w, height=logo_h, mask="auto")

    y = y - 9.4*cm - 1.2*cm
    c.setFont(FB, 11)
    c.drawCentredString(W/2, y, "Caso de estudio: ReperTrack - Sistema Integrado de Gestión de")
    y -= 0.45*cm
    c.drawCentredString(W/2, y, "Repertorio y Práctica Musical")
    y -= 1.0*cm
    def campo(label, value):
        nonlocal y
        c.setFont(FB, 11); c.drawString(2.5*cm, y, label)
        lw = c.stringWidth(label, FB, 11)
        c.setFont(F, 11); c.drawString(2.5*cm + lw + 0.15*cm, y, value)
        y -= 0.52*cm
    campo("Estudiante:", "Juan Martín Canguillen")
    campo("DNI:", "44933871")
    campo("Profesor Titular:", "Pablo Alejandro Virgolini")
    campo("Carrera:", "Licenciatura en Informática")
    campo("Año:", "2026 - 2.° cuatrimestre")
    campo("Fecha de entrega:", "17 de octubre de 2026")
    c.restoreState()

def header_footer(c, doc):
    c.saveState()
    W, H = doc.pagesize
    c.setFont(F, 8); c.setFillColor(HexColor("#777777"))
    c.drawString(2*cm, 1*cm, "CANGUILLEN-JUAN_MARTIN-AP4.PDF")
    c.drawRightString(W-2*cm, 1*cm, f"Página {doc.page}")
    c.restoreState()

# ---------------- Documento ----------------
OUT.parent.mkdir(parents=True, exist_ok=True)
doc = BaseDocTemplate(str(OUT), pagesize=A4,
                      leftMargin=2.4*cm, rightMargin=2.4*cm,
                      topMargin=2.2*cm, bottomMargin=2*cm,
                      title="CANGUILLEN-JUAN_MARTIN-AP4.PDF",
                      author="Juan Martín Canguillen")

fp = Frame(2.4*cm, 2*cm, A4[0]-4.8*cm, A4[1]-4.2*cm, id="fp")
fl = Frame(1.5*cm, 1.5*cm, landscape(A4)[0]-3*cm, landscape(A4)[1]-3*cm, id="fl")
doc.addPageTemplates([
    PageTemplate(id="cover", frames=[fp], onPage=cover),
    PageTemplate(id="portrait", frames=[fp], onPage=header_footer),
    PageTemplate(id="landscape", frames=[fl], onPage=header_footer, pagesize=landscape(A4)),
])

story = [NextPageTemplate("portrait"), PageBreak()]

# ---------------- Índice ----------------
story += [H1("Índice"), SP(0.1)]
indice = [
    "Parte I. Trabajo Práctico 1 — Inicio del PUD",
    "  1. Introducción y justificación",
    "  2. Objetivos del proyecto y del sistema",
    "  3. Alcance y elicitación",
    "  4. Diagrama de dominio",
    "  5. Propuesta funcional y técnica",
    "  6. Requerimientos y casos de uso",
    "Parte II. Trabajo Práctico 2 — Análisis y diseño",
    "  7. Análisis y secuencia de diseño",
    "  8. Diagrama de clases de diseño",
    "  9. Despliegue y aclaración del CU011",
    "  10. Plan de pruebas (CP01..CP18)",
    "  11. Base de datos, DER y scripts SQL",
    "  12. Prototipos de consola y comunicaciones",
    "Parte III. Trabajo Práctico 3 — Prototipo Java",
    "  13. Los cuatro pilares de la POO",
    "  14. Excepciones y evidencia de ejecución",
    "  15. Menú, estructuras propias y persistencia JDBC",
    "Parte IV. Trabajo Práctico 4 — Entrega integradora",
    "  16. Patrón de diseño MVC",
    "  17. Arreglos y ArrayList",
    "  18. Manejo de archivos de texto",
    "  19. Excepciones en la interacción con MySQL",
    "  20. Clases abstractas e interfaces",
    "  21. Lógica de negocio y reglas RN-01..RN-05",
    "  22. Capturas del programa y video",
    "  23. Correcciones aplicadas y cierre",
    "  24. Participación activa",
    "Anexo A. Diagrama de clases completo",
    "Anexo B. Capturas de ejecución",
    "Anexo C. Consultas SQL",
    "Anexo D. Guion del video",
    "Referencias bibliográficas",
]
for i in indice:
    story.append(P(i, body))
story += [SP(0.3), P("<b>Repositorio público del proyecto:</b> <a href='https://github.com/duendecillo2/reper-track'><font color='#1c5c3c'><b>https://github.com/duendecillo2/reper-track</b></font></a>", body)]
story.append(PageBreak())

# ================= PARTE I =================
story += [H1("Parte I. Trabajo Práctico 1 — Inicio del PUD")]
story += [P("Esta primera parte retoma el documento original del TP1 con las correcciones de redacción y de modelo que quedaron pendientes. Se mantienen el problema, la justificación y el alcance; se reescriben los objetivos y se ajusta el diagrama de dominio al relevamiento de procesos.")]

story += [H2("1. Introducción y justificación")]
story += [P("La práctica musical de nivel intermedio o avanzado exige organizar, en paralelo, un repertorio vivo, una biblioteca de ejercicios técnicos y metas de mediano y largo plazo. En la mayoría de los casos ese control se hace de forma artesanal: cuadernos, planillas dispersas o la memoria del intérprete. El problema no es nuevo —la literatura sobre práctica deliberada de Ericsson, Krampe y Tesch-Römer (1993) ya lo señalaba—, pero sigue sin una respuesta informática adecuada para el músico individual.")]
story += [P("ReperTrack nace de esa experiencia personal: durante años llevé mi repertorio en cuadernos que se perdían y en planillas que abandonaba por cansancio. El sistema propone un repositorio único, un cronómetro integrado y métricas de progreso que permitan decidir qué practicar con evidencia y no con intuición. La justificación es, a la vez, técnica (Java + MySQL + JDBC) y personal (un músico que necesita la herramienta).")]

story += [H2("2. Objetivos del proyecto y del sistema")]
story += [P("<b>Objetivo general del proyecto.</b> Desarrollar ReperTrack, una aplicación de consola en Java 11 con persistencia en MySQL 8 y arquitectura MVC, que permita a músicos intermedios y avanzados gestionar su repertorio y cronometrar sesiones de práctica vinculadas a instrumentos y elementos de estudio, generando estadísticas de progreso que respalden la planificación de metas.")]
story += [P("<b>Objetivos específicos.</b>")]
story += B([
    "Implementar el ABM de Instrumentos y de Elementos de Estudio (Canciones y Ejercicios), con persistencia transaccional y validación mediante excepciones personalizadas.",
    "Desarrollar el registro cronometrado de sesiones, con iniciar/pausar/finalizar, asociado obligatoriamente a un Instrumento y a un Elemento.",
    "Implementar el módulo de estadísticas: acumulados por instrumento y por elemento, frecuencia semanal y promedio por sesión.",
    "Aplicar el patrón MVC separando menú de consola, controladores/servicios y modelo/DAO.",
    "Construir un esquema relacional en 3FN con claves foráneas, UNIQUE, CHECK e índices compuestos.",
    "Versionar el código y los scripts en un repositorio público de GitHub con README de instalación y ejecución.",
])
story += [P("<b>Objetivo general del sistema.</b> Registrar, almacenar y procesar los datos de la práctica musical —instrumentos, elementos y sesiones cronometradas— para producir información agregada de progreso que respalde la toma de decisiones sobre qué obras ensayar, cuánto tiempo dedicar a cada una y qué elementos retirar del repertorio activo.")]

story += [H2("3. Alcance y elicitación")]
story += [P("El prototipo cubre el ABM de instrumentos y elementos, el registro cronometrado de sesiones, la consulta y eliminación de sesiones, las estadísticas de progreso, la exportación de reportes (PDF y TXT) y las alertas de mantenimiento. No contempla reproducción de audio, sincronización en la nube, multiusuario ni autenticación remota: el acceso físico al equipo es la frontera de seguridad.")]
story += [P("La elicitación se apoyó en la experiencia directa del autor como músico y en la observación de las herramientas que usa a diario (cuaderno, Excel, notas del celular). El «cliente» de esta primera versión es el propio músico, lo que elimina la curva de aprendizaje del dominio y permite validar cada módulo con la rutina real de práctica.")]

story += [H2("4. Diagrama de dominio")]
story += [P("El diagrama se ajustó para mostrar atributos, multiplicidades y la asociación directa entre Músico y SesiónDePráctica, que en la versión original quedaba implícita. Músico posee instrumentos y registra sesiones; Instrumento agrupa elementos y se asocia a sesiones; ElementoDeEstudio se aborda en sesiones y se especializa en Canción y Ejercicio.")]
story.append(FIG(DIAG/"dominio.png", "Figura 1. Diagrama de clases de dominio (corrección del TP1).", 15))

story += [H2("5. Propuesta funcional y técnica")]
story += [P("<b>Propuesta funcional.</b> Cuatro módulos: gestión de instrumentos y elementos, registro transaccional de sesiones, estadísticas y reportes, y alertas de mantenimiento. Cada módulo se corresponde con un caso de uso y con un conjunto de reglas de negocio.")]
story += [P("<b>Propuesta técnica.</b> Java 11 LTS, MySQL 8.0 (InnoDB, utf8mb4), JDBC oficial (mysql-connector-j), Maven como gestor de dependencias y una aplicación de consola pura (Scanner + System.out) como capa de presentación. No se usan frameworks de UI ni de persistencia: la consigna pide Java y JDBC, y la consola mantiene el prototipo liviano, repetible y fácil de probar.")]

story += [H2("6. Requerimientos y casos de uso")]
story += [P("Los requerimientos funcionales se numeraron RFS01..RFS11 y los no funcionales RNF01..RNF08. Los primeros cubren ABM, cronómetro, asociación obligatoria, listado, eliminación, estadísticas, alertas y exportación; los segundos fijan Java 11, MySQL 8, MVC, GitHub, transacciones ACID, usabilidad, portabilidad y pruebas.")]
story += [P("Los actores son el Músico (principal), el Sistema de Persistencia (secundario), el Reloj del Sistema y el Sistema de Archivos. El caso de uso crítico es CU006 (Registrar Sesión de Práctica), que incluye la validación de integridad referencial (CU010) y puede disparar el recálculo de alertas (CU012). CU011 (Exportar Reporte) extiende a CU009 (Consultar Estadísticas).")]
story += [H3("Requerimientos funcionales")]
rfs = [["ID", "Descripción"],
       ["RFS01", "ABM de instrumentos (nombre, tipo, observaciones)."],
       ["RFS02", "ABM de canciones (autor, intérprete, tonalidad, compás, dificultad)."],
       ["RFS03", "ABM de ejercicios (tipo, BPM, dificultad)."],
       ["RFS04", "Cronómetro con iniciar, pausar y finalizar."],
       ["RFS05", "Asociación obligatoria de la sesión a instrumento y elemento."],
       ["RFS06", "Listado y filtrado de sesiones por instrumento, elemento y fecha."],
       ["RFS07", "Eliminación de sesión con confirmación explícita."],
       ["RFS08", "Tiempo acumulado por instrumento y por elemento."],
       ["RFS09", "Frecuencia semanal y promedio por sesión."],
       ["RFS10", "Alertas de mantenimiento por inactividad."],
       ["RFS11", "Exportación del reporte a PDF y TXT."]]
story.append(TABLE(rfs, [2.0, 13.5], font_size=9))
story += [H3("Requerimientos no funcionales")]
rnf = [["ID", "Categoría", "Descripción"],
       ["RNF01", "Implementación", "Java 11 o superior, aplicación de consola."],
       ["RNF02", "Persistencia", "MySQL 8 con JDBC oficial."],
       ["RNF03", "Arquitectura", "Patrón MVC con capas separadas."],
       ["RNF04", "Calidad", "Repositorio público en GitHub con README."],
       ["RNF05", "Confiabilidad", "Transacciones ACID con commit/rollback."],
       ["RNF06", "Usabilidad", "Mensajes claros y confirmación en eliminaciones."],
       ["RNF07", "Portabilidad", "Windows, Linux y macOS sin recompilar."],
       ["RNF08", "Mantenibilidad", "Pruebas automatizadas del dominio y los DAO."]]
story.append(TABLE(rnf, [2.0, 3.2, 10.3], font_size=9))

story.append(PageBreak())

# ================= PARTE II =================
story += [H1("Parte II. Trabajo Práctico 2 — Análisis y diseño")]
story += [P("El TP2 se corrigió en los puntos que el docente señaló: la secuencia de diseño ahora usa exclusivamente las clases del diagrama de clases; ElementoDeEstudio guarda una asociación a Instrumento (no un id suelto); el despliegue tiene definiciones completas; el CU011 está especificado; la nomenclatura de pruebas quedó unificada; el DER muestra tipos SQL y las pruebas de integridad se ejecutan en MySQL.")]

story += [H2("7. Análisis y secuencia de diseño")]
story += [P("El análisis se documentó con clases de interfaz, control y entidad. La secuencia de diseño del CU006 usa exactamente las clases que luego se programaron: MenuPrincipal, SesionController, SesionService, SesionDePractica, Instrumento, ElementoDeEstudio, SesionDAO, ConexionDB y MySQL. El flujo normal pide instrumento y elemento, valida su existencia, construye la sesión, la valida (RN-01) y la inserta dentro de una transacción (setAutoCommit(false), commit, rollback).")]
story.append(FIG(DIAG/"secuencia-cu006.png", "Figura 2. Diagrama de secuencia del CU006 con las clases de diseño.", 16))

story += [H2("8. Diagrama de clases de diseño")]
story += [P("El diagrama se sincronizó con el código de la Entrega 4: paquete <font name='Courier'>model</font> (no «modelo»), asociaciones explícitas, visibilidades, tipos Java, <font name='Courier'>Estadistica</font> con el arreglo <font name='Courier'>int[7]</font>, <font name='Courier'>ExportadorTextoService</font>, <font name='Courier'>ArchivoException</font> y los enums del dominio. La figura se presenta en una página horizontal para que sea legible; el detalle de cada clase se explica en el texto.")]
story.append(NextPageTemplate("landscape"))
story.append(PageBreak())
story.append(FIG(DIAG/"clases-diseno.png", "Figura 3. Diagrama de clases de diseño sincronizado con el código (Entrega 4).", 25))
story.append(tabla_paquetes())
story.append(NextPageTemplate("portrait"))
story.append(PageBreak())

story += [H2("9. Despliegue y aclaración del CU011")]
story += [P("El despliegue es monousuario: la PC del músico ejecuta la aplicación sobre JRE 11 y conversa con un MySQL 8.0 local mediante mysql-connector-j sobre TCP/IP loopback :3306. No hay servidor web, ni Apache/Tomcat/HTTP/XAMPP. Los artefactos son el JAR, el script de creación, el script de integridad, el reporte TXT y el README.")]
story.append(FIG(DIAG/"despliegue.png", "Figura 4. Diagrama de despliegue local.", 16))
story += [P("<b>CU011 — Exportar reporte.</b> Precondiciones: hay al menos una sesión registrada y el músico eligió la opción de exportar. Postcondiciones: se genera un archivo válido en la ruta indicada y se informa el tamaño. Curso normal: se calcula la estadística, se escribe el archivo (PDF o TXT) y se muestra la confirmación. Alternativas: si no hay datos, se rechaza con un mensaje; si el archivo existe, se sobrescribe. Excepciones: error de E/S (<font name='Courier'>IOException</font> o <font name='Courier'>ArchivoException</font>) y ruta inválida. Reglas: el reporte debe contener al menos un indicador (RN-07).")]

story += [H2("10. Plan de pruebas (CP01..CP18)")]
story += [P("La nomenclatura se unificó: el plan y los resultados usan los mismos códigos CP01..CP18. Los catorce primeros cubren sesiones, elementos, polimorfismo, estadísticas, alertas y PDF; CP15 cubre el arreglo semanal; CP16..CP18 cubren el archivo de texto (escritura/lectura, archivo inexistente y reporte vacío). Todos se ejecutan con un stub de DAO para que sean deterministas.")]
tabla_cp = [
    ["Código", "Qué verifica", "Resultado"],
    ["CP01", "Sesión válida persiste", "PASS"],
    ["CP02", "Sesión huérfana rechazada (RN-01)", "PASS"],
    ["CP03", "Tiempo cero rechazado (RN-02)", "PASS"],
    ["CP04", "Frontera 1 min aceptada", "PASS"],
    ["CP05", "Comentario de 501 caracteres rechazado (RN-04)", "PASS"],
    ["CP06", "Dificultad 0/11 rechazada", "PASS"],
    ["CP07", "BPM 19/301 rechazado", "PASS"],
    ["CP08", "Progreso de Canción 480/960 = 0,5", "PASS"],
    ["CP09", "Progreso de Ejercicio 120/240 = 0,5", "PASS"],
    ["CP10", "Acumulado por instrumento (75 y 60)", "PASS"],
    ["CP11", "Promedio global 45,0", "PASS"],
    ["CP12", "Alerta por 45 días sin práctica", "PASS"],
    ["CP13", "PDF válido con estadísticas", "PASS"],
    ["CP14", "Reporte PDF vacío rechazado", "PASS"],
    ["CP15", "Arreglo semanal de 7 días (lun=30, mié=45)", "PASS"],
    ["CP16", "TXT se escribe y se vuelve a leer", "PASS"],
    ["CP17", "Lectura de archivo inexistente rechazada", "PASS"],
    ["CP18", "Reporte TXT vacío rechazado", "PASS"],
]
story.append(TABLE(tabla_cp, [2.0, 11.5, 2.5]))
story.append(P("Resultado de la corrida: 18 PASS, 0 FAIL. La evidencia completa se muestra en el Anexo B.", cap))

story += [H2("11. Base de datos, DER y scripts SQL")]
story += [P("El esquema tiene cuatro tablas InnoDB en utf8mb4: musico, instrumento, elemento_estudio y sesion_practica. La herencia Canción/Ejercicio se resolvió con single-table inheritance (columna categoria + columnas específicas nulables). Las claves foráneas usan ON DELETE RESTRICT para no perder sesiones; hay UNIQUE para evitar duplicados lógicos, CHECK para las reglas de negocio e índices compuestos para las consultas de estadísticas.")]
story.append(FIG(DIAG/"der.png", "Figura 5. DER con tipos SQL explícitos (corrección del TP2).", 15))
story += [P("Las diez pruebas de integridad se ejecutaron sobre MySQL 8.0.40 real (no SQLite): se rechazaron el tiempo cero, la dificultad 11, el BPM 19, la FK inexistente, el UNIQUE duplicado y el borrado con FK activa; el INSERT válido pasó y el conteo final dio 1,1,1,1. La captura está en el Anexo B (Figura 15).")]
story += [P("Las consultas Q1..Q5 cubren historial, acumulado por instrumento, acumulado por elemento, frecuencia/promedio y alertas. Se incluyen en el Anexo C.")]

story += [H2("12. Prototipos de consola y comunicaciones")]
story += [P("Los prototipos de interfaz se diseñaron como pantallas ASCII: menú principal, submenú de instrumentos, submenú de elementos, registro de sesión con cronómetro, estadísticas y alertas. La implementación final conserva esa estructura y agrega las opciones 8 y 9 para el reporte TXT. Las capturas reales están en el Anexo B.")]
story += [P("Las comunicaciones son locales: JDBC sobre TCP/IP loopback hacia MySQL; llamadas a métodos entre capas (Vista → Controlador → Servicio → DAO); sin HTTP, sin serialización y sin servidor de aplicaciones. El backup queda a cargo del músico (mysqldump).")]

story.append(PageBreak())

# ================= PARTE III =================
story += [H1("Parte III. Trabajo Práctico 3 — Prototipo Java")]
story += [P("El TP3 migró la vista Swing a consola pura y convirtió el modelo en un diseño con asociaciones reales. Esta parte resume los cuatro pilares y la evidencia de ejecución que el docente pidió reforzar.")]

story += [H2("13. Los cuatro pilares de la POO")]
story += [P("<b>Encapsulamiento.</b> Todos los atributos del modelo son privados; el acceso se hace por getters/setters y los setters validan con <font name='Courier'>Validador</font>. No se puede crear una Canción con dificultad 11 sin atrapar <font name='Courier'>RangoInvalidoException</font>.")]
story += [P("<b>Herencia.</b> <font name='Courier'>ElementoDeEstudio</font> es abstracta y <font name='Courier'>Cancion</font> y <font name='Courier'>Ejercicio</font> la especializan. Cada subclase redefine <font name='Courier'>calcularProgreso(int)</font>.")]
story += [P("<b>Polimorfismo.</b> El servicio invoca <font name='Courier'>calcularProgreso</font> sobre la referencia del tipo padre y la JVM despacha la versión concreta: Canción divide por dificultad × 120; Ejercicio, por dificultad × 60. CP08 y CP09 lo verifican.")]
story += [P("<b>Abstracción.</b> La clase abstracta define el contrato y las interfaces DAO definen la persistencia sin atar a una tecnología. El TestRunner inyecta un stub de <font name='Courier'>SesionDAO</font> sin tocar MySQL.")]

story += [H2("14. Excepciones y evidencia de ejecución")]
story += [P("Hay seis excepciones checked: <font name='Courier'>RangoInvalidoException</font>, <font name='Courier'>LongitudExcedidaException</font>, <font name='Courier'>SesionSinReferenciaException</font>, <font name='Courier'>ElementoInexistenteException</font>, <font name='Courier'>InstrumentoInexistenteException</font> y <font name='Courier'>PersistenciaException</font>. La Entrega 4 agrega <font name='Courier'>ArchivoException</font> para el manejo de archivos. Cada controlador captura y muestra un mensaje claro; los DAO envuelven <font name='Courier'>SQLException</font> en <font name='Courier'>PersistenciaException</font>.")]
story.append(FIG(CAPS/"07-error-entrada.png", "Figura B.7. Manejo de entrada inválida: se ingresa una letra donde va un número.", 14))
story.append(FIG(CAPS/"08-error-rango.png", "Figura B.8. Excepción de rango: dificultad 11 rechazada por la regla de negocio.", 14))

story += [H2("15. Menú, estructuras propias y persistencia JDBC")]
story += [P("El menú principal tiene nueve opciones y despacha con un switch. Las estructuras propias son lista enlazada simple, pila y cola, más búsqueda lineal y ordenación por selección/burbuja. La persistencia usa JDBC con SQL parametrizado y transacciones ACID; si MySQL no responde, los DAO caen a memoria y lo informan por consola.")]
story.append(FIG(CAPS/"01-menu.png", "Figura B.1. Menú principal del sistema en consola.", 14))

story.append(PageBreak())

# ================= PARTE IV =================
story += [H1("Parte IV. Trabajo Práctico 4 — Entrega integradora")]
story += [P("Esta parte responde a los requisitos específicos de la Entrega 4: patrón de diseño justificado, arreglos y ArrayList, archivos, excepciones en MySQL, clases abstractas/interfaces y evidencia de ejecución. También consolida las correcciones de las entregas anteriores.")]

story += [H2("16. Patrón de diseño MVC")]
story += [P("Se aplica Modelo-Vista-Controlador. El <b>Modelo</b> son las entidades de <font name='Courier'>model</font> y los DAO; no conocen la consola ni JDBC. La <b>Vista</b> es la capa de controladores de consola: lee con Scanner, valida la forma de la entrada y formatea la salida; no decide reglas de negocio. El <b>Controlador</b> son los servicios: orquestan el flujo, aplican RN-01..RN-05 y manejan transacciones.")]
story += [P("La separación se justifica por tres razones concretas: (1) permite probar la lógica sin levantar la interfaz (el TestRunner usa un stub de DAO); (2) aísla el cambio de presentación (pasamos de Swing en TP2 a consola en TP3 sin tocar servicios ni DAO); (3) evita que una regla de negocio quede duplicada en la vista. Si mañana se agregara una API REST, los servicios y el modelo se reutilizan tal cual.")]

story += [H2("17. Arreglos y ArrayList")]
story += [P("La consigna pide usarlos de forma complementaria, no elegir uno. El <font name='Courier'>ArrayList</font> se usa para lo dinámico: los DAO devuelven <font name='Courier'>List&lt;SesionDePractica&gt;</font>, <font name='Courier'>List&lt;Instrumento&gt;</font> y <font name='Courier'>List&lt;ElementoDeEstudio&gt;</font>, y el TestRunner y los controladores los recorren con for-each. El <b>arreglo fijo</b> se usa para lo que tiene tamaño conocido: la distribución de minutos por día de la semana es un <font name='Courier'>int[7]</font> (0=lunes … 6=domingo) que se llena desde las sesiones y se muestra como barras.")]
story += [CODE(
    "private final int[] minutosPorDiaSemana = new int[7];\n"
    "\n"
    "public void agregarMinutosDia(int diaSemana, int minutos) {\n"
    "    if (diaSemana >= 1 && diaSemana <= 7)\n"
    "        minutosPorDiaSemana[diaSemana - 1] += minutos;\n"
    "}\n"
    "\n"
    "public int[] getMinutosPorDiaSemana() { return minutosPorDiaSemana.clone(); }"
)]
story += [P("La complementariedad es real: el ArrayList trae las filas de MySQL; el arreglo las agrega en siete posiciones fijas. CP15 verifica que el arreglo tenga siete posiciones y que la suma por día sea correcta (lunes 30, miércoles 45).")]
story.append(FIG(CAPS/"05-estadisticas.png", "Figura B.5. Estadísticas con la distribución semanal calculada sobre el arreglo int[7].", 14))

story += [H2("18. Manejo de archivos de texto")]
story += [P("Además del PDF, el sistema exporta y vuelve a leer un reporte de texto UTF-8. <font name='Courier'>ExportadorTextoService.exportar</font> escribe con <font name='Courier'>BufferedWriter</font> y <font name='Courier'>Files.newBufferedWriter</font>; <font name='Courier'>leer</font> usa <font name='Courier'>BufferedReader</font>. Ambos cierran el recurso con try-with-resources y envuelven <font name='Courier'>IOException</font> en <font name='Courier'>ArchivoException</font>.")]
story += [CODE(
    "try (BufferedWriter w = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {\n"
    "    w.write(\"ReperTrack - Reporte de Estadísticas\"); w.newLine();\n"
    "    w.write(\"Total de sesiones: \" + est.getTotalSesiones()); w.newLine();\n"
    "    // ... acumulados y distribución semanal ...\n"
    "} catch (IOException ex) {\n"
    "    throw new ArchivoException(\"No se pudo escribir el reporte de texto\", ex);\n"
    "}"
)]
story += [P("La funcionalidad se expone en el menú (opciones 8 y 9) y se verifica con CP16 (escribe y lee), CP17 (archivo inexistente) y CP18 (reporte vacío). La Figura B.6 muestra la salida real.")]
story.append(FIG(CAPS/"06-txt.png", "Figura B.6. Exportación y lectura del reporte TXT desde el menú.", 14))

story += [H2("19. Excepciones en la interacción con MySQL")]
story += [P("Cada operación JDBC está dentro de try-with-resources. Las escrituras usan <font name='Courier'>setAutoCommit(false)</font>, <font name='Courier'>commit()</font> y <font name='Courier'>rollback()</font>; cualquier <font name='Courier'>SQLException</font> se envuelve en <font name='Courier'>PersistenciaException</font> para que la vista no dependa de <font name='Courier'>java.sql</font>. En la lectura, un <font name='Courier'>ResultSet</font> inválido se reporta con el mismo tipo.")]
story += [P("La evidencia real se generó con MySQL 8.0.40 en <font name='Courier'>127.0.0.1:3307</font>: se creó el esquema con <font name='Courier'>01_creacion.sql</font>, se cargaron datos con <font name='Courier'>02_inserts.sql</font>, se ejecutó el programa con <font name='Courier'>REPER_DB_URL</font> apuntando al servidor y se verificó la fila con un SELECT. La Figura B.9 muestra la sesión 5 con 40 minutos recién insertada; la Figura B.10, las pruebas de integridad con los rechazos esperados.")]
story.append(FIG(CAPS/"09-mysql-select.png", "Figura B.9. Verificación en MySQL de la sesión insertada por el programa.", 15))
story.append(FIG(CAPS/"10-integridad.png", "Figura B.10. Pruebas de integridad en MySQL: CHECK, FK y UNIQUE rechazan los casos inválidos.", 16))

story += [H2("20. Clases abstractas e interfaces")]
story += [P("Se usan ambas y se justifica por qué. <b>Clase abstracta:</b> <font name='Courier'>ElementoDeEstudio</font> concentra los atributos comunes y obliga a implementar <font name='Courier'>calcularProgreso</font>; no tiene sentido instanciarla porque un «elemento genérico» no existe en el dominio. <b>Interfaces:</b> <font name='Courier'>InstrumentoDAO</font>, <font name='Courier'>ElementoDAO</font> y <font name='Courier'>SesionDAO</font> definen el contrato de persistencia y permiten inyectar un stub en las pruebas; esa es una costura que se usa de verdad, no una interfaz decorativa.")]
story += [P("No se creó una interfaz para <font name='Courier'>ExportadorTextoService</font> porque tiene una sola implementación y ningún test necesita sustituirla. Si en una iteración futura se agrega CSV o JSON con el mismo contrato, ese es el disparador para extraer la interfaz.")]

story += [H2("21. Lógica de negocio y reglas RN-01..RN-05")]
story += [P("La lógica se concentra en los servicios, no en la vista. <b>RN-01</b>: toda sesión debe tener instrumento y elemento; se valida en <font name='Courier'>SesionDePractica.validar()</font> y en <font name='Courier'>SesionService</font>. <b>RN-02</b>: el tiempo neto debe ser mayor a cero. <b>RN-03</b>: la fecha no puede ser futura (validada al registrar). <b>RN-04</b>: el comentario no supera 500 caracteres. <b>RN-05</b>: atomicidad transaccional en el DAO.")]
story += [P("El flujo completo es: la vista pide los datos, el controlador los convierte a objetos, el servicio aplica las reglas y el DAO persiste. Si una regla falla, la excepción checked obliga al controlador a mostrar el mensaje y no se ejecuta el INSERT. Esa separación es la que permite que CP01..CP07 prueben las reglas sin abrir el menú.")]

story += [H2("22. Capturas del programa y video")]
story += [P("Las capturas del Anexo B son la salida real del programa ejecutado contra MySQL: menú, alta de instrumento, alta de elemento, registro de sesión, estadísticas con el arreglo semanal, reporte TXT, errores de entrada/rango y verificación en la base. No son mockups: se capturaron desde un pseudo-terminal y se renderizaron como imagen para el PDF.")]
story += [P("El video de presentación (3 minutos) muestra el mismo recorrido: menú, alta de instrumento, alta de obra, registro de sesión, SELECT en MySQL y estadísticas. El guion completo está en el Anexo D. Enlace del video: <b>[PEGAR AQUÍ EL ENLACE DEL VIDEO]</b>.")]

story += [H2("23. Correcciones aplicadas y cierre")]
story += [P("<b>TP1:</b> objetivos reescritos, dominio con asociaciones y atributos, propuesta técnica en consola, requerimientos con redacción revisada. <b>TP2:</b> secuencia de diseño con las clases del diagrama, asociación en ElementoDeEstudio, despliegue completo, CU011 especificado, CP01..CP18 unificados, DER con tipos SQL e integridad en MySQL. <b>TP3:</b> lógica de negocio explicada, capturas de ejecución, excepciones con evidencia y menú documentado.")]
story += [P("El resultado es un prototipo que compila, ejecuta y pasa 18/18 pruebas; un documento que explica las decisiones y las muestra en pantalla; y un repositorio público con el código, los scripts y los diagramas. Quedan como trabajo futuro las notificaciones push, la sincronización en la nube y la planificación semanal con metas configurables, todas ya listadas como requerimientos candidatos.")]

story += [H2("24. Participación activa")]
story += [P("Durante el cursado participé en los foros de la materia y en los encuentros sincrónicos, aporté en los espacios comunes y mantuve comunicación con el equipo docente. Las evidencias (capturas de las participaciones y enlaces a los hilos) se incorporan a continuación.")]
story += [P("<b>[ADJUNTAR AQUÍ LAS CAPTURAS O ENLACES DE PARTICIPACIÓN]</b>")]

story.append(PageBreak())

# ================= ANEXO A =================
story += [H1("Anexo A. Diagrama de clases completo")]
story += [P("Los cinco diagramas del PUD están incluidos en las Partes I y II con su explicación. Este anexo agrega la vista completa del diagrama de clases de diseño, que por su tamaño se presenta en página horizontal; el detalle de cada paquete está en la tabla siguiente y en el texto de la sección 8.")]
story.append(NextPageTemplate("landscape"))
story.append(PageBreak())
story += [FIG(DIAG/"clases-diseno.png", "Figura A.1. Diagrama de clases de diseño sincronizado con el código.", 25), tabla_paquetes()]
story.append(NextPageTemplate("portrait"))
story.append(PageBreak())

# ================= ANEXO B =================
story += [H1("Anexo B. Capturas de ejecución")]
story += [P("Todas las capturas son la salida real del programa ejecutado contra MySQL 8.0.40. Se renderizaron con un marco de terminal para facilitar la lectura en el PDF.")]
caps = [
    ("01-menu.png", "Figura B.1. Menú principal."),
    ("02-instrumento.png", "Figura B.2. Alta de instrumento."),
    ("03-elemento.png", "Figura B.3. Alta de elemento (canción)."),
    ("04-sesion.png", "Figura B.4. Registro de sesión con instrumento y elemento."),
    ("05-estadisticas.png", "Figura B.5. Estadísticas y distribución semanal."),
    ("06-txt.png", "Figura B.6. Reporte TXT: escritura y lectura."),
    ("07-error-entrada.png", "Figura B.7. Error de entrada inválida."),
    ("08-error-rango.png", "Figura B.8. Error de rango en la dificultad."),
    ("09-mysql-select.png", "Figura B.9. Verificación de la fila en MySQL."),
    ("10-integridad.png", "Figura B.10. Pruebas de integridad en MySQL."),
]
for name, caption in caps:
    story.append(FIG(CAPS/name, caption, 15))

story.append(PageBreak())

# ================= ANEXO C =================
story += [H1("Anexo C. Consultas SQL")]
story += [P("Las consultas Q1..Q5 se ejecutan en el cliente MySQL y se corresponden con los casos de uso. Se reproducen para que el lector pueda verificarlas.")]
story += [CODE(
    "-- Q1. Historial completo (CU007)\n"
    "SELECT SP.sesion_id, M.nombre AS musico, I.nombre AS instrumento,\n"
    "       EE.nombre AS elemento, EE.categoria, SP.tiempo_neto_min AS minutos,\n"
    "       SP.fecha_registro AS fecha\n"
    "  FROM sesion_practica SP\n"
    "  JOIN instrumento I ON SP.instrumento_id = I.instrumento_id\n"
    "  JOIN musico M ON I.musico_id = M.musico_id\n"
    "  JOIN elemento_estudio EE ON SP.elemento_id = EE.elemento_id\n"
    " ORDER BY SP.fecha_registro DESC;"
)]
story += [CODE(
    "-- Q2. Acumulado por instrumento (CU009)\n"
    "SELECT I.nombre AS instrumento, SUM(SP.tiempo_neto_min) AS minutos_totales\n"
    "  FROM sesion_practica SP JOIN instrumento I ON SP.instrumento_id = I.instrumento_id\n"
    " GROUP BY I.nombre;"
)]
story += [CODE(
    "-- Q3. Acumulado por elemento (CU009)\n"
    "SELECT EE.nombre AS elemento, SUM(SP.tiempo_neto_min) AS minutos_totales\n"
    "  FROM sesion_practica SP JOIN elemento_estudio EE ON SP.elemento_id = EE.elemento_id\n"
    " GROUP BY EE.nombre;"
)]
story += [CODE(
    "-- Q4. Frecuencia y promedio (RFS09)\n"
    "SELECT COUNT(*) AS sesiones_7d, AVG(tiempo_neto_min) AS promedio_min\n"
    "  FROM sesion_practica WHERE fecha_registro >= NOW() - INTERVAL 7 DAY;"
)]
story += [CODE(
    "-- Q5. Alertas de mantenimiento (CU012)\n"
    "SELECT EE.elemento_id, EE.nombre, MAX(SP.fecha_registro) AS ultima,\n"
    "       DATEDIFF(NOW(), MAX(SP.fecha_registro)) AS dias\n"
    "  FROM elemento_estudio EE LEFT JOIN sesion_practica SP ON SP.elemento_id = EE.elemento_id\n"
    " WHERE EE.activo = 1 GROUP BY EE.elemento_id\n"
    "HAVING ultima IS NULL OR DATEDIFF(NOW(), ultima) > 30;"
)]

story.append(PageBreak())

# ================= ANEXO D =================
story += [H1("Anexo D. Guion del video")]
story += [P("El video dura 3 minutos y muestra el core del sistema contra MySQL. El guion completo está en <font name='Courier'>docs/guion-video.md</font>; aquí se resume el recorrido.")]
story += B([
    "0:00–0:15 — Presentación del proyecto y del stack (Java 11 + MySQL 8 + JDBC).",
    "0:15–0:40 — Menú y arquitectura MVC.",
    "0:40–1:15 — Alta de un instrumento.",
    "1:15–1:50 — Alta de una canción asociada.",
    "1:50–2:30 — Registro de una sesión y verificación con SELECT en MySQL.",
    "2:30–2:50 — Estadísticas con el arreglo semanal y exportación TXT.",
    "2:50–3:00 — Cierre y enlace al repositorio.",
])
story += [P("<b>Enlace del video:</b> [PEGAR AQUÍ EL ENLACE DEL VIDEO ANTES DE ENTREGAR]")]

# ================= REFERENCIAS =================
story += [H1("Referencias bibliográficas")]
refs = [
    "Braude, E. (2003). <i>Ingeniería de software: una perspectiva orientada a objetos</i>. Alfaomega.",
    "Ericsson, K. A., Krampe, R. T., y Tesch-Römer, C. (1993). The role of deliberate practice in the acquisition of expert performance. <i>Psychological Review, 100</i>(3), 363-406.",
    "Institute of Electrical and Electronics Engineers. (2008). <i>Standard for Software Quality Assurance Plans</i> (ANSI/IEEE Standard No. 829).",
    "Jacobson, I., Booch, G., y Rumbaugh, J. (1999). <i>The unified software development process</i>. Addison-Wesley.",
    "Kendall, K., y Kendall, J. (2011). <i>Análisis y diseño de sistemas</i>. Pearson Educación.",
    "Newport, C. (2016). <i>Deep work: Rules for focused success in a distracted world</i>. Grand Central Publishing.",
    "Reinosa, E. J., Maldonado, C. A., Muñoz, R., Damiano, L. E., y Abrutsky, M. A. (2012). <i>Bases de datos</i>. Alfaomega.",
    "Rumbaugh, J., Jacobson, I., y Booch, G. (2006). <i>El lenguaje unificado de modelado</i> (2.ª ed.). Pearson-Addison-Wesley.",
    "Saroka, R. (2002). <i>Sistemas de información en la era digital</i>. Fundación OSDE.",
    "Sommerville, I. (2005). <i>Ingeniería del software</i> (7.ª ed.). Pearson-Addison-Wesley.",
    "Stair, R., y Reynolds, G. (2010). <i>Principios de sistemas de información</i> (9.ª ed.). Cengage Learning.",
]
for r in refs:
    story.append(P(r, body))

story += [SP(0.6), P("<b>Repositorio público del proyecto:</b> <a href='https://github.com/duendecillo2/reper-track'><font color='#1c5c3c'><b>https://github.com/duendecillo2/reper-track</b></font></a>", body)]

doc.build(story)
print("PDF:", OUT)
