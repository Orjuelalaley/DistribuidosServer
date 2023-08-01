package org.example;
import java.io.*;
import java.net.*;

public class ServerOperation2 {
    public static void main(String[] args) {
        final int PORT = 8002;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor de Operación 2 en espera de conexiones...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String data = in.readLine();
                    System.out.printf(data + "\n");
                    int num = Integer.parseInt(data);
                    int result = num * 2;
                    out.println(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
