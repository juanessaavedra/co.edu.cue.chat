import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/** La libreria java.net implementa las clases ServerSocket y Socket para manejar las conexiones de red **/


public class Server { // Aquí se definen los sockets y flujos de entrada y salida necesarios para la comunicación.

    private Socket socket; // esta línea de código establece la base para que la clase Server tenga acceso a un objeto Socket que se utilizará para la comunicación con otro punto de conexión, ya sea un cliente en el caso de un servidor, o un servidor en el caso de un cliente.
    private ServerSocket serverSocket; // es una clase que representa un socket del lado del servidor que escucha las conexiones entrantes en un puerto específico. Permite a una aplicación servidor aceptar conexiones entrantes de clientes remotos.
    private DataInputStream bufferDeEntrada = null; // se utilizará para leer datos que se envían desde un cliente hacia el servidor. Normalmente, en una aplicación de servidor, se crearía y asociaría un DataInputStream con el flujo de entrada del socket del cliente para leer los datos que llegan desde el cliente.
    private DataOutputStream bufferDeSalida = null; //se utilizará para escribir datos que el servidor enviará al cliente. Normalmente, en una aplicación de servidor, se crearía y asociaría un DataOutputStream con el flujo de salida del socket del cliente para escribir los datos que el servidor desea enviar al cliente.
    Scanner escaner = new Scanner(System.in); //Instancia de scanner
    final String COMANDO_TERMINACION = "salir()"; //Final define que es una constante, no va cambiar. Comando que el servidor espera recibir del cliente para indicar que la comunicacion debe terminar

    public void levantarConexion(int puerto) { //Se encarga de iniciar el servidor, tiene el parametro puerto en el que el servidor esta esperando conexiones entrantes
        try {
            serverSocket = new ServerSocket(puerto); //Se crea objeto especificando el puerto
            mostrarTexto("Esperando conexión entrante en el puerto " + String.valueOf(puerto) + "..."); //Convierte el numero del puerto en una cadena de caracteres para poder concaternarlo con el mensaje
            socket = serverSocket.accept(); //Este metodo .accept es del serverSocket, el cual espera y acepta la conexion entrante de un cliente. Una vez que se establece la conexion, devuelve un objeto Socket que representa la conexion establecida con el cliente
            mostrarTexto("Conexión establecida con: " + socket.getInetAddress().getHostName() + "\n\n\n"); //el getInetAddress que representa la direccion IP del cliente, el .getHostName devuelve el nombre del host asociado con la direccion IP, En este caso, devuelve el nombre del host del cliente que se ha conectado al servidor.
        } catch (Exception e) {
            mostrarTexto("Error en levantarConexion(): " + e.getMessage()); //e.getMessage proporciona informacion sobre la causa del error
            System.exit(0); //Hace que el programa se cierre inmediatamente y devuelve un codigo de salida 0 (que indica que el programa termino correctamente)
        }
    }
    public void flujos() { //se encarga de abrir los flujos de entrada y salida asociados con el socket del cliente que se ha conectado al servidor. Los flujos de entrada y salida permiten al servidor leer datos enviados por el cliente y enviar datos al cliente, respectivamente.
        try {
            bufferDeEntrada = new DataInputStream(socket.getInputStream()); //Este es el constructor de la clase DataInputStream. Toma un objeto InputStream como argumento y lo envuelve en un DataInputStream, que proporciona métodos adicionales para LEER tipos de datos primitivos y cadenas de caracteres desde el flujo de entrada.
            // el socket.getInputStream. Este método devuelve un InputStream que puede ser utilizado para LEER datos del socket del cliente. Es el flujo de entrada asociado al socket, a través del cual el servidor puede recibir datos enviados por el cliente
            bufferDeSalida = new DataOutputStream(socket.getOutputStream()); //  Este método devuelve un OutputStream que puede ser utilizado para ESCRIBIR datos en el socket del cliente. Es el flujo de salida asociado al socket, a través del cual el servidor puede enviar datos al cliente.
            //DataOutputStream: que proporciona métodos adicionales para ESCRIBIR tipos de datos primitivos y cadenas de caracteres en el flujo de salida.
            bufferDeSalida.flush();
            //después de que se han escrito los datos en el DataOutputStream, llamar a flush() asegura que los datos sean enviados al cliente de manera inmediata y no se queden en el búfer interno.
        } catch (IOException e) {
            mostrarTexto("Error en la apertura de flujos");
        }
    }

    public void recibirDatos() {
        String st = ""; // Se inicializa una cadena vacía st para almacenar los datos recibidos del cliente.
        try {
            do {
                st = bufferDeEntrada.readUTF(); // se lee una cadena UTF-8 del flujo de entrada utilizando el método readUTF()
                mostrarTexto("\n[Cliente] => " + st); //Se muestra en la consola del servidor el mensaje recibido del cliente,
                System.out.print("\n[Usted] => "); //ndicando que el servidor está listo para recibir una respuesta del usuario.
            } while (!st.equals(COMANDO_TERMINACION)); //El bucle continuará ejecutándose hasta que el mensaje recibido del cliente sea igual al comando de terminación
        } catch (IOException e) {
            cerrarConexion(); //Se llama el metodo cerrarConexion con el cliente.
        }
    }


    public void enviar(String s) {
        try {
            bufferDeSalida.writeUTF(s); //Esta línea de código escribe la cadena s en el flujo de salida bufferDeSalida. La cadena se codifica utilizando el formato UTF-8 antes de ser enviada al cliente. El método writeUTF() se utiliza específicamente para escribir cadenas en el formato UTF-8, lo que garantiza que el cliente pueda leer la cadena correctamente, independientemente de su idioma o caracteres especiales.
            bufferDeSalida.flush(); //se llama al método flush() para asegurarse de que todos los datos almacenados en el búfer de salida sean enviados al flujo subyacente de manera inmediata.
        } catch (IOException e) {
            mostrarTexto("Error en enviar(): " + e.getMessage());
        }
    }

    public static void mostrarTexto(String s) { //Puede ser llamado desde cualquier parte del programa, sin necesidad de una instancia
        System.out.print(s);
    }

    public void escribirDatos() { //se encarga de recibir datos desde la entrada estándar del servidor (probablemente desde la consola) utilizando un Scanner y enviar estos datos al cliente a través del método enviar().
        while (true) {
            System.out.print("[Usted] => ");
            enviar(escaner.nextLine()); //para leer una línea de texto introducida por el usuario desde la entrada estándar del servidor. La llamada a nextLine() bloquea la ejecución del programa hasta que el usuario introduce una línea de texto y presiona la tecla Enter.
            //Este método enviar, se llama con el texto introducido por el usuario como argumento. Suponiendo que este método envía los datos al cliente, es responsable de enviar el texto al cliente a través de la conexión establecida.
        }
    }

    public void cerrarConexion() {
        try {
            bufferDeEntrada.close(); // Cierra el flujo de entrada (DataInputStream) asociado con el socket del cliente.
            bufferDeSalida.close();
            socket.close(); //Cierra el socket del cliente, lo que resulta en la terminación de la conexión entre el servidor y el cliente.
        } catch (IOException e) {
            mostrarTexto("Excepción en cerrarConexion(): " + e.getMessage());
        } finally { //Se ejecutara siempre
            mostrarTexto("Conversación finalizada....");
            System.exit(0);

        }
    }

    public void ejecutarConexion(int puerto) { //nicia un nuevo hilo (thread) para gestionar la conexión con un cliente. Dentro de este hilo, se llama a los métodos levantarConexion(int puerto), flujos(), recibirDatos(), y cerrarConexion() en un bucle infinito.
        Thread hilo = new Thread(() -> {
            while (true) {
                try {
                    levantarConexion(puerto); //Se llama el metodo
                    flujos(); //Se llama el metodo
                    recibirDatos(); //Se llama para recibir los datos del cliente
                } finally {
                    cerrarConexion(); //Despues de que el hilo haya terminado de recibir datos se llama el metodo cerrarconexion
                }
            }
        });
        hilo.start(); //Se inicia la ejecucion del hilo llamando al .start. Esto hace que el codigo dentro de la expresion lambda se ejecute en un hilo separado, permitiendo que el servidor maneje multiples conexiones de forma concurrente
    }

    public static void main(String[] args) throws IOException {
        Server s = new Server();
        Scanner sc = new Scanner(System.in);

        mostrarTexto("Ingresa el puerto [5050 por defecto]: ");
        String puerto = sc.nextLine(); //Este método del objeto Scanner espera a que el usuario introduzca una línea de texto y presione la tecla Enter. Una vez que el usuario proporciona la entrada, nextLine() lee esa línea de entrada como una cadena de caracteres.
        if (puerto.length() <= 0) puerto = "5050";
        s.ejecutarConexion(Integer.parseInt(puerto));
        s.escribirDatos();
    }
}
