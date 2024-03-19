package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private DataInputStream inputBuffer = null; //Se utiliza para leer datos
    private DataOutputStream outputBuffer = null; //Para escribir datos
    Scanner scanner = new Scanner(System.in);
    final String commandFinish = "exit()";

    public void liftConnection(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            System.out.println("Conectado a :" + socket.getInetAddress().getHostName());
        } catch (Exception e) {
            System.out.println("Excepción al levantar conexión: " + e.getMessage());
            System.exit(0);
        }
    }



    public void openFlows() {
        try {
            inputBuffer = new DataInputStream(socket.getInputStream());
            outputBuffer = new DataOutputStream(socket.getOutputStream());
            outputBuffer.flush();
        } catch (IOException e) {
            System.out.println("Error en la apertura de flujos");
        }
    }

    public void send(String s) {
        try {
            outputBuffer.writeUTF(s);
            outputBuffer.flush();
        } catch (IOException e) {
            System.out.println("IOException on send");
        }
    }

    public void closeConnection() {
        try {
            inputBuffer.close();
            outputBuffer.close();
            socket.close();
            System.out.println("Conexión terminada");
        } catch (IOException e) {
            System.out.println("IOException on closeConnection()");
        }finally{
            System.exit(0);
        }
    }

    public void executeConnection(String ip, int port) {
        Thread thread = new Thread(() -> {
            try {
                liftConnection(ip, port);
                openFlows();
                receiveData();
            } finally {
                closeConnection();
            }
        });
        thread.start();
    }

    public void receiveData() {
        String st = "";
        try {
            do {
                st = inputBuffer.readUTF();
                System.out.println("\n[Servidor] => " + st);
                System.out.print("\n[Usted] => ");
            } while (!st.equals(commandFinish));
        } catch (IOException e) {}
    }

    public void enterData() {
        String enter;
        do {
            System.out.print("[Usted] => ");
            enter = scanner.nextLine();
            if (!enter.isEmpty()) {
                send(enter);
            }
        } while (true);
    }

}
