package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/** La libreria java.net implementa las clases ServerSocket y Socket para manejar las conexiones de red
 *  Aquí se definen los sockets y flujos de entrada y salida necesarios para la comunicación.**/


public class Server {

    private Socket socket; // La clase Server tenga acceso a un objeto Socket que se utilizará para la comunicación con otro punto de conexión
    private ServerSocket serverSocket; // es una clase que representa un socket del lado del servidor que escucha las conexiones entrantes en un puerto específico. Permite a una aplicación servidor aceptar conexiones entrantes de clientes remotos.
    private DataInputStream inputBuffer = null; // se utilizará para leer datos que se envían desde un cliente hacia el servidor.
    private DataOutputStream outputBuffer = null; //se utilizará para escribir datos que el servidor enviará al cliente.
    Scanner scanner = new Scanner(System.in);
    final String commandFinish = "exit()"; //Comando que el servidor espera recibir del cliente para indicar que la comunicacion debe terminar

    public void liftConnection(int port) { //Se encarga de iniciar el servidor, tiene el parametro puerto en el que el servidor esta esperando conexiones entrantes
        try {
            serverSocket = new ServerSocket(port); //Se crea objeto especificando el puerto
            System.out.println("Esperando conexión entrante en el puerto " + String.valueOf(port) + "..."); //Convierte el numero del puerto en una cadena de caracteres para poder concaternarlo con el mensaje
            socket = serverSocket.accept(); //Este metodo .accept es del serverSocket, el cual espera y acepta la conexion entrante de un cliente.
            System.out.println("Conexión establecida con: " + socket.getInetAddress().getHostName() + "\n\n\n"); //el getInetAddress que representa la direccion IP del cliente, el .getHostName devuelve el nombre del host asociado con la direccion IP.
        } catch (Exception e) {
            System.out.println("Error en liftConnection(): " + e.getMessage()); //e.getMessage proporciona informacion sobre la causa del error
            System.exit(0); //Hace que el programa se cierre inmediatamente.
        }
    }
    public void flows() { //se encarga de abrir los flujos de entrada y salida asociados con el socket del cliente que se ha conectado al servidor.
        try {
            inputBuffer = new DataInputStream(socket.getInputStream()); //Este es el constructor de la clase DataInputStream.
            // el socket.getInputStream. Este método devuelve un InputStream que puede ser utilizado para LEER datos del socket del cliente.
            outputBuffer = new DataOutputStream(socket.getOutputStream()); //  Este método devuelve un OutputStream que puede ser utilizado para ESCRIBIR datos
            //DataOutputStream: que proporciona métodos adicionales para ESCRIBIR tipos de datos primitivos y cadenas de caracteres en el flujo de salida.
            outputBuffer.flush();
            //después de que se han escrito los datos en el DataOutputStream, llamar a flush() asegura que los datos sean enviados al cliente de manera inmediata y no se queden en el búfer interno.
        } catch (IOException e) {
            System.out.println("Error en la apertura de flujos");
        }
    }

    public void receiveData() {
        String st = ""; // Se inicializa una cadena vacía st para almacenar los datos recibidos del cliente.
        try {
            do {
                st = inputBuffer.readUTF(); // se lee una cadena UTF-8 del flujo de entrada utilizando el método readUTF()
                System.out.println(("\n[Cliente] => " + st));
                System.out.print("\n[Usted] => ");
            } while (!st.equals(commandFinish)); //El bucle continuará ejecutándose hasta que el mensaje recibido del cliente sea igual al comando de terminación
        } catch (IOException e) {
            closeConnection();
        }
    }


    public void send(String s) {
        try {
            outputBuffer.writeUTF(s);
            outputBuffer.flush();
        } catch (IOException e) {
            System.out.println("Error en send(): " + e.getMessage());
        }
    }


    public void enterData() { //se encarga de recibir datos desde la entrada estándar del servidor (probablemente desde la consola) utilizando un Scanner y enviar estos datos al cliente a través del método enviar().
        while (true) {
            System.out.print("[Usted] => ");
            send(scanner.nextLine());
        }
    }

    public void closeConnection() {
        try {
            inputBuffer.close(); // Cierra el flujo de entrada (DataInputStream) asociado con el socket del cliente.
            outputBuffer.close();
            socket.close(); //Cierra el socket del cliente, lo que resulta en la terminación de la conexión entre el servidor y el cliente.
        } catch (IOException e) {
            System.out.println("Excepción en closeConnection(): " + e.getMessage());
        } finally {
            System.out.println("Conversación finalizada....");
            System.exit(0);

        }
    }

    public void executeConnection(int port) { //Inicia un nuevo hilo (thread) para gestionar la conexión con un cliente. Dentro de este hilo, se llama a los métodos levantarConexion(int puerto), flujos(), recibirDatos(), y cerrarConexion() en un bucle infinito.
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    liftConnection(port);
                    flows();
                    receiveData();
                } finally {
                    closeConnection();
                }
            }
        });
        thread.start(); //Se inicia la ejecucion del hilo llamando al .start. Esto hace que el codigo dentro de la expresion lambda se ejecute en un hilo separado, permitiendo que el servidor maneje multiples conexiones de forma concurrente
    }

}
