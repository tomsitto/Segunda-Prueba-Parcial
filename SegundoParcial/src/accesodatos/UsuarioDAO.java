package accesodatos;

import entidades.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos para la entidad {@link Usuario}.
 * <p>
 * Persiste y recupera usuarios desde el archivo {@value #ARCHIVO}.
 * Cada línea del archivo sigue el formato: {@code id,nombre,rol}
 * </p>
 *
 * @author Sistema de Control de Acceso
 * @version 1.0
 */
public class UsuarioDAO {

    // -------------------------------------------------------------------------
    // Constantes
    // -------------------------------------------------------------------------

    /** Ruta del archivo de persistencia de usuarios. */
    private static final String ARCHIVO = "usuarios.txt";

    /** Delimitador de campos dentro de cada línea del archivo. */
    private static final String DELIMITADOR = ",";

    // -------------------------------------------------------------------------
    // Escritura
    // -------------------------------------------------------------------------

    /**
     * Agrega un usuario al final del archivo sin sobrescribir el contenido existente.
     * <p>
     * Si el usuario ya existe (mismo {@code id}), la operación es ignorada
     * para evitar duplicados.
     * </p>
     *
     * @param usuario usuario a guardar; no debe ser {@code null}
     */
    public void guardarUsuario(Usuario usuario) {
        if (existeUsuario(usuario.getId())) {
            System.out.println("Advertencia: el usuario con id '" + usuario.getId() + "' ya existe. No se guardó.");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
            bw.write(serializar(usuario));
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Lectura
    // -------------------------------------------------------------------------

    /**
     * Lee el archivo y retorna todos los usuarios registrados.
     *
     * @return lista de usuarios; vacía si el archivo no existe o está vacío
     */
    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();

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
            System.err.println("Error al leer usuarios: " + e.getMessage());
        }

        return lista;
    }

    // -------------------------------------------------------------------------
    // Eliminación
    // -------------------------------------------------------------------------

    /**
     * Elimina el usuario con el {@code id} indicado reescribiendo el archivo
     * sin incluirlo.
     *
     * @param id identificador del usuario a eliminar
     * @return {@code true} si el usuario fue encontrado y eliminado;
     *         {@code false} si no existía
     */
    public boolean eliminarUsuario(String id) {
        List<Usuario> usuarios = listarUsuarios();
        boolean encontrado = usuarios.removeIf(u -> u.getId().equals(id));

        if (!encontrado) {
            return false;
        }

        reescribirArchivo(usuarios);
        return true;
    }

    // -------------------------------------------------------------------------
    // Verificación
    // -------------------------------------------------------------------------

    /**
     * Verifica si existe un usuario con el {@code id} indicado.
     *
     * @param id identificador a buscar
     * @return {@code true} si el usuario existe; {@code false} en caso contrario
     */
    public boolean existeUsuario(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty() && linea.startsWith(id + DELIMITADOR)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            // Archivo aún no creado: el usuario no existe
        } catch (IOException e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares privados
    // -------------------------------------------------------------------------

    /**
     * Convierte un {@link Usuario} a su representación en cadena para el archivo.
     *
     * @param usuario usuario a serializar
     * @return línea con formato {@code id,nombre,rol}
     */
    private String serializar(Usuario usuario) {
        return usuario.getId() + DELIMITADOR
                + usuario.getNombre() + DELIMITADOR
                + usuario.getRol();
    }

    /**
     * Construye un {@link Usuario} a partir de una línea del archivo.
     *
     * @param linea línea con formato {@code id,nombre,rol}
     * @return instancia de {@link Usuario} con los datos de la línea
     */
    private Usuario deserializar(String linea) {
        String[] partes = linea.split(DELIMITADOR, 3);
        return new Usuario(partes[0], partes[1], partes[2]);
    }

    /**
     * Sobreescribe el archivo con la lista de usuarios proporcionada.
     * Usado internamente tras una eliminación.
     *
     * @param usuarios lista de usuarios a escribir
     */
    private void reescribirArchivo(List<Usuario> usuarios) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO, false))) {
            for (Usuario u : usuarios) {
                bw.write(serializar(u));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al reescribir archivo de usuarios: " + e.getMessage());
        }
    }
}
