package org.example;

import java.io.*;
import java.net.*;

public class MainServer {
    public static void main(String[] args) {
        //creamos las variables para la conexión con los servidores
        final String HOST = "localhost";
        final int PORT_1 = 8001;
        final int PORT_2 = 8002;

        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
             Socket clientSocket1 = new Socket(HOST, PORT_1); // Conexión al Servidor de Operación 1
             PrintWriter out1 = new PrintWriter(clientSocket1.getOutputStream(), true); // Flujo de salida para enviar datos al Servidor de Operación 1
             BufferedReader in1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream())); // Flujo de entrada para recibir datos del Servidor de Operación 1
             Socket clientSocket2 = new Socket(HOST, PORT_2); // Conexión al Servidor de Operación 2
             PrintWriter out2 = new PrintWriter(clientSocket2.getOutputStream(), true); // Flujo de salida para enviar datos al Servidor de Operación 2
             BufferedReader in2 = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()))) { // Flujo de entrada para recibir datos del Servidor de Operación 2

            // Solicitar al usuario que ingrese el primer número
            System.out.print("Ingrese el primer número: ");
            int num1 = Integer.parseInt(consoleReader.readLine());

            // Enviar el primer número al Servidor de Operación 1
            out1.println(num1);

            // Solicitar al usuario que ingrese el segundo número
            System.out.print("Ingrese el segundo número: ");
            int num2 = Integer.parseInt(consoleReader.readLine());

            // Enviar el segundo número al Servidor de Operación 2
            out2.println(num2);

            // Recibir el resultado del Servidor de Operación 1
            int result1 = Integer.parseInt(in1.readLine());

            // Recibir el resultado del Servidor de Operación 2
            int result2 = Integer.parseInt(in2.readLine());

            // Calcular el resultado final sumando los resultados de ambos servidores de operación
            int finalResult = result1 + result2;

            // Mostrar el resultado final del cálculo
            System.out.println("Resultado del cálculo: " + finalResult);

        } catch (IOException e) {
            // En caso de error en la comunicación con los servidores de operación, imprimir el rastro de la excepción
            e.printStackTrace();
        }
    }
}
