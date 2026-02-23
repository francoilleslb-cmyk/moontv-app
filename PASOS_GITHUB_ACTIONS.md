# 📱 Cómo obtener el APK de Moon TV sin PC

## Paso 1 — Crear un repositorio en GitHub

1. Entrá a **github.com** desde tu celular o cualquier navegador
2. Tocá el **+** (arriba a la derecha) → **New repository**
3. Nombre: `moon-tv-android` (o el que quieras)
4. Marcá **Private** (para que nadie más lo vea)
5. NO marques "Add README"
6. Tocá **Create repository**

---

## Paso 2 — Subir el código al repositorio

### Opción fácil: desde la web de GitHub

1. En tu nuevo repo, tocá **uploading an existing file**
2. **Descomprimí** el ZIP `MoonTV.zip` que te di
3. **Arrastrá TODOS los archivos y carpetas** al área de upload
   > ⚠️ Importante: subir la carpeta `.github` también (puede estar oculta)
4. Tocá **Commit changes**

### Opción con Git (si tenés Git instalado):
```bash
cd MoonTV
git init
git add .
git commit -m "Moon TV Android App"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/moon-tv-android.git
git push -u origin main
```

---

## Paso 3 — GitHub Actions compila el APK automáticamente

Apenas subas el código, GitHub Actions **empieza a compilar solo**.

Para verlo:
1. Entrá a tu repo en GitHub
2. Tocá la pestaña **"Actions"**
3. Vas a ver **"🌙 Moon TV — Build APK"** ejecutándose
4. Esperá ~5-10 minutos (la primera vez descarga dependencias)
5. Cuando aparezca un ✅ verde, ¡terminó!

---

## Paso 4 — Descargar el APK

1. Tocá el workflow que tiene ✅
2. Bajá hasta la sección **"Artifacts"**
3. Tocá **"MoonTV-APK"** para descargarlo
4. Se descarga un ZIP → adentro está el `app-debug.apk`

---

## Paso 5 — Instalar el APK en tu Android

1. Pasá el `app-debug.apk` a tu celular (por WhatsApp, Drive, etc.)
2. Abrilo desde el celular
3. Si te pide "Permitir instalar de fuentes desconocidas" → **Permitir**
4. Tocá **Instalar**
5. ¡Abrí Moon TV! 🌙

---

## ✅ Próximas actualizaciones

Cada vez que hagas cambios y los subas a GitHub, 
el APK se **recompila automáticamente** y lo podés descargar de nuevo.

---

## ❓ Preguntas frecuentes

**¿El APK tiene virus?** No, lo compilaste vos mismo desde el código fuente.

**¿Por qué pide "fuentes desconocidas"?** Porque no está en la Play Store. Es normal para APKs que compilás vos.

**¿Cuánto tarda la primera compilación?** 5-15 minutos (descarga ~500MB de dependencias). Las siguientes tardan ~2 minutos por el caché.

**¿El APK es seguro compartir?** Es un APK de debug, mejor no compartirlo públicamente.
