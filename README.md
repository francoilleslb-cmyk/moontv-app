# 🌙 Moon TV - Android App

App Android para reproducir IPTV desde tu backend en Render + MongoDB.

---

## ⚙️ Configuración (OBLIGATORIO antes de compilar)

### 1. URL del Backend

Edita `RetrofitClient.java` y cambia la URL base:

```java
// app/src/main/java/com/moontv/app/network/RetrofitClient.java
private static final String BASE_URL = "https://TU-APP.onrender.com/";
//                                       ^^^^ CAMBIA ESTO ^^^^
```

---

## 📡 API esperada del backend

La app espera estas rutas en tu backend Node.js/Express:

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/channels` | Todos los canales activos |
| GET | `/api/channels?category=Deportes` | Canales por categoría |
| GET | `/api/channels/featured` | Canales destacados |
| GET | `/api/channels/:id` | Canal por ID |
| GET | `/api/channels/search?q=cnn` | Buscar canales |
| GET | `/api/categories` | Todas las categorías |
| GET | `/api/categories/:id/channels` | Canales de una categoría |

### Formato de respuesta esperado:
```json
{
  "success": true,
  "data": [...],
  "total": 50
}
```

### Modelo Channel en MongoDB:
```js
{
  name: String,          // "CNN en Español"
  streamUrl: String,     // URL del stream M3U8 / TS
  logo: String,          // URL del logo
  category: String,      // "Noticias"
  country: String,       // "US"
  isActive: Boolean,     // true
  isFeatured: Boolean    // true/false
}
```

---

## 🎬 ExoPlayer - Configuración IPTV

El `ExoPlayerManager.java` ya está configurado con:

```java
new DefaultHttpDataSource.Factory()
    .setUserAgent("Mozilla/5.0 (Linux; Android ...)")
    .setAllowCrossProtocolRedirects(true)  // ← MUY IMPORTANTE para IPTV
    .setConnectTimeoutMs(15_000)
    .setReadTimeoutMs(15_000)
```

Soporta automáticamente:
- ✅ HLS (`.m3u8`)
- ✅ DASH (`.mpd`)
- ✅ TS progresivo
- ✅ MP4
- ✅ Redirecciones HTTP → HTTPS

---

## 🏗️ Estructura del proyecto

```
app/
├── activities/
│   ├── SplashActivity.java      # Pantalla de inicio
│   ├── MainActivity.java        # Home: categorías + destacados
│   ├── ChannelListActivity.java # Lista de canales por categoría
│   └── PlayerActivity.java      # Reproductor fullscreen
├── adapters/
│   ├── ChannelAdapter.java
│   └── CategoryAdapter.java
├── models/
│   ├── Channel.java
│   ├── Category.java
│   └── ApiResponse.java
├── network/
│   ├── ApiService.java          # Endpoints Retrofit
│   └── RetrofitClient.java      # Singleton con OkHttp
└── utils/
    └── ExoPlayerManager.java    # Gestor de ExoPlayer
```

---

## 🚀 Compilar

1. Abrir en **Android Studio**
2. Cambiar `BASE_URL` en `RetrofitClient.java`
3. `Build > Make Project`
4. Correr en dispositivo o emulador (API 21+)

---

## 🔧 Requisitos

- Android Studio Hedgehog o superior
- Gradle 8.2+
- Android API 21+ (Android 5.0)
- Conexión a internet en el dispositivo
