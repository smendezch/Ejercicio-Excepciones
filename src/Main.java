import excepciones.BibliotecaException;
import modelo.Libro;
import modelo.Material;
import modelo.Revista;
import modelo.Usuario;
import servicio.Biblioteca;
import util.RegistroErrores;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();

        System.out.println("====================================================");
        System.out.println("  SIMULACIÓN AUTOMÁTICA DE ESCENARIOS OBLIGATORIOS  ");
        System.out.println("====================================================\n");

        ejecutarSimulacionObligatoria(biblioteca);

        System.out.println("\n====================================================");
        System.out.println("     MENÚ INTERACTIVO DEL SISTEMA DE BIBLIOTECA     ");
        System.out.println("====================================================");

        ejecutarMenuInteractivo(biblioteca);
    }

    private static void ejecutarSimulacionObligatoria(Biblioteca biblioteca) {
        // 1. Registrar materiales y usuarios iniciales
        try {
            biblioteca.registrarMaterial(new Libro("L001", "Clean Code", "Robert C. Martin", "978-0132350884"));
            biblioteca.registrarMaterial(new Libro("L002", "Effective Java", "Joshua Bloch", "978-0134685991"));
            biblioteca.registrarMaterial(new Libro("L003", "Design Patterns", "Erich Gamma", "978-0201633610"));
            biblioteca.registrarMaterial(new Revista("R001", "National Geographic", 205));

            biblioteca.registrarUsuario(new Usuario(1, "Ana Gómez"));
            biblioteca.registrarUsuario(new Usuario(2, "Carlos Pérez"));
            System.out.println("[OK] Materiales y usuarios iniciales registrados correctamente.\n");
        } catch (BibliotecaException e) {
            System.out.println("[ERROR] " + e.getMessage());
            RegistroErrores.registrar(e);
        }

        // Caso 1: Préstamo exitoso
        System.out.println("--- Caso 1: Préstamo exitoso ---");
        try {
            biblioteca.prestarMaterial(1, "L001");
            System.out.println("Préstamo realizado con éxito: Usuario 1 -> L001");
        } catch (BibliotecaException e) {
            System.out.println("Error: " + e.getMessage());
            RegistroErrores.registrar(e);
        } finally {
            System.out.println("Operación finalizada.\n");
        }

        // Caso 2: Intentar prestar un material ya prestado
        System.out.println("--- Caso 2: Intentar prestar un material ya prestado ---");
        try {
            biblioteca.prestarMaterial(2, "L001");
        } catch (BibliotecaException e) {
            System.out.println("Excepción capturada (" + e.getClass().getSimpleName() + "): " + e.getMessage());
            RegistroErrores.registrar(e);
        } finally {
            System.out.println("Operación finalizada.\n");
        }

        // Caso 3: Intentar prestar un material inexistente
        System.out.println("--- Caso 3: Intentar prestar un material inexistente ---");
        try {
            biblioteca.prestarMaterial(1, "L999");
        } catch (BibliotecaException e) {
            System.out.println("Excepción capturada (" + e.getClass().getSimpleName() + "): " + e.getMessage());
            RegistroErrores.registrar(e);
        } finally {
            System.out.println("Operación finalizada.\n");
        }

        // Caso 4: Intentar prestar con usuario inexistente
        System.out.println("--- Caso 4: Intentar prestar con usuario inexistente ---");
        try {
            biblioteca.prestarMaterial(99, "L002");
        } catch (BibliotecaException e) {
            System.out.println("Excepción capturada (" + e.getClass().getSimpleName() + "): " + e.getMessage());
            RegistroErrores.registrar(e);
        } finally {
            System.out.println("Operación finalizada.\n");
        }

        // Caso 5: Superar el máximo de préstamos permitidos (máximo 3 por usuario)
        System.out.println("--- Caso 5: Superar el máximo de préstamos permitidos ---");
        try {
            biblioteca.prestarMaterial(1, "L002"); // Préstamo 2 de Usuario 1
            biblioteca.prestarMaterial(1, "L003"); // Préstamo 3 de Usuario 1
            System.out.println("Usuario 1 ahora tiene 3 préstamos activos. Intentando un 4to préstamo...");
            biblioteca.prestarMaterial(1, "R001"); // Debe lanzar MaximoPrestamosException
        } catch (BibliotecaException e) {
            System.out.println("Excepción capturada (" + e.getClass().getSimpleName() + "): " + e.getMessage());
            RegistroErrores.registrar(e);
        } finally {
            System.out.println("Operación finalizada.\n");
        }

        // Prueba de devolución exitosa
        System.out.println("--- Prueba adicional: Devolución exitosa ---");
        try {
            biblioteca.devolverMaterial(1, "L001");
            System.out.println("Devolución realizada con éxito: Usuario 1 devolvió L001");
        } catch (BibliotecaException e) {
            System.out.println("Error: " + e.getMessage());
            RegistroErrores.registrar(e);
        } finally {
            System.out.println("Operación finalizada.\n");
        }
    }

    private static void ejecutarMenuInteractivo(Biblioteca biblioteca) {
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;

        do {
            System.out.println("\nSeleccione una opción:");
            System.out.println("1. Registrar usuario");
            System.out.println("2. Registrar material");
            System.out.println("3. Prestar material");
            System.out.println("4. Devolver material");
            System.out.println("5. Salir");
            System.out.print("Opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer

                switch (opcion) {
                    case 1 -> {
                        System.out.print("Ingrese ID del usuario (entero): ");
                        int idUsuario = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Ingrese nombre del usuario: ");
                        String nombre = scanner.nextLine();
                        biblioteca.registrarUsuario(new Usuario(idUsuario, nombre));
                        System.out.println("Usuario registrado exitosamente.");
                    }
                    case 2 -> {
                        System.out.print("Tipo de material (1 = Libro, 2 = Revista): ");
                        int tipo = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Ingrese ID del material: ");
                        String idMat = scanner.nextLine();
                        System.out.print("Ingrese título: ");
                        String titulo = scanner.nextLine();

                        if (tipo == 1) {
                            System.out.print("Ingrese autor: ");
                            String autor = scanner.nextLine();
                            System.out.print("Ingrese ISBN: ");
                            String isbn = scanner.nextLine();
                            biblioteca.registrarMaterial(new Libro(idMat, titulo, autor, isbn));
                            System.out.println("Libro registrado exitosamente.");
                        } else if (tipo == 2) {
                            System.out.print("Ingrese número de revista: ");
                            int numero = scanner.nextInt();
                            scanner.nextLine();
                            biblioteca.registrarMaterial(new Revista(idMat, titulo, numero));
                            System.out.println("Revista registrada exitosamente.");
                        } else {
                            System.out.println("Tipo de material no válido.");
                        }
                    }
                    case 3 -> {
                        System.out.print("Ingrese ID del usuario: ");
                        int idUsuario = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Ingrese ID del material a prestar: ");
                        String idMat = scanner.nextLine();
                        biblioteca.prestarMaterial(idUsuario, idMat);
                        System.out.println("Préstamo realizado con éxito.");
                    }
                    case 4 -> {
                        System.out.print("Ingrese ID del usuario: ");
                        int idUsuario = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Ingrese ID del material a devolver: ");
                        String idMat = scanner.nextLine();
                        biblioteca.devolverMaterial(idUsuario, idMat);
                        System.out.println("Devolución realizada con éxito.");
                    }
                    case 5 -> System.out.println("Saliendo del sistema...");
                    default -> System.out.println("Opción inválida. Intente de nuevo.");
                }
            } catch (BibliotecaException e) {
                System.out.println("Error de Biblioteca (" + e.getClass().getSimpleName() + "): " + e.getMessage());
                RegistroErrores.registrar(e);
            } catch (InputMismatchException e) {
                System.out.println("Error de entrada: debe ingresar un valor numérico válido.");
                scanner.nextLine(); // Limpiar entrada incorrecta
                RegistroErrores.registrar(e);
            } finally {
                if (opcion != 5) {
                    System.out.println("Operación finalizada.");
                }
            }
        } while (opcion != 5);

        scanner.close();
    }
}
