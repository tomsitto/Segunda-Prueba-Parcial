package presentacion;

import entidades.Acceso;
import entidades.Usuario;
import logicanegocio.AccesoService;
import logicanegocio.UsuarioService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Punto de entrada del Sistema de Control de Acceso a Laboratorio.
 * <p>
 * Presenta un menú interactivo por consola y delega toda la lógica
 * exclusivamente a {@link UsuarioService} y {@link AccesoService}.
 * </p>
 *
 * @author Sistema de Control de Acceso
 * @version 1.0
 */
public class Main {

    // -------------------------------------------------------------------------
    // Constantes de presentación
    // -------------------------------------------------------------------------

    /** Formato usado para mostrar fechas y horas al usuario. */
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss");

    /** Línea separadora para secciones del menú. */
    private static final String SEPARADOR =
            "══════════════════════════════════════════════════";

    /** Línea divisoria más corta para sub-secciones. */
    private static final String DIVISOR =
            "──────────────────────────────────────────────────";

    // -------------------------------------------------------------------------
    // Servicios
    // -------------------------------------------------------------------------

    private static final UsuarioService usuarioService = new UsuarioService();
    private static final AccesoService  accesoService  = new AccesoService();

    // -------------------------------------------------------------------------
    // Scanner compartido
    // -------------------------------------------------------------------------

    private static final Scanner scanner = new Scanner(System.in);

    // =========================================================================
    // ENTRY POINT
    // =========================================================================

    public static void main(String[] args) {
        mostrarBienvenida();

        int opcion;
        do {
            mostrarMenu();
            opcion = leerOpcion();
            procesarOpcion(opcion);
        } while (opcion != 0);

        scanner.close();
    }

    // =========================================================================
    // MENÚ PRINCIPAL
    // =========================================================================

    /** Imprime el menú principal con todas las opciones disponibles. */
    private static void mostrarMenu() {
        System.out.println("\n" + SEPARADOR);
        System.out.println("         MENÚ PRINCIPAL");
        System.out.println(SEPARADOR);
        System.out.println("  1. Registrar usuario");
        System.out.println("  2. Listar usuarios");
        System.out.println("  3. Eliminar usuario");
        System.out.println(DIVISOR);
        System.out.println("  4. Registrar entrada al laboratorio");
        System.out.println("  5. Registrar salida del laboratorio");
        System.out.println("  6. Ver historial de usuario");
        System.out.println("  7. Ver tiempo total en laboratorio");
        System.out.println(DIVISOR);
        System.out.println("  0. Salir");
        System.out.println(SEPARADOR);
        System.out.print("  Seleccione una opción: ");
    }

    /**
     * Enruta la opción seleccionada al método correspondiente.
     *
     * @param opcion número de opción elegida por el usuario
     */
    private static void procesarOpcion(int opcion) {
        System.out.println();
        switch (opcion) {
            case 1:
                registrarUsuario();
                break;
            case 2:
                listarUsuarios();
                break;
            case 3:
                eliminarUsuario();
                break;
            case 4:
                registrarEntrada();
                break;
            case 5:
                registrarSalida();
                break;
            case 6:
                verHistorial();
                break;
            case 7:
                verTiempoTotal();
                break;
            case 0:
                mostrarDespedida();
                break;
            default:
                mostrarError("Opción no válida. Por favor ingrese un número del 0 al 7.");
        }
    }

    // =========================================================================
    // OPCIÓN 1 — Registrar usuario
    // =========================================================================

    private static void registrarUsuario() {
        mostrarTitulo("REGISTRAR USUARIO");

        String id     = leerCampo("ID del usuario");
        String nombre = leerCampo("Nombre completo");

        System.out.println("  Roles disponibles: [1] ESTUDIANTE   [2] DOCENTE");
        System.out.print("  Seleccione el rol: ");
        String rolInput = scanner.nextLine().trim();
        String rol;
        if (rolInput.equals("1")) {
            rol = "ESTUDIANTE";
        } else if (rolInput.equals("2")) {
            rol = "DOCENTE";
        } else {
            rol = rolInput.toUpperCase();   // Se deja pasar; el servicio lo valida
        }

        boolean exito = usuarioService.registrarUsuario(id, nombre, rol);
        if (exito) {
            mostrarExito("Usuario registrado correctamente.");
            System.out.println("  ID     : " + id);
            System.out.println("  Nombre : " + nombre);
            System.out.println("  Rol    : " + rol);
        } else {
            mostrarError("No se pudo registrar el usuario. Revise los mensajes anteriores.");
        }
    }

    // =========================================================================
    // OPCIÓN 2 — Listar usuarios
    // =========================================================================

    private static void listarUsuarios() {
        mostrarTitulo("LISTA DE USUARIOS");

        List<Usuario> usuarios = usuarioService.obtenerUsuarios();

        if (usuarios.isEmpty()) {
            System.out.println("  No hay usuarios registrados.");
            return;
        }

        System.out.printf("  %-12s %-28s %-12s%n", "ID", "NOMBRE", "ROL");
        System.out.println("  " + DIVISOR);
        for (Usuario u : usuarios) {
            System.out.printf("  %-12s %-28s %-12s%n",
                    u.getId(), u.getNombre(), u.getRol());
        }
        System.out.println("  " + DIVISOR);
        System.out.println("  Total: " + usuarios.size() + " usuario(s)");
    }

    // =========================================================================
    // OPCIÓN 3 — Eliminar usuario
    // =========================================================================

    private static void eliminarUsuario() {
        mostrarTitulo("ELIMINAR USUARIO");

        String id = leerCampo("ID del usuario a eliminar");

        System.out.print("  ¿Confirma la eliminación del usuario '" + id + "'? (s/n): ");
        String confirmacion = scanner.nextLine().trim().toLowerCase();

        if (!confirmacion.equals("s")) {
            System.out.println("  Operación cancelada.");
            return;
        }

        boolean exito = usuarioService.eliminarUsuario(id);
        if (exito) {
            mostrarExito("Usuario '" + id + "' eliminado correctamente.");
        } else {
            mostrarError("No se pudo eliminar el usuario. Revise los mensajes anteriores.");
        }
    }

    // =========================================================================
    // OPCIÓN 4 — Registrar entrada
    // =========================================================================

    private static void registrarEntrada() {
        mostrarTitulo("REGISTRAR ENTRADA AL LABORATORIO");

        String idUsuario = leerCampo("ID del usuario");

        boolean exito = accesoService.registrarEntrada(idUsuario);
        if (exito) {
            mostrarExito("Entrada registrada correctamente.");
            System.out.println("  Usuario : " + idUsuario);
            System.out.println("  Hora    : " + java.time.LocalDateTime.now().format(FORMATO_FECHA));
        } else {
            mostrarError("No se pudo registrar la entrada. Revise los mensajes anteriores.");
        }
    }

    // =========================================================================
    // OPCIÓN 5 — Registrar salida
    // =========================================================================

    private static void registrarSalida() {
        mostrarTitulo("REGISTRAR SALIDA DEL LABORATORIO");

        String idUsuario = leerCampo("ID del usuario");

        boolean exito = accesoService.registrarSalida(idUsuario);
        if (exito) {
            mostrarExito("Salida registrada correctamente.");
            System.out.println("  Usuario : " + idUsuario);
            System.out.println("  Hora    : " + java.time.LocalDateTime.now().format(FORMATO_FECHA));
        } else {
            mostrarError("No se pudo registrar la salida. Revise los mensajes anteriores.");
        }
    }

    // =========================================================================
    // OPCIÓN 6 — Ver historial
    // =========================================================================

    private static void verHistorial() {
        mostrarTitulo("HISTORIAL DE ACCESOS");

        String idUsuario = leerCampo("ID del usuario");

        List<Acceso> historial = accesoService.obtenerHistorial(idUsuario);

        if (historial.isEmpty()) {
            System.out.println("  No se encontraron registros para el usuario '" + idUsuario + "'.");
            return;
        }

        System.out.printf("  %-5s  %-20s  %-20s  %-12s%n",
                "#", "ENTRADA", "SALIDA", "DURACIÓN");
        System.out.println("  " + DIVISOR);

        int numero = 1;
        for (Acceso acceso : historial) {
            String entrada  = acceso.getFechaHoraEntrada().format(FORMATO_FECHA);
            String salida   = (acceso.getFechaHoraSalida() != null)
                    ? acceso.getFechaHoraSalida().format(FORMATO_FECHA)
                    : "En laboratorio";
            String duracion = (acceso.getFechaHoraSalida() != null)
                    ? calcularDuracion(acceso)
                    : "-";

            System.out.printf("  %-5d  %-20s  %-20s  %-12s%n",
                    numero++, entrada, salida, duracion);
        }

        System.out.println("  " + DIVISOR);
        System.out.println("  Total de registros: " + historial.size());
    }

    // =========================================================================
    // OPCIÓN 7 — Tiempo total
    // =========================================================================

    private static void verTiempoTotal() {
        mostrarTitulo("TIEMPO TOTAL EN LABORATORIO");

        String idUsuario = leerCampo("ID del usuario");

        long totalMinutos = accesoService.calcularTiempoTotalMinutos(idUsuario);
        long horas        = totalMinutos / 60;
        long minutos      = totalMinutos % 60;

        System.out.println("  Usuario       : " + idUsuario);
        System.out.printf ("  Tiempo total  : %d h %02d min  (%d minutos en total)%n",
                horas, minutos, totalMinutos);

        if (totalMinutos == 0) {
            System.out.println("  (No hay accesos completados para este usuario)");
        }
    }

    // =========================================================================
    // UTILIDADES DE PRESENTACIÓN
    // =========================================================================

    /** Muestra la pantalla de bienvenida al iniciar el sistema. */
    private static void mostrarBienvenida() {
        System.out.println("\n" + SEPARADOR);
        System.out.println("   SISTEMA DE CONTROL DE ACCESO A LABORATORIO");
        System.out.println(SEPARADOR);
        System.out.println("   Bienvenido. Use el menú para navegar.");
        System.out.println(SEPARADOR);
    }

    /** Muestra el mensaje de despedida al salir. */
    private static void mostrarDespedida() {
        System.out.println(SEPARADOR);
        System.out.println("  Sistema cerrado. ¡Hasta luego!");
        System.out.println(SEPARADOR);
    }

    /**
     * Muestra el título de una sección del menú.
     *
     * @param titulo texto del título
     */
    private static void mostrarTitulo(String titulo) {
        System.out.println(SEPARADOR);
        System.out.println("   " + titulo);
        System.out.println(SEPARADOR);
    }

    /**
     * Muestra un mensaje de operación exitosa.
     *
     * @param mensaje texto a mostrar
     */
    private static void mostrarExito(String mensaje) {
        System.out.println("\n  ✔  " + mensaje);
    }

    /**
     * Muestra un mensaje de error o advertencia.
     *
     * @param mensaje texto a mostrar
     */
    private static void mostrarError(String mensaje) {
        System.out.println("\n  ✘  " + mensaje);
    }

    /**
     * Solicita al usuario que ingrese un campo de texto no vacío.
     * Repite la solicitud mientras el campo esté en blanco.
     *
     * @param etiqueta nombre descriptivo del campo
     * @return valor ingresado por el usuario (nunca vacío)
     */
    private static String leerCampo(String etiqueta) {
        String valor;
        do {
            System.out.print("  " + etiqueta + ": ");
            valor = scanner.nextLine().trim();
            if (valor.isEmpty()) {
                System.out.println("  ⚠  Este campo no puede estar vacío. Intente nuevamente.");
            }
        } while (valor.isEmpty());
        return valor;
    }

    /**
     * Lee y valida la opción numérica del menú principal.
     * Repite la lectura si el valor no es un entero.
     *
     * @return número entero ingresado por el usuario
     */
    private static int leerOpcion() {
        while (true) {
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.print("  ⚠  Ingrese únicamente un número entero: ");
            }
        }
    }

    /**
     * Calcula y formatea la duración de un acceso como cadena legible.
     *
     * @param acceso acceso con entrada y salida definidas
     * @return cadena con formato {@code Xh YYm}
     */
    private static String calcularDuracion(Acceso acceso) {
        long minutosTotales = java.time.temporal.ChronoUnit.MINUTES.between(
                acceso.getFechaHoraEntrada(),
                acceso.getFechaHoraSalida()
        );
        long h = minutosTotales / 60;
        long m = minutosTotales % 60;
        return h + "h " + String.format("%02d", m) + "m";
    }
}