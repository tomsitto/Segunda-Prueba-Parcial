# Sistema de Control de Acceso a Laboratorio

Sistema de escritorio por consola desarrollado en **Java 8** que permite gestionar el ingreso y salida de usuarios (estudiantes y docentes) a un laboratorio, con persistencia de datos en archivos de texto plano.

---

## Tabla de contenidos

1. [Descripción general](#descripción-general)
2. [Tecnologías utilizadas](#tecnologías-utilizadas)
3. [Arquitectura del proyecto](#arquitectura-del-proyecto)
4. [Estructura de carpetas](#estructura-de-carpetas)
5. [Formato de archivos de datos](#formato-de-archivos-de-datos)
6. [Funcionalidades](#funcionalidades)
7. [Cómo ejecutar el proyecto](#cómo-ejecutar-el-proyecto)
8. [Ejemplo de uso](#ejemplo-de-uso)
9. [Restricciones y convenciones](#restricciones-y-convenciones)

---

## Descripción general

El sistema permite:

- Registrar usuarios con roles definidos (`ESTUDIANTE` / `DOCENTE`).
- Controlar entradas y salidas al laboratorio con marca de tiempo automática.
- Consultar el historial de accesos por usuario.
- Calcular el tiempo total acumulado de permanencia en el laboratorio.
- Persistir toda la información en archivos `.txt` sin necesidad de base de datos.

---

## Tecnologías utilizadas

| Tecnología | Detalle |
|---|---|
| Java | Versión 8 (compatible con JDK 8+) |
| `java.time.LocalDateTime` | Manejo de fechas y horas |
| `java.io.BufferedReader/Writer` | Lectura y escritura de archivos |
| `java.util.Scanner` | Entrada de datos por consola |
| Sin frameworks externos | Solo biblioteca estándar de Java |

---

## Arquitectura del proyecto

El proyecto sigue una **arquitectura por capas** con separación estricta de responsabilidades:

```
┌─────────────────────────────────┐
│      presentacion (Main)        │  ← Menú interactivo por consola
├─────────────────────────────────┤
│  logicanegocio (Services)       │  ← Validaciones y reglas de negocio
├─────────────────────────────────┤
│   accesodatos (DAOs)            │  ← Lectura y escritura en archivos .txt
├─────────────────────────────────┤
│     entidades (POJOs)           │  ← Modelo de datos puro
└─────────────────────────────────┘
```

Cada capa solo se comunica con la capa inmediatamente inferior. La capa de presentación **nunca** accede a los DAOs directamente.

---

## Estructura de carpetas

```
src/
├── entidades/
│   ├── Usuario.java            # POJO con constantes de rol
│   └── Acceso.java             # POJO con LocalDateTime para entrada/salida
│
├── accesodatos/
│   ├── UsuarioDAO.java         # Persistencia en usuarios.txt
│   └── AccesoDAO.java          # Persistencia en accesos.txt
│
├── logicanegocio/
│   ├── UsuarioService.java     # Lógica de negocio de usuarios
│   └── AccesoService.java      # Lógica de negocio de accesos
│
└── presentacion/
    └── Main.java               # Punto de entrada y menú por consola

datos/
├── usuarios.txt                # Generado automáticamente al registrar usuarios
└── accesos.txt                 # Generado automáticamente al registrar accesos
```

> Los archivos `.txt` se crean automáticamente en el directorio de trabajo al primer uso. No es necesario crearlos manualmente.

---

## Formato de archivos de datos

### `usuarios.txt`

Un usuario por línea con el formato:

```
id,nombre,rol
```

Ejemplo:

```
U001,María González,ESTUDIANTE
U002,Carlos Méndez,DOCENTE
U003,Ana Jiménez,ESTUDIANTE
```

### `accesos.txt`

Un registro de acceso por línea con el formato:

```
idUsuario,fechaHoraEntrada,fechaHoraSalida
```

Las fechas se almacenan en formato **ISO-8601** (`yyyy-MM-ddTHH:mm:ss`).
Cuando el usuario aún no ha registrado su salida, el tercer campo contiene el valor `PENDIENTE`.

Ejemplo:

```
U001,2025-06-10T08:30:00,2025-06-10T11:45:00
U002,2025-06-10T09:00:00,PENDIENTE
U001,2025-06-11T07:55:00,2025-06-11T12:10:00
```

---

## Funcionalidades

| Opción | Función | Descripción |
|:---:|---|---|
| `1` | Registrar usuario | Valida campos, rol permitido e ID no duplicado |
| `2` | Listar usuarios | Muestra tabla con ID, nombre y rol |
| `3` | Eliminar usuario | Solicita confirmación antes de eliminar |
| `4` | Registrar entrada | Estampa hora actual; bloquea si ya hay una entrada activa |
| `5` | Registrar salida | Completa el registro abierto con la hora actual |
| `6` | Ver historial | Muestra todos los accesos del usuario con entrada, salida y duración |
| `7` | Ver tiempo total | Suma los minutos de todos los accesos completados |
| `0` | Salir | Cierra el sistema |

### Validaciones aplicadas

- Ningún campo puede estar vacío o ser `null`.
- El rol solo acepta `ESTUDIANTE` o `DOCENTE` (insensible a mayúsculas).
- No se permiten IDs de usuario duplicados.
- Un usuario no puede registrar una entrada si ya tiene una sesión abierta.
- Un usuario no puede registrar una salida si no tiene una entrada activa.
- El tiempo total solo contabiliza accesos con salida registrada.

---

## Cómo ejecutar el proyecto

### Requisitos previos

- **JDK 8** o superior instalado.
- IDE compatible (IntelliJ IDEA, Eclipse, NetBeans) o compilación manual.

### Compilación manual desde terminal

```bash
# Desde la raíz del proyecto, compilar todas las clases
javac -d out src/entidades/*.java src/accesodatos/*.java src/logicanegocio/*.java src/presentacion/*.java

# Ejecutar el sistema
java -cp out presentacion.Main
```

### Desde un IDE

1. Importar el proyecto como proyecto Java estándar.
2. Asegurarse de que el directorio `src/` esté marcado como fuente (_Source Root_).
3. Ejecutar la clase `presentacion.Main`.

---

## Ejemplo de uso

```
══════════════════════════════════════════════════
   SISTEMA DE CONTROL DE ACCESO A LABORATORIO
══════════════════════════════════════════════════

         MENÚ PRINCIPAL
══════════════════════════════════════════════════
  1. Registrar usuario
  2. Listar usuarios
  3. Eliminar usuario
──────────────────────────────────────────────────
  4. Registrar entrada al laboratorio
  5. Registrar salida del laboratorio
  6. Ver historial de usuario
  7. Ver tiempo total en laboratorio
──────────────────────────────────────────────────
  0. Salir
══════════════════════════════════════════════════
  Seleccione una opción: 1

══════════════════════════════════════════════════
   REGISTRAR USUARIO
══════════════════════════════════════════════════
  ID del usuario: U001
  Nombre completo: María González
  Roles disponibles: [1] ESTUDIANTE   [2] DOCENTE
  Seleccione el rol: 1

  ✔  Usuario registrado correctamente.
  ID     : U001
  Nombre : María González
  Rol    : ESTUDIANTE
```

---

## Restricciones y convenciones

- **Compatibilidad**: Java 8. No se usan características de versiones posteriores.
- **Sin base de datos**: toda la persistencia es en archivos `.txt`.
- **Sin frameworks**: solo la biblioteca estándar de Java (`java.io`, `java.time`, `java.util`).
- **Separación de capas**: cada capa accede únicamente a la capa inmediatamente inferior.
- **Sin lógica de presentación en servicios**: los mensajes de error de negocio se emiten por `System.err`; el formateo visual es responsabilidad exclusiva de `Main`.
- **Documentación**: entidades y DAOs cuentan con JavaDoc completo.
