package Client;

import java.util.Scanner;

public class MainClient {
    public static void main(String[] args) {
        Client client = new Client();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingresa la IP: [localhost por defecto] ");
        String ip = scanner.nextLine();
        if (ip.length() <= 0) ip = "localhost";

        System.out.println("Puerto: [5050 por defecto] ");
        String puerto = scanner.nextLine();
        puerto = (puerto.length() <= 0) ? "5050" : puerto;
        client.executeConnection(ip, Integer.parseInt(puerto));
        client.enterData();
    }
}
