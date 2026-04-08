# Changelog

## Sistema de Control de Acceso a Laboratorio — Java (Arquitectura por Capas)

Todos los cambios relevantes del proyecto están documentados en este archivo.
El formato sigue las convenciones de [Keep a Changelog](https://keepachangelog.com/es/1.0.0/).

---

## [1.0.0] — Versión inicial completa

### Añadido — Capa de Entidades (`entidades/`)

- **`Usuario.java`**
  - Atributos privados: `id`, `nombre`, `rol`.
  - Constantes de rol: `ESTUDIANTE` y `DOCENTE` como `public static final String`.
  - Constructor vacío y constructor completo.
  - Getters y setters con validación básica (`null` / vacío) mediante `IllegalArgumentException`.
  - Método `toString()` formateado multilínea.
  - Documentación JavaDoc completa.

- **`Acceso.java`**
  - Atributos privados: `idUsuario`, `fechaHoraEntrada`, `fechaHoraSalida` (usando `java.time.LocalDateTime`).
  - `fechaHoraSalida` admite `null` de forma explícita para representar sesiones activas.
  - Constructor vacío y constructor completo.
  - Getters y setters; `fechaHoraEntrada` valida que no sea `null`.
  - Método `toString()` con `DateTimeFormatter` en patrón `dd/MM/yyyy HH:mm:ss`; muestra `"Aún en laboratorio"` cuando la salida es `null`.
  - Documentación JavaDoc completa.

---

### Añadido — Capa de Acceso a Datos (`accesodatos/`)

- **`UsuarioDAO.java`**
  - Persistencia en archivo `usuarios.txt` con formato `id,nombre,rol`.
  - `guardarUsuario()`: append al archivo; previene duplicados llamando a `existeUsuario()` antes de escribir.
  - `listarUsuarios()`: lectura con `BufferedReader`; usa `split(DELIMITADOR, 3)` para tolerar comas en nombres.
  - `eliminarUsuario()`: carga la lista, aplica `removeIf` y reescribe el archivo completo.
  - `existeUsuario()`: búsqueda línea a línea sin cargar todo en memoria; detecta `id,` al inicio de línea para evitar falsos positivos.
  - Manejo de `FileNotFoundException` silencioso (archivo aún no creado) y de `IOException` con mensaje en `System.err`.

- **`AccesoDAO.java`**
  - Persistencia en archivo `accesos.txt` con formato `idUsuario,fechaHoraEntrada,fechaHoraSalida`.
  - Valor centinela `"PENDIENTE"` para representar salidas no registradas.
  - Fechas serializadas con `LocalDateTime.toString()` (ISO-8601) y deserializadas con `LocalDateTime.parse()`.
  - `registrarEntrada()`: append al archivo; bloquea duplicados con `tieneEntradaPendiente()`.
  - `registrarSalida()`: carga todos los accesos, localiza el primer registro abierto del usuario, estampa `LocalDateTime.now()` y reescribe.
  - `listarAccesos()` y `obtenerAccesosPorUsuario()` como operaciones de solo lectura.

---

### Añadido — Capa de Lógica de Negocio (`logicanegocio/`)

- **`UsuarioService.java`**
  - `registrarUsuario()`: valida campos vacíos → rol válido → id duplicado (en ese orden) antes de persistir.
  - Normalización del rol con `.toUpperCase().trim()` para aceptar variantes como `"docente"` o `" Docente "`.
  - `obtenerUsuarios()`: delegación directa al DAO.
  - `eliminarUsuario()`: valida id no vacío y reporta si el usuario no existía.
  - Método privado `rolEsValido()` para centralizar la lógica de validación de roles.

- **`AccesoService.java`**
  - Consume tanto `AccesoDAO` como `UsuarioDAO` para validar existencia de usuario sin acceder a archivos directamente.
  - `registrarEntrada()`: verifica existencia de usuario y ausencia de entrada activa antes de delegar.
  - `registrarSalida()`: verifica existencia de usuario y presencia de entrada pendiente antes de delegar.
  - `obtenerHistorial()`: retorna lista filtrada por usuario; devuelve `new ArrayList<>()` ante id vacío.
  - `calcularTiempoTotalMinutos()`: suma duraciones de accesos completados usando `ChronoUnit.MINUTES.between()`; ignora sesiones activas (`fechaHoraSalida == null`).
  - Método privado `tieneEntradaPendiente()` reutilizado en ambas operaciones de entrada/salida.

---

### Añadido — Capa de Presentación (`presentacion/`)

- **`Main.java`**
  - Menú interactivo por consola con 8 opciones (0–7) en bucle `do-while`.
  - `Scanner` compartido como campo estático; cerrado al salir.
  - `leerCampo()`: bucle que rechaza campos vacíos con mensaje de advertencia antes de llegar al servicio.
  - `leerOpcion()`: captura `NumberFormatException` y repite la solicitud sin romper el flujo.
  - Selección de rol mediante `[1] / [2]` para evitar errores de tipeo; valor libre igualmente enviado al servicio para validación.
  - Confirmación `s/n` antes de ejecutar la eliminación de un usuario.
  - `calcularDuracion()`: formatea la duración de cada acceso como `Xh YYm` en la capa de presentación.
  - Accesos activos mostrados como `"En laboratorio"` con duración `"-"` en el historial.
  - Salida totalmente compatible con **Java 8**.

---

### Corregido

- **`AccesoService.java`** — `obtenerHistorial()`:
  - `List.of()` (Java 9+) reemplazado por `new ArrayList<>()` para compatibilidad con **Java 8**.
  - Agregado `import java.util.ArrayList`.

- **`Main.java`** — compatibilidad con **Java 8**:
  - `switch` con *arrow expressions* (`case 1 ->`) en `procesarOpcion()` reemplazado por `switch` clásico con `case:` / `break`.
  - *Switch expression* (`String rol = switch (...)`) en `registrarUsuario()` reemplazado por bloque `if / else if / else`.

---

### Estructura final del proyecto

```
src/
├── entidades/
│   ├── Usuario.java
│   └── Acceso.java
├── accesodatos/
│   ├── UsuarioDAO.java
│   └── AccesoDAO.java
├── logicanegocio/
│   ├── UsuarioService.java
│   └── AccesoService.java
└── presentacion/
    └── Main.java

recursos/
├── usuarios.txt
└── accesos.txt
```

---

### Restricciones y convenciones aplicadas en todo el proyecto

- Compatibilidad con **Java 8**.
- Sin frameworks externos; solo la biblioteca estándar de Java.
- Sin base de datos; persistencia exclusivamente en archivos `.txt`.
- Sin lógica de negocio en DAOs ni en la capa de presentación.
- Sin acceso directo a archivos desde servicios o presentación.
- Código documentado con **JavaDoc** en entidades y DAOs.
