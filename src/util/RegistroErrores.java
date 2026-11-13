package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistroErrores {
    private static final String ARCHIVO_LOG = "errores.log";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void registrar(Exception e) {
        String fecha = LocalDateTime.now().format(FORMATO_FECHA);
        String tipoExcepcion = e.getClass().getSimpleName();
        String mensaje = e.getMessage();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_LOG, true))) {
            writer.write("[" + fecha + "] " + tipoExcepcion);
            writer.newLine();
            writer.write(mensaje != null ? mensaje : "Sin mensaje de detalle");
            writer.newLine();
            if (e.getCause() != null) {
                writer.write("Causa: " + e.getCause().getClass().getSimpleName() + " - " + e.getCause().getMessage());
                writer.newLine();
            }
            writer.newLine();
        } catch (IOException ioEx) {
            System.err.println("Error crítico al escribir en el archivo de log: " + ioEx.getMessage());
        }
    }
}
