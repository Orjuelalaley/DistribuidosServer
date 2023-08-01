package org.example;

import java.io.*;
import java.net.*;

public class ServerOperation1 {
    public static void main(String[] args) {
        final int PORT = 8001;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor de Operación 1 en espera de conexiones...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String data = in.readLine();
                    int num = Integer.parseInt(data);
                    System.out.printf(num + "\n");
                    int result = num + 5;
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

