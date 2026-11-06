#!/usr/bin/env python3
"""Logo estilizado de la Universidad Siglo 21 para la portada (escudo verde)."""
from PIL import Image, ImageDraw, ImageFont
from pathlib import Path

W, H = 700, 950
img = Image.new("RGBA", (W, H), (255, 255, 255, 0))
d = ImageDraw.Draw(img)
VERDE_OSC = (24, 86, 56, 255)
VERDE_CLARO = (44, 122, 84, 255)
VERDE_BASE = (28, 92, 60, 255)
CREMA = (252, 248, 235, 255)
DORADO = (170, 140, 60, 255)

d.ellipse([60, 0, 640, 200], outline=VERDE_OSC, width=6, fill=CREMA)
d.ellipse([90, 30, 610, 180], outline=VERDE_OSC, width=2)
def hoja(cx, cy, rx, ry):
    d.ellipse([cx-rx, cy-ry, cx+rx, cy+ry], fill=VERDE_CLARO, outline=VERDE_OSC)
hoja(350, 70, 180, 75); hoja(220, 120, 90, 50); hoja(480, 120, 90, 50); hoja(350, 30, 120, 45)
d.rectangle([335, 145, 365, 215], fill=VERDE_OSC)

SX, SY, EX, EY = 130, 220, 570, 880
d.rounded_rectangle([SX, SY, EX, EY], radius=45, outline=VERDE_OSC, width=6, fill=CREMA)
d.rectangle([SX+8, SY+8, EX-8, SY+90], fill=VERDE_BASE)
f_uni = ImageFont.truetype("/usr/share/fonts/liberation-sans-fonts/LiberationSans-Bold.ttf", 28)
text = "UNIVERSIDAD"
bb = d.textbbox((0,0), text, font=f_uni); tw = bb[2]-bb[0]
d.text(((W-tw)/2, SY + 30), text, font=f_uni, fill=(255,255,255,255))

f_siglo = ImageFont.truetype("/usr/share/fonts/liberation-serif-fonts/LiberationSerif-Bold.ttf", 80)
text = "SIGLO"
bb = d.textbbox((0,0), text, font=f_siglo); tw = bb[2]-bb[0]
d.text(((W-tw)/2, SY + 130), text, font=f_siglo, fill=VERDE_OSC)

f_21 = ImageFont.truetype("/usr/share/fonts/liberation-serif-fonts/LiberationSerif-Bold.ttf", 290)
text = "21"
bb = d.textbbox((0,0), text, font=f_21); tw = bb[2]-bb[0]
d.text(((W-tw)/2 - 4, SY + 230), text, font=f_21, fill=VERDE_OSC)

d.rectangle([SX+8, EY-90, EX-8, EY-8], fill=VERDE_BASE)
f_ley = ImageFont.truetype("/usr/share/fonts/liberation-sans-fonts/LiberationSans-Regular.ttf", 18)
text = "DEMOCRATICA . LAICA . TRANSCENDENTE"
bb = d.textbbox((0,0), text, font=f_ley); tw = bb[2]-bb[0]
d.text(((W-tw)/2, EY - 72), text, font=f_ley, fill=(255,255,255,255))

d.polygon([(50, 380), (100, 380), (100, 700), (50, 720)], fill=CREMA, outline=VERDE_OSC)
d.polygon([(600, 380), (650, 380), (650, 720), (600, 700)], fill=CREMA, outline=VERDE_OSC)
d.rectangle([SX+8, SY+90, EX-8, SY+96], fill=DORADO)
d.rectangle([SX+8, EY-96, EX-8, EY-90], fill=DORADO)

out = Path(__file__).resolve().parent.parent / "docs" / "assets" / "logo-siglo21.png"
out.parent.mkdir(parents=True, exist_ok=True)
img.save(out, "PNG")
print("logo:", out, img.size)
