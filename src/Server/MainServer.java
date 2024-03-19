package Server;

import java.util.Scanner;

public class MainServer {
    public static void main(String[] args) {
        Server server = new Server();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingresa el puerto [5050 por defecto]: ");
        String port = scanner.nextLine(); //Este método del objeto Scanner espera a que el usuario introduzca una línea de texto y presione la tecla Enter. Una vez que el usuario proporciona la entrada, nextLine() lee esa línea de entrada como una cadena de caracteres.
        port = (port.length() <= 0) ? "5050" : port;
        server.executeConnection(Integer.parseInt(port));
        server.enterData();
    }
}
