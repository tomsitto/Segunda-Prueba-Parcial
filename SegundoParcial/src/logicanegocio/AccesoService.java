package logicanegocio;

import accesodatos.AccesoDAO;
import accesodatos.UsuarioDAO;
import entidades.Acceso;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de lógica de negocio para la gestión de {@link Acceso} al laboratorio.
 * <p>
 * Coordina las validaciones de negocio necesarias antes de delegar
 * las operaciones de persistencia a {@link AccesoDAO}. Consume también
 * {@link UsuarioDAO} para verificar la existencia de usuarios, sin
 * acceder directamente a ningún archivo.
 * </p>
 *
 * @author Sistema de Control de Acceso
 * @version 1.0
 */
public class AccesoService {

    // -------------------------------------------------------------------------
    // Dependencias
    // -------------------------------------------------------------------------

    /** DAO utilizado para persistir y recuperar registros de acceso. */
    private final AccesoDAO accesoDAO;

    /** DAO utilizado para verificar la existencia de usuarios. */
    private final UsuarioDAO usuarioDAO;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Crea una instancia de {@code AccesoService} con sus DAOs correspondientes.
     */
    public AccesoService() {
        this.accesoDAO  = new AccesoDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    // -------------------------------------------------------------------------
    // Operaciones de negocio
    // -------------------------------------------------------------------------

    /**
     * Registra la entrada de un usuario al laboratorio en el momento actual.
     * <p>
     * Validaciones aplicadas:
     * <ol>
     *   <li>El {@code idUsuario} no puede ser vacío.</li>
     *   <li>El usuario debe estar registrado en el sistema.</li>
     *   <li>El usuario no puede tener ya una entrada sin salida (dentro del laboratorio).</li>
     * </ol>
     * </p>
     *
     * @param idUsuario identificador del usuario que ingresa
     * @return {@code true} si la entrada fue registrada con éxito;
     *         {@code false} si alguna validación falló
     */
    public boolean registrarEntrada(String idUsuario) {
        if (estaVacio(idUsuario)) {
            System.err.println("Error: el ID de usuario no puede estar vacío.");
            return false;
        }

        if (!usuarioDAO.existeUsuario(idUsuario)) {
            System.err.println("Error: no existe un usuario con el ID '" + idUsuario + "'.");
            return false;
        }

        if (tieneEntradaPendiente(idUsuario)) {
            System.err.println("Error: el usuario '" + idUsuario
                    + "' ya tiene una entrada registrada sin salida. "
                    + "Debe registrar la salida antes de ingresar de nuevo.");
            return false;
        }

        accesoDAO.registrarEntrada(new Acceso(idUsuario, LocalDateTime.now(), null));
        return true;
    }

    /**
     * Registra la salida de un usuario del laboratorio en el momento actual.
     * <p>
     * Validaciones aplicadas:
     * <ol>
     *   <li>El {@code idUsuario} no puede ser vacío.</li>
     *   <li>El usuario debe estar registrado en el sistema.</li>
     *   <li>El usuario debe tener una entrada activa (sin salida registrada).</li>
     * </ol>
     * </p>
     *
     * @param idUsuario identificador del usuario que sale
     * @return {@code true} si la salida fue registrada con éxito;
     *         {@code false} si alguna validación falló
     */
    public boolean registrarSalida(String idUsuario) {
        if (estaVacio(idUsuario)) {
            System.err.println("Error: el ID de usuario no puede estar vacío.");
            return false;
        }

        if (!usuarioDAO.existeUsuario(idUsuario)) {
            System.err.println("Error: no existe un usuario con el ID '" + idUsuario + "'.");
            return false;
        }

        if (!tieneEntradaPendiente(idUsuario)) {
            System.err.println("Error: el usuario '" + idUsuario
                    + "' no tiene ninguna entrada activa. "
                    + "Debe registrar una entrada antes de registrar la salida.");
            return false;
        }

        return accesoDAO.registrarSalida(idUsuario);
    }

    /**
     * Retorna el historial completo de accesos de un usuario específico.
     *
     * @param idUsuario identificador del usuario
     * @return lista de accesos del usuario; vacía si no tiene registros
     */
    public List<Acceso> obtenerHistorial(String idUsuario) {
        if (estaVacio(idUsuario)) {
            System.err.println("Error: el ID de usuario no puede estar vacío.");
            return new ArrayList<>();
        }

        return accesoDAO.obtenerAccesosPorUsuario(idUsuario);
    }

    /**
     * Calcula el tiempo total que un usuario ha permanecido en el laboratorio,
     * sumando la duración de todos sus accesos con salida registrada.
     * <p>
     * Los accesos cuya salida aún es {@code null} (entrada activa) no se
     * incluyen en el cálculo.
     * </p>
     *
     * @param idUsuario identificador del usuario
     * @return total de minutos acumulados en el laboratorio; {@code 0} si no
     *         tiene accesos completados
     */
    public long calcularTiempoTotalMinutos(String idUsuario) {
        if (estaVacio(idUsuario)) {
            System.err.println("Error: el ID de usuario no puede estar vacío.");
            return 0L;
        }

        List<Acceso> historial = accesoDAO.obtenerAccesosPorUsuario(idUsuario);
        long totalMinutos = 0L;

        for (Acceso acceso : historial) {
            if (acceso.getFechaHoraSalida() != null) {
                long minutos = ChronoUnit.MINUTES.between(
                        acceso.getFechaHoraEntrada(),
                        acceso.getFechaHoraSalida()
                );
                totalMinutos += minutos;
            }
        }

        return totalMinutos;
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares privados
    // -------------------------------------------------------------------------

    /**
     * Indica si una cadena es {@code null} o está en blanco.
     *
     * @param valor cadena a evaluar
     * @return {@code true} si es nula o en blanco
     */
    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    /**
     * Determina si el usuario tiene actualmente una entrada activa,
     * es decir, un registro de acceso sin salida registrada.
     *
     * @param idUsuario identificador del usuario a verificar
     * @return {@code true} si existe al menos un acceso sin salida para ese usuario
     */
    private boolean tieneEntradaPendiente(String idUsuario) {
        return accesoDAO.obtenerAccesosPorUsuario(idUsuario)
                .stream()
                .anyMatch(a -> a.getFechaHoraSalida() == null);
    }
}