package modelo;

import excepciones.MaximoPrestamosException;
import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private static final int MAX_PRESTAMOS = 3;
    private int id;
    private String nombre;
    private List<Material> prestamos;

    public Usuario(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.prestamos = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Material> getPrestamos() {
        return prestamos;
    }

    public void prestarMaterial(Material material) throws MaximoPrestamosException {
        if (prestamos.size() >= MAX_PRESTAMOS) {
            throw new MaximoPrestamosException(
                "El usuario " + nombre + " (ID: " + id + ") ha alcanzado el límite máximo de " + MAX_PRESTAMOS + " préstamos activos."
            );
        }
        prestamos.add(material);
    }

    public void devolverMaterial(Material material) {
        prestamos.remove(material);
    }

    public int cantidadPrestamos() {
        return prestamos.size();
    }

    @Override
    public String toString() {
        return "Usuario ID: " + id + " | Nombre: " + nombre + " | Préstamos activos: " + cantidadPrestamos();
    }
}
