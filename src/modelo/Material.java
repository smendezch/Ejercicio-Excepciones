package modelo;

import excepciones.MaterialNoDisponibleException;

public abstract class Material implements Prestable {
    private String id;
    private String titulo;
    private boolean prestado;

    public Material(String id, String titulo) {
        this.id = id;
        this.titulo = titulo;
        this.prestado = false;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    @Override
    public boolean estaPrestado() {
        return prestado;
    }

    public void setPrestado(boolean prestado) {
        this.prestado = prestado;
    }

    @Override
    public void prestar() throws MaterialNoDisponibleException {
        if (this.prestado) {
            throw new MaterialNoDisponibleException("El material '" + titulo + "' (ID: " + id + ") ya está prestado.");
        }
        this.prestado = true;
    }

    @Override
    public void devolver() {
        this.prestado = false;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Título: " + titulo + " | Prestado: " + (prestado ? "Sí" : "No");
    }
}
