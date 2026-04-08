package entidades;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa un registro de acceso al laboratorio por parte de un usuario.
 * <p>
 * Almacena el identificador del usuario junto con las marcas de tiempo
 * de entrada y salida. La {@code fechaHoraSalida} puede ser {@code null}
 * mientras el usuario permanezca dentro del laboratorio.
 * </p>
 *
 * @author Sistema de Control de Acceso
 * @version 1.0
 */
public class Acceso {

    // -------------------------------------------------------------------------
    // Formato de fecha/hora para toString
    // -------------------------------------------------------------------------

    /** Patrón utilizado para mostrar fechas y horas de forma legible. */
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // -------------------------------------------------------------------------
    // Atributos privados
    // -------------------------------------------------------------------------

    /** Identificador del usuario que realiza el acceso. */
    private String idUsuario;

    /** Fecha y hora en que el usuario ingresó al laboratorio. */
    private LocalDateTime fechaHoraEntrada;

    /**
     * Fecha y hora en que el usuario salió del laboratorio.
     * Puede ser {@code null} si el usuario aún no ha salido.
     */
    private LocalDateTime fechaHoraSalida;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    /**
     * Constructor vacío requerido para frameworks y serialización.
     */
    public Acceso() {
    }

    /**
     * Constructor con todos los atributos.
     *
     * @param idUsuario       identificador del usuario; no debe ser {@code null} ni vacío
     * @param fechaHoraEntrada fecha y hora de entrada; no debe ser {@code null}
     * @param fechaHoraSalida  fecha y hora de salida; puede ser {@code null}
     *                         si el usuario aún no ha salido
     */
    public Acceso(String idUsuario, LocalDateTime fechaHoraEntrada, LocalDateTime fechaHoraSalida) {
        setIdUsuario(idUsuario);
        setFechaHoraEntrada(fechaHoraEntrada);
        this.fechaHoraSalida = fechaHoraSalida; // Permitido nulo: salida aún no registrada
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
    // -------------------------------------------------------------------------

    /**
     * Retorna el identificador del usuario asociado a este acceso.
     *
     * @return id del usuario
     */
    public String getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el identificador del usuario.
     *
     * @param idUsuario id del usuario; no debe ser {@code null} ni vacío
     * @throws IllegalArgumentException si {@code idUsuario} es {@code null} o está en blanco
     */
    public void setIdUsuario(String idUsuario) {
        if (idUsuario == null || idUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El idUsuario no puede ser nulo o vacío.");
        }
        this.idUsuario = idUsuario.trim();
    }

    /**
     * Retorna la fecha y hora de entrada al laboratorio.
     *
     * @return {@link LocalDateTime} de entrada
     */
    public LocalDateTime getFechaHoraEntrada() {
        return fechaHoraEntrada;
    }

    /**
     * Establece la fecha y hora de entrada al laboratorio.
     *
     * @param fechaHoraEntrada fecha/hora de entrada; no debe ser {@code null}
     * @throws IllegalArgumentException si {@code fechaHoraEntrada} es {@code null}
     */
    public void setFechaHoraEntrada(LocalDateTime fechaHoraEntrada) {
        if (fechaHoraEntrada == null) {
            throw new IllegalArgumentException("La fecha/hora de entrada no puede ser nula.");
        }
        this.fechaHoraEntrada = fechaHoraEntrada;
    }

    /**
     * Retorna la fecha y hora de salida del laboratorio.
     *
     * @return {@link LocalDateTime} de salida, o {@code null} si el usuario aún no ha salido
     */
    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    /**
     * Establece la fecha y hora de salida del laboratorio.
     * Se permite {@code null} para representar que el usuario aún está dentro.
     *
     * @param fechaHoraSalida fecha/hora de salida; puede ser {@code null}
     */
    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    /**
     * Retorna una representación legible del registro de acceso.
     *
     * @return cadena con los datos del acceso
     */
    @Override
    public String toString() {
        String salida = (fechaHoraSalida != null)
                ? fechaHoraSalida.format(FORMATO)
                : "Aún en laboratorio";

        return "Acceso {" +
                "\n  idUsuario        = '" + idUsuario + '\'' +
                ",\n  fechaHoraEntrada = " + fechaHoraEntrada.format(FORMATO) +
                ",\n  fechaHoraSalida  = " + salida +
                "\n}";
    }
}