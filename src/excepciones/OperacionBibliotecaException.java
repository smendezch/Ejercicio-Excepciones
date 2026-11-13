package excepciones;

public class OperacionBibliotecaException extends BibliotecaException {
    public OperacionBibliotecaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
