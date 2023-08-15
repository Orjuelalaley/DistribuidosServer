//Suma
import java.io.*;
import java.net.*;

public class Suma {
    public static void main(String[] args) {
        int sumaPort = 12347;

        try {
            ServerSocket serverSocket = new ServerSocket(sumaPort);

            while (true) {
                Socket socket = serverSocket.accept();
                handleClient(socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            double sumaPrecios = in.readDouble();
            double sumaCalculada = calcularSuma(sumaPrecios);

            out.writeDouble(sumaCalculada);
            out.flush();

            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double calcularSuma(double sumaPrecios) {
        return sumaPrecios;
    }
}
