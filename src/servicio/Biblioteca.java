package servicio;

import excepciones.MaterialNoDisponibleException;
import excepciones.MaterialNoEncontradoException;
import excepciones.MaximoPrestamosException;
import excepciones.OperacionBibliotecaException;
import excepciones.UsuarioNoEncontradoException;
import modelo.Libro;
import modelo.Material;
import modelo.Revista;
import modelo.Usuario;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Biblioteca {
    private static final String ARCHIVO_MATERIALES = "materiales.txt";
    private List<Material> materiales;
    private List<Usuario> usuarios;

    public Biblioteca() {
        this.materiales = new ArrayList<>();
        this.usuarios = new ArrayList<>();
    }

    public List<Material> getMateriales() {
        return materiales;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void registrarMaterial(Material material) throws OperacionBibliotecaException {
        for (Material m : materiales) {
            if (m.getId().equalsIgnoreCase(material.getId())) {
                throw new OperacionBibliotecaException(
                    "Error al registrar material: ya existe un material con ID " + material.getId(),
                    new IllegalArgumentException("ID duplicado")
                );
            }
        }
        materiales.add(material);
        guardarMaterialesEnArchivo();
    }

    public void registrarUsuario(Usuario usuario) throws OperacionBibliotecaException {
        for (Usuario u : usuarios) {
            if (u.getId() == usuario.getId()) {
                throw new OperacionBibliotecaException(
                    "Error al registrar usuario: ya existe un usuario con ID " + usuario.getId(),
                    new IllegalArgumentException("ID de usuario duplicado")
                );
            }
        }
        usuarios.add(usuario);
    }

    public Material buscarMaterial(String id) throws MaterialNoEncontradoException {
        for (Material m : materiales) {
            if (m.getId().equalsIgnoreCase(id)) {
                return m;
            }
        }
        throw new MaterialNoEncontradoException("No se encontró ningún material con el ID: " + id);
    }

    public Usuario buscarUsuario(int id) throws UsuarioNoEncontradoException {
        for (Usuario u : usuarios) {
            if (u.getId() == id) {
                return u;
            }
        }
        throw new UsuarioNoEncontradoException("No se encontró ningún usuario con el ID: " + id);
    }

    public void prestarMaterial(int usuarioId, String materialId)
            throws UsuarioNoEncontradoException,
                   MaterialNoEncontradoException,
                   MaterialNoDisponibleException,
                   MaximoPrestamosException,
                   OperacionBibliotecaException {

        Usuario usuario = buscarUsuario(usuarioId);
        Material material = buscarMaterial(materialId);

        if (material.estaPrestado()) {
            throw new MaterialNoDisponibleException(
                "El material '" + material.getTitulo() + "' (ID: " + materialId + ") ya está prestado."
            );
        }

        usuario.prestarMaterial(material);
        try {
            material.prestar();
        } catch (MaterialNoDisponibleException e) {
            usuario.devolverMaterial(material);
            throw e;
        }
        guardarMaterialesEnArchivo();
    }

    public void devolverMaterial(int usuarioId, String materialId)
            throws UsuarioNoEncontradoException,
                   MaterialNoEncontradoException,
                   OperacionBibliotecaException {

        Usuario usuario = buscarUsuario(usuarioId);
        Material material = buscarMaterial(materialId);

        if (!material.estaPrestado() || !usuario.getPrestamos().contains(material)) {
            throw new OperacionBibliotecaException(
                "El usuario " + usuario.getNombre() + " no tiene prestado el material con ID: " + materialId,
                new IllegalStateException("Devolución inválida")
            );
        }

        material.devolver();
        usuario.devolverMaterial(material);
        guardarMaterialesEnArchivo();
    }

    public void guardarMaterialesEnArchivo() throws OperacionBibliotecaException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_MATERIALES))) {
            for (Material m : materiales) {
                if (m instanceof Libro libro) {
                    writer.write(String.join(";",
                        "LIBRO",
                        libro.getId(),
                        libro.getTitulo(),
                        libro.getAutor(),
                        libro.getIsbn(),
                        String.valueOf(libro.estaPrestado())
                    ));
                } else if (m instanceof Revista revista) {
                    writer.write(String.join(";",
                        "REVISTA",
                        revista.getId(),
                        revista.getTitulo(),
                        String.valueOf(revista.getNumero()),
                        String.valueOf(revista.estaPrestado())
                    ));
                }
                writer.newLine();
            }
        } catch (IOException e) {
            throw new OperacionBibliotecaException("No se pudo guardar el estado de los materiales en el archivo.", e);
        }
    }

    public void cargarMaterialesDesdeArchivo() throws OperacionBibliotecaException {
        File archivo = new File(ARCHIVO_MATERIALES);
        if (!archivo.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            materiales.clear();
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) continue;
                String[] partes = linea.split(";");
                String tipo = partes[0];
                if ("LIBRO".equals(tipo) && partes.length == 6) {
                    Libro libro = new Libro(partes[1], partes[2], partes[3], partes[4]);
                    libro.setPrestado(Boolean.parseBoolean(partes[5]));
                    materiales.add(libro);
                } else if ("REVISTA".equals(tipo) && partes.length == 5) {
                    Revista revista = new Revista(partes[1], partes[2], Integer.parseInt(partes[3]));
                    revista.setPrestado(Boolean.parseBoolean(partes[4]));
                    materiales.add(revista);
                }
            }
        } catch (IOException | NumberFormatException e) {
            throw new OperacionBibliotecaException("Error al cargar los materiales desde el archivo.", e);
        }
    }
}
