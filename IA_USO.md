# IA_USO.md — Documentación del uso de Inteligencia Artificial

## Sistema de Control de Acceso a Laboratorio

Este documento registra de forma transparente cómo se utilizó la herramienta de
Inteligencia Artificial (Claude - Anthropic) durante el desarrollo del proyecto,
incluyendo los prompts utilizados, las partes que resolvió cada uno, los ajustes
realizados manualmente y la justificación técnica del uso de la IA.

---

## Índice

1. [Resumen del uso de IA](#resumen-del-uso-de-ia)
2. [Prompt 1 — Capa de Entidades](#prompt-1--capa-de-entidades)
3. [Prompt 2 — Capa de Acceso a Datos](#prompt-2--capa-de-acceso-a-datos)
4. [Prompt 3 — Capa de Lógica de Negocio](#prompt-3--capa-de-lógica-de-negocio)
5. [Prompt 4 — Capa de Presentación](#prompt-4--capa-de-presentación)
6. [Prompt 5 — Corrección de error en AccesoService](#prompt-5--corrección-de-error-en-accesoservice)
7. [Prompt 6 — Corrección de errores en Main](#prompt-6--corrección-de-errores-en-main)
8. [Prompt 7 — Generación de CHANGELOG.md](#prompt-7--generación-de-changelogmd)
9. [Prompt 8 — Generación de README.md](#prompt-8--generación-de-readmemd)
10. [Prompt 9 — Generación de IA_USO.md](#prompt-9--generación-de-ia_usomd)
11. [Justificación técnica del uso de IA](#justificación-técnica-del-uso-de-ia)
12. [Valoración crítica](#valoración-crítica)

---

## Resumen del uso de IA

| # | Prompt | Archivos generados | Ajustes manuales |
|:---:|---|---|:---:|
| 1 | Capa de entidades | `Usuario.java`, `Acceso.java` | No |
| 2 | Capa de acceso a datos | `UsuarioDAO.java`, `AccesoDAO.java` | No |
| 3 | Capa de lógica de negocio | `UsuarioService.java`, `AccesoService.java` | No |
| 4 | Capa de presentación | `Main.java` | No |
| 5 | Fix `List.of()` en AccesoService | `AccesoService.java` (corregido) | No |
| 6 | Fix `switch` moderno en Main | `Main.java` (corregido) | No |
| 7 | Changelog | `CHANGELOG.md` | No |
| 8 | README | `README.md` | No |
| 9 | Este documento | `IA_USO.md` | No |

---

## Prompt 1 — Capa de Entidades

### Prompt utilizado

> Estoy desarrollando un sistema en Java llamado "Sistema de Control de Acceso a
> Laboratorio" utilizando arquitectura por capas. Necesito implementar ÚNICAMENTE
> la capa de entidades (modelo de datos). Genera dos clases Java dentro de un
> paquete llamado "entidades": [...]

### Parte que resolvió

Generación completa de las dos clases del modelo de datos:

- **`Usuario.java`**: atributos privados (`id`, `nombre`, `rol`), constantes
  `ESTUDIANTE` y `DOCENTE`, constructor vacío, constructor completo, getters y
  setters con validación de campos vacíos/nulos mediante `IllegalArgumentException`,
  y `toString()` formateado multilínea. JavaDoc completo.

- **`Acceso.java`**: atributos privados (`idUsuario`, `fechaHoraEntrada`,
  `fechaHoraSalida`) usando `java.time.LocalDateTime`, manejo explícito de
  `fechaHoraSalida` como `null` para sesiones activas, `DateTimeFormatter`
  declarado como constante estática, y `toString()` con texto condicional
  `"Aún en laboratorio"`. JavaDoc completo.

### Ajustes manuales realizados

Ninguno. El código generado cumplió todos los requisitos especificados.

### Decisiones tomadas por la IA destacables

- Usar `split(DELIMITADOR, 3)` con límite para evitar romper nombres con comas.
- Declarar el `DateTimeFormatter` como `static final` en lugar de instanciarlo
  en cada llamada a `toString()`.
- El constructor completo de `Acceso` delega en los setters para reutilizar
  validaciones, pero asigna `fechaHoraSalida` directamente para permitir `null`.

---

## Prompt 2 — Capa de Acceso a Datos

### Prompt utilizado

> Estoy desarrollando un sistema en Java con arquitectura por capas. Ahora necesito
> la capa de acceso a datos dentro del paquete "accesodatos". Condiciones generales:
> persistencia en archivos .txt, usar BufferedReader, BufferedWriter, FileWriter,
> FileReader, manejar correctamente excepciones (try-catch), no incluir lógica de
> negocio. [...]

### Parte que resolvió

Generación completa de las dos clases DAO:

- **`UsuarioDAO.java`**: métodos `guardarUsuario()` (append con control de
  duplicados), `listarUsuarios()`, `eliminarUsuario()` (reescritura del archivo
  sin el elemento eliminado) y `existeUsuario()`. Métodos privados auxiliares
  `serializar()`, `deserializar()` y `reescribirArchivo()` para mantener el
  código modular.

- **`AccesoDAO.java`**: métodos `registrarEntrada()`, `registrarSalida()`
  (búsqueda del registro abierto y actualización), `listarAccesos()` y
  `obtenerAccesosPorUsuario()`. Constante centinela `"PENDIENTE"` para
  representar salidas no registradas. Serialización/deserialización de
  `LocalDateTime` en formato ISO-8601.

### Ajustes manuales realizados

Ninguno. La lógica de archivos, manejo de excepciones y modularización fueron
generados correctamente desde el primer intento.

### Decisiones tomadas por la IA destacables

- Uso del valor centinela `"PENDIENTE"` en lugar de un campo vacío, lo que hace
  el archivo legible y evita ambigüedades al parsear.
- `FileNotFoundException` capturado silenciosamente (archivo aún no creado)
  separado del `IOException` general, que sí reporta en `System.err`.
- `FileWriter(ARCHIVO, true)` para append y `FileWriter(ARCHIVO, false)` para
  reescritura completa, claramente diferenciados.

---

## Prompt 3 — Capa de Lógica de Negocio

### Prompt utilizado

> Estoy desarrollando un sistema en Java con arquitectura por capas. Ahora necesito
> la capa de lógica de negocio en el paquete "logicanegocio". Clases requeridas:
> UsuarioService y AccesoService. Esta capa debe usar DAO, pero NO manejar archivos
> directamente. [...]

### Parte que resolvió

Generación completa de las dos clases de servicio:

- **`UsuarioService.java`**: `registrarUsuario()` con validaciones en orden
  (campos vacíos → rol válido → duplicado), `obtenerUsuarios()` y
  `eliminarUsuario()`. Método privado `rolEsValido()` centralizado.
  Normalización del rol con `.toUpperCase().trim()`.

- **`AccesoService.java`**: `registrarEntrada()` y `registrarSalida()` con
  doble validación (existencia de usuario + estado de sesión),
  `obtenerHistorial()`, y `calcularTiempoTotalMinutos()` usando
  `ChronoUnit.MINUTES.between()`. Método privado `tieneEntradaPendiente()`
  reutilizado en ambas operaciones. `AccesoService` consume tanto `AccesoDAO`
  como `UsuarioDAO`.

### Ajustes manuales realizados

Ninguno en esta etapa. El error de `List.of()` fue detectado posteriormente
al compilar con Java 8 y corregido en el Prompt 5.

### Decisiones tomadas por la IA destacables

- `AccesoService` inyecta `UsuarioDAO` para verificar existencia de usuario,
  reconociendo que esa validación es una regla de negocio y no debe estar
  en el DAO de accesos.
- Las sesiones activas (`fechaHoraSalida == null`) son excluidas del cálculo
  de tiempo total sin lanzar error, dado que representan un estado válido.
- `obtenerHistorial()` retorna una colección vacía en lugar de `null` para
  proteger a la capa de presentación.

---

## Prompt 4 — Capa de Presentación

### Prompt utilizado

> Estoy desarrollando un sistema en Java con arquitectura por capas. Ahora necesito
> la capa de presentación en el paquete "presentacion". Genera una clase Main con
> un menú interactivo por consola. [...]

### Parte que resolvió

Generación completa de `Main.java`:

- Bucle `do-while` con menú de 8 opciones (0–7).
- `leerCampo()`: bucle que rechaza entradas vacías con mensaje inmediato.
- `leerOpcion()`: captura `NumberFormatException` sin romper el flujo.
- Selección de rol con `[1]/[2]` para evitar errores de tipeo.
- Confirmación `s/n` antes de eliminar un usuario.
- `calcularDuracion()` privado para formatear duración como `Xh YYm`.
- Accesos activos mostrados como `"En laboratorio"`.
- Constantes `SEPARADOR` y `DIVISOR` para una presentación uniforme.

### Ajustes manuales realizados

Ninguno en esta etapa. Los errores de sintaxis incompatible con Java 8 fueron
detectados al compilar y corregidos en el Prompt 6.

### Decisiones tomadas por la IA destacables

- `Scanner` y servicios declarados como campos estáticos compartidos por todos
  los métodos, evitando instanciaciones repetidas.
- El formateo de duración (`calcularDuracion()`) se mantiene en la capa de
  presentación, sin delegar esa responsabilidad a los servicios.
- Mensajes de éxito (`✔`) y error (`✘`) con símbolos visuales para mejorar
  la experiencia en consola.

---

## Prompt 5 — Corrección de error en AccesoService

### Prompt utilizado

> ok en AccesoService tengo error en la linea `return List.of();` y me indica
> `cannot find symbol` arregla esa clase

### Parte que resolvió

Diagnóstico y corrección del error de compatibilidad con Java 8 en
`AccesoService.java`:

- Identificó que `List.of()` es una API introducida en Java 9.
- Reemplazó `List.of()` por `new ArrayList<>()`.
- Agregó el `import java.util.ArrayList` faltante.

### Ajustes manuales realizados

Ninguno. La corrección fue puntual y precisa sin alterar el resto de la clase.

### Causa raíz del error

El código fue generado inicialmente sin restricción de versión de Java.
Al compilar en un entorno con JDK 8, `List.of()` no estaba disponible.
El error fue detectado en tiempo de compilación, no en ejecución.

---

## Prompt 6 — Corrección de errores en Main

### Prompt utilizado

> ahora tengo otro error en la clase de Main en la linea `case 1 -> registrarUsuario();`
> donde me dice que `switch rules are not supported in -source 8` y tambien en otra linea
> `String rol = switch (rolInput) {` donde me dice que `switch expressions are not
> supported in -source 8`, agarra esa clase y arreglalo

### Parte que resolvió

Diagnóstico y corrección de dos incompatibilidades con Java 8 en `Main.java`:

- **`switch` con arrow expressions** (`case 1 ->`): reemplazado por `switch`
  clásico con `case:` y `break` en `procesarOpcion()`.
- **Switch expression** (`String rol = switch (...)`): reemplazado por bloque
  `if / else if / else` equivalente en `registrarUsuario()`.

### Ajustes manuales realizados

Ninguno. Ambas correcciones fueron aplicadas quirúrgicamente sin afectar la
lógica del resto de la clase.

### Causa raíz del error

Las *arrow expressions* en `switch` son sintaxis de Java 14+ y las *switch
expressions* son de Java 14+ (preview desde Java 12). Ambas son incompatibles
con `-source 8`.

---

## Prompt 7 — Generación de CHANGELOG.md

### Prompt utilizado

> ahora necesito que me generes un changelog.md de lo que hicimos ahora

### Parte que resolvió

Generación del archivo `CHANGELOG.md` con:

- Versión `[1.0.0]` documentando todos los artefactos añadidos por capa.
- Sección **Corregido** con los dos bugs de Java 8 y sus causas.
- Estructura final del proyecto en árbol de directorios.
- Restricciones y convenciones aplicadas globalmente.
- Formato basado en [Keep a Changelog](https://keepachangelog.com/es/1.0.0/).

### Ajustes manuales realizados

Ninguno.

---

## Prompt 8 — Generación de README.md

### Prompt utilizado

> necesito ahora que me generes el readme.md del proyecto

### Parte que resolvió

Generación del archivo `README.md` con:

- Descripción general del sistema.
- Tabla de tecnologías utilizadas.
- Diagrama ASCII de la arquitectura por capas.
- Árbol de estructura de carpetas con descripción por archivo.
- Formato real de los archivos `usuarios.txt` y `accesos.txt` con ejemplos.
- Tabla de funcionalidades y lista de validaciones aplicadas.
- Instrucciones de compilación manual (`javac`/`java`) y desde IDE.
- Ejemplo de sesión real en consola.
- Restricciones y convenciones del proyecto.

### Ajustes manuales realizados

Ninguno.

---

## Prompt 9 — Generación de IA_USO.md

### Prompt utilizado

> necesito ahora que me generes un IA_USO.md que contenga los prompts utilizados,
> que parte resolvio cada prompt, ajustes realizados manualmente y justificacion
> tecnica del uso de la IA

### Parte que resolvió

Generación de este mismo documento.

### Ajustes manuales realizados

Ninguno.

---

## Justificación técnica del uso de IA

### Por qué se utilizó IA en este proyecto

El uso de IA como asistente de desarrollo se justifica por las siguientes razones:

**1. Generación de código estructurado y repetitivo**
Las capas de entidades y DAOs siguen patrones muy definidos (POJOs, CRUD sobre
archivos). La IA genera este tipo de código de forma consistente, aplicando
buenas prácticas como JavaDoc, separación de responsabilidades y nombres
descriptivos, reduciendo el tiempo de escritura de código boilerplate.

**2. Aplicación correcta de la arquitectura por capas**
La IA respetó en todo momento las restricciones de comunicación entre capas:
la presentación no accedió a DAOs, los servicios no manipularon archivos, y
las entidades no contuvieron lógica. Esto evidencia que los prompts bien
estructurados guían la generación de código arquitecturalmente correcto.

**3. Diagnóstico y corrección de errores de compilación**
Ante errores concretos de compilación (con mensaje y línea exacta), la IA
identificó la causa raíz (incompatibilidad de versión de Java) y aplicó la
corrección mínima necesaria sin reescribir código no afectado. Esto demuestra
un uso eficiente: no reemplazar todo, sino corregir lo puntual.

**4. Generación de documentación técnica**
La IA generó `CHANGELOG.md`, `README.md` e `IA_USO.md` a partir del contexto
acumulado de la conversación, sin necesidad de re-explicar el proyecto. Esto
muestra cómo la IA puede funcionar como documentador técnico cuando el
historial de interacción está bien construido.

**5. Consistencia a lo largo del proyecto**
Al generarse todas las capas en la misma sesión, la IA mantuvo coherencia en
convenciones de nombres, estilo de código, mensajes de error y patrones de
diseño entre todos los archivos.

---

## Valoración crítica

### Lo que funcionó bien

- Los prompts detallados con restricciones explícitas (`no incluir lógica de
  negocio`, `no acceso directo a archivos`) produjeron código que respetó la
  arquitectura desde la primera generación.
- La IA infirió decisiones de diseño no solicitadas explícitamente, como el
  centinela `"PENDIENTE"`, el `split` con límite, o la reutilización de setters
  en constructores, que mejoran la calidad del código.
- La corrección de errores fue quirúrgica: solo se modificó lo necesario.

### Limitaciones observadas

- La IA no detectó proactivamente la incompatibilidad con Java 8 durante la
  generación inicial. Fue necesario compilar y recibir el error para que la
  corrección ocurriera. Un prompt más explícito indicando `"compatible con
  Java 8"` desde el inicio habría evitado estos dos ciclos de corrección.
- La IA no puede verificar que el código compile o ejecute correctamente por
  sí sola; la validación en entorno real sigue siendo responsabilidad del
  desarrollador.

### Lección aprendida

> Especificar la versión de Java objetivo desde el primer prompt es fundamental
> para evitar incompatibilidades sintácticas que solo se detectan al compilar.
