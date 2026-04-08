package accesodatos;

import entidades.Acceso;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos para la entidad {@link Acceso}.
 * <p>
 * Persiste y recupera registros de acceso desde el archivo {@value #ARCHIVO}.
 * Cada línea sigue el formato:
 * {@code idUsuario,fechaHoraEntrada,fechaHoraSalida}
 * </p>
 * <p>
 * Las fechas se almacenan en formato ISO-8601 usando
 * {@link LocalDateTime#toString()} y se recuperan con
 * {@link LocalDateTime#parse(CharSequence)}.
 * Cuando la salida aún no ha sido registrada, el tercer campo se almacena
 * como la cadena literal {@value #SALIDA_PENDIENTE}.
 * </p>
 *
 * @author Sistema de Control de Acceso
 * @version 1.0
 */
public class AccesoDAO {

    // -------------------------------------------------------------------------
    // Constantes
    // -------------------------------------------------------------------------

    /** Ruta del archivo de persistencia de accesos. */
    private static final String ARCHIVO = "accesos.txt";

    /** Delimitador de campos dentro de cada línea del archivo. */
    private static final String DELIMITADOR = ",";

    /**
     * Valor centinela que indica que el usuario aún no ha registrado su salida.
     */
    private static final String SALIDA_PENDIENTE = "PENDIENTE";

    // -------------------------------------------------------------------------
    // Escritura: entrada
    // -------------------------------------------------------------------------

    /**
     * Registra la entrada de un usuario agregando una nueva línea al archivo.
     * <p>
     * Si el usuario ya tiene una entrada sin salida registrada, la operación
     * es ignorada para evitar duplicados inconsistentes.
     * </p>
     *
     * @param acceso acceso con al menos {@code idUsuario} y
     *               {@code fechaHoraEntrada} definidos; no debe ser {@code null}
     */
    public void registrarEntrada(Acceso acceso) {
        if (tieneEntradaPendiente(acceso.getIdUsuario())) {
            System.out.println("Advertencia: el usuario '" + acceso.getIdUsuario()
                    + "' ya tiene una entrada sin salida registrada.");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
            bw.write(serializar(acceso));
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error al registrar entrada: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Escritura: salida
    // -------------------------------------------------------------------------

    /**
     * Busca el registro de acceso abierto (sin salida) del usuario indicado
     * y lo completa con la hora actual, reescribiendo el archivo.
     *
     * @param idUsuario identificador del usuario que registra su salida
     * @return {@code true} si se encontró y actualizó el registro;
     *         {@code false} si el usuario no tenía una entrada pendiente
     */
    public boolean registrarSalida(String idUsuario) {
        List<Acceso> accesos = listarAccesos();
        boolean actualizado = false;

        for (Acceso acceso : accesos) {
            if (acceso.getIdUsuario().equals(idUsuario) && acceso.getFechaHoraSalida() == null) {
                acceso.setFechaHoraSalida(LocalDateTime.now());
                actualizado = true;
                break; // Solo se actualiza el primer registro pendiente
            }
        }

        if (!actualizado) {
            return false;
        }

        reescribirArchivo(accesos);
        return true;
    }

    // -------------------------------------------------------------------------
    // Lectura
    // -------------------------------------------------------------------------

    /**
     * Lee el archivo y retorna todos los registros de acceso.
     *
     * @return lista de accesos; vacía si el archivo no existe o está vacío
     */
    public List<Acceso> listarAccesos() {
        List<Acceso> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    lista.add(deserializar(linea));
                }
            }
        } catch (FileNotFoundException e) {
            // Archivo aún no creado: se retorna lista vacía
        } catch (IOException e) {
            System.err.println("Error al leer accesos: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Retorna todos los registros de acceso asociados a un usuario específico.
     *
     * @param idUsuario identificador del usuario a filtrar
     * @return lista de accesos del usuario; vacía si no tiene registros
     */
    public List<Acceso> obtenerAccesosPorUsuario(String idUsuario) {
        List<Acceso> resultado = new ArrayList<>();

        for (Acceso acceso : listarAccesos()) {
            if (acceso.getIdUsuario().equals(idUsuario)) {
                resultado.add(acceso);
            }
        }

        return resultado;
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares privados
    // -------------------------------------------------------------------------

    /**
     * Indica si un usuario tiene actualmente una entrada sin salida registrada.
     *
     * @param idUsuario identificador del usuario a verificar
     * @return {@code true} si existe al menos un acceso abierto para ese usuario
     */
    private boolean tieneEntradaPendiente(String idUsuario) {
        for (Acceso acceso : listarAccesos()) {
            if (acceso.getIdUsuario().equals(idUsuario) && acceso.getFechaHoraSalida() == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convierte un {@link Acceso} a su representación en cadena para el archivo.
     * <p>
     * Si la salida es {@code null}, se usa el valor centinela {@value #SALIDA_PENDIENTE}.
     * </p>
     *
     * @param acceso acceso a serializar
     * @return línea con formato {@code idUsuario,fechaHoraEntrada,fechaHoraSalida}
     */
    private String serializar(Acceso acceso) {
        String salida = (acceso.getFechaHoraSalida() != null)
                ? acceso.getFechaHoraSalida().toString()
                : SALIDA_PENDIENTE;

        return acceso.getIdUsuario() + DELIMITADOR
                + acceso.getFechaHoraEntrada().toString() + DELIMITADOR
                + salida;
    }

    /**
     * Construye un {@link Acceso} a partir de una línea del archivo.
     * <p>
     * Si el tercer campo es {@value #SALIDA_PENDIENTE}, la {@code fechaHoraSalida}
     * del objeto resultante será {@code null}.
     * </p>
     *
     * @param linea línea con formato {@code idUsuario,fechaHoraEntrada,fechaHoraSalida}
     * @return instancia de {@link Acceso} con los datos de la línea
     */
    private Acceso deserializar(String linea) {
        String[] partes = linea.split(DELIMITADOR, 3);

        String idUsuario       = partes[0];
        LocalDateTime entrada  = LocalDateTime.parse(partes[1]);
        LocalDateTime salida   = SALIDA_PENDIENTE.equals(partes[2])
                                    ? null
                                    : LocalDateTime.parse(partes[2]);

        return new Acceso(idUsuario, entrada, salida);
    }

    /**
     * Sobreescribe el archivo con la lista de accesos proporcionada.
     * Usado internamente tras actualizar una salida.
     *
     * @param accesos lista de accesos a escribir
     */
    private void reescribirArchivo(List<Acceso> accesos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO, false))) {
            for (Acceso acceso : accesos) {
                bw.write(serializar(acceso));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al reescribir archivo de accesos: " + e.getMessage());
        }
    }
}
