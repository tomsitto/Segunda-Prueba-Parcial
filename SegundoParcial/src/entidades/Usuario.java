package entidades;

/**
 * Representa un usuario del Sistema de Control de Acceso a Laboratorio.
 * <p>
 * Un usuario puede tener el rol de {@code ESTUDIANTE} o {@code DOCENTE},
 * definidos como constantes en esta misma clase.
 * </p>
 *
 * @author Sistema de Control de Acceso
 * @version 1.0
 */
public class Usuario {

    // -------------------------------------------------------------------------
    // Constantes de rol
    // -------------------------------------------------------------------------

    /** Rol asignado a usuarios de tipo estudiante. */
    public static final String ESTUDIANTE = "ESTUDIANTE";

    /** Rol asignado a usuarios de tipo docente. */
    public static final String DOCENTE = "DOCENTE";

    // -------------------------------------------------------------------------
    // Atributos privados
    // -------------------------------------------------------------------------

    /** Identificador único del usuario. */
    private String id;

    /** Nombre completo del usuario. */
    private String nombre;

    /** Rol del usuario dentro del sistema ({@code ESTUDIANTE} o {@code DOCENTE}). */
    private String rol;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    /**
     * Constructor vacío requerido para frameworks y serialización.
     */
    public Usuario() {
    }

    /**
     * Constructor con todos los atributos.
     *
     * @param id     identificador único del usuario; no debe ser {@code null} ni vacío
     * @param nombre nombre completo del usuario; no debe ser {@code null} ni vacío
     * @param rol    rol del usuario ({@link #ESTUDIANTE} o {@link #DOCENTE});
     *               no debe ser {@code null} ni vacío
     */
    public Usuario(String id, String nombre, String rol) {
        setId(id);
        setNombre(nombre);
        setRol(rol);
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
    // -------------------------------------------------------------------------

    /**
     * Retorna el identificador del usuario.
     *
     * @return id del usuario
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador del usuario.
     *
     * @param id identificador único; no debe ser {@code null} ni vacío
     * @throws IllegalArgumentException si {@code id} es {@code null} o está en blanco
     */
    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El id del usuario no puede ser nulo o vacío.");
        }
        this.id = id.trim();
    }

    /**
     * Retorna el nombre del usuario.
     *
     * @return nombre completo del usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     *
     * @param nombre nombre completo; no debe ser {@code null} ni vacío
     * @throws IllegalArgumentException si {@code nombre} es {@code null} o está en blanco
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario no puede ser nulo o vacío.");
        }
        this.nombre = nombre.trim();
    }

    /**
     * Retorna el rol del usuario.
     *
     * @return rol asignado ({@code ESTUDIANTE} o {@code DOCENTE})
     */
    public String getRol() {
        return rol;
    }

    /**
     * Establece el rol del usuario.
     *
     * @param rol rol a asignar; no debe ser {@code null} ni vacío
     * @throws IllegalArgumentException si {@code rol} es {@code null} o está en blanco
     */
    public void setRol(String rol) {
        if (rol == null || rol.trim().isEmpty()) {
            throw new IllegalArgumentException("El rol del usuario no puede ser nulo o vacío.");
        }
        this.rol = rol.trim();
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    /**
     * Retorna una representación legible del usuario.
     *
     * @return cadena con los datos del usuario
     */
    @Override
    public String toString() {
        return "Usuario {" +
                "\n  id      = '" + id + '\'' +
                ",\n  nombre  = '" + nombre + '\'' +
                ",\n  rol     = '" + rol + '\'' +
                "\n}";
    }
}