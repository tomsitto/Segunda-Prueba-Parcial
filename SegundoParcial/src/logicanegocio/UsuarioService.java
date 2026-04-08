package logicanegocio;

import accesodatos.UsuarioDAO;
import entidades.Usuario;

import java.util.List;

/**
 * Servicio de lógica de negocio para la gestión de {@link Usuario}.
 * <p>
 * Actúa como intermediario entre la capa de presentación y {@link UsuarioDAO},
 * aplicando validaciones de negocio antes de delegar las operaciones de
 * persistencia.
 * </p>
 *
 * @author Sistema de Control de Acceso
 * @version 1.0
 */
public class UsuarioService {

    // -------------------------------------------------------------------------
    // Dependencias
    // -------------------------------------------------------------------------

    /** DAO utilizado para persistir y recuperar usuarios. */
    private final UsuarioDAO usuarioDAO;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Crea una instancia de {@code UsuarioService} con su propio {@link UsuarioDAO}.
     */
    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    // -------------------------------------------------------------------------
    // Operaciones de negocio
    // -------------------------------------------------------------------------

    /**
     * Registra un nuevo usuario en el sistema tras validar los datos recibidos.
     * <p>
     * Validaciones aplicadas (en orden):
     * <ol>
     *   <li>Ningún campo puede ser {@code null} o estar en blanco.</li>
     *   <li>El rol debe ser {@link Usuario#ESTUDIANTE} o {@link Usuario#DOCENTE}.</li>
     *   <li>El {@code id} no puede estar ya registrado.</li>
     * </ol>
     * </p>
     *
     * @param id     identificador único del usuario
     * @param nombre nombre completo del usuario
     * @param rol    rol del usuario; debe ser {@code "ESTUDIANTE"} o {@code "DOCENTE"}
     * @return {@code true} si el usuario fue registrado con éxito;
     *         {@code false} si alguna validación falló
     */
    public boolean registrarUsuario(String id, String nombre, String rol) {
        // --- Validación 1: campos vacíos ---
        if (estaVacio(id)) {
            System.err.println("Error: el ID del usuario no puede estar vacío.");
            return false;
        }
        if (estaVacio(nombre)) {
            System.err.println("Error: el nombre del usuario no puede estar vacío.");
            return false;
        }
        if (estaVacio(rol)) {
            System.err.println("Error: el rol del usuario no puede estar vacío.");
            return false;
        }

        // --- Validación 2: rol válido ---
        if (!rolEsValido(rol)) {
            System.err.println("Error: rol '" + rol + "' no reconocido. "
                    + "Use '" + Usuario.ESTUDIANTE + "' o '" + Usuario.DOCENTE + "'.");
            return false;
        }

        // --- Validación 3: ID duplicado ---
        if (usuarioDAO.existeUsuario(id)) {
            System.err.println("Error: ya existe un usuario con el ID '" + id + "'.");
            return false;
        }

        usuarioDAO.guardarUsuario(new Usuario(id, nombre, rol.toUpperCase().trim()));
        return true;
    }

    /**
     * Retorna la lista de todos los usuarios registrados en el sistema.
     *
     * @return lista de usuarios; vacía si no hay ninguno registrado
     */
    public List<Usuario> obtenerUsuarios() {
        return usuarioDAO.listarUsuarios();
    }

    /**
     * Elimina el usuario con el {@code id} indicado.
     * <p>
     * Si el usuario no existe, retorna {@code false} e informa el motivo.
     * </p>
     *
     * @param id identificador del usuario a eliminar
     * @return {@code true} si el usuario fue eliminado; {@code false} si no existía
     */
    public boolean eliminarUsuario(String id) {
        if (estaVacio(id)) {
            System.err.println("Error: el ID para eliminar no puede estar vacío.");
            return false;
        }

        boolean eliminado = usuarioDAO.eliminarUsuario(id);
        if (!eliminado) {
            System.err.println("Error: no se encontró ningún usuario con el ID '" + id + "'.");
        }
        return eliminado;
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
     * Indica si el rol proporcionado corresponde a uno de los roles permitidos.
     *
     * @param rol rol a validar
     * @return {@code true} si es {@code ESTUDIANTE} o {@code DOCENTE}
     */
    private boolean rolEsValido(String rol) {
        String rolNormalizado = rol.toUpperCase().trim();
        return Usuario.ESTUDIANTE.equals(rolNormalizado) || Usuario.DOCENTE.equals(rolNormalizado);
    }
}