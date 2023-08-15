//cliente
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 12345;
        int backupServerPort = 12356;
        Scanner scanner = new Scanner(System.in);

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            List<Producto> productos = new ArrayList<>();

            System.out.print("Ingrese la cantidad de productos: ");
            int cantidadProductos = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            for (int i = 0; i < cantidadProductos; i++) {
                System.out.print("Ingrese el nombre del producto " + (i + 1) + ": ");
                String nombre = scanner.nextLine();

                System.out.print("Ingrese la categoría del producto " + (i + 1) + ": ");
                String categoria = scanner.nextLine();

                System.out.print("Ingrese el precio del producto " + (i + 1) + ": ");
                double precio = scanner.nextDouble();
                scanner.nextLine(); // Limpiar el buffer

                Producto producto = new Producto(nombre, categoria, precio);
                productos.add(producto);
            }

            outputStream.writeObject(productos);
            outputStream.flush(); // Importante: asegúrate de vaciar el búfer

            double sumaCalculada = inputStream.readDouble();
            System.out.println("Total a pagar: " + sumaCalculada);

            socket.close();
        } catch (IOException e) {
            System.out.println("No se pudo conectar al servidor principal. Intentando con el servidor de respaldo...");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            try {
                Socket backupSocket = new Socket(serverAddress, backupServerPort);
                ObjectOutputStream backupOutputStream = new ObjectOutputStream(backupSocket.getOutputStream());
                ObjectInputStream backupInputStream = new ObjectInputStream(backupSocket.getInputStream());

                List<Producto> productos = new ArrayList<>();

                System.out.print("Ingrese la cantidad de productos: ");
                int cantidadProductos = scanner.nextInt();
                scanner.nextLine();

                for (int i = 0; i < cantidadProductos; i++) {
                    System.out.print("Ingrese el nombre del producto " + (i + 1) + ": ");
                    String nombre = scanner.nextLine();

                    System.out.print("Ingrese la categoría del producto " + (i + 1) + ": ");
                    String categoria = scanner.nextLine();

                    System.out.print("Ingrese el precio del producto " + (i + 1) + ": ");
                    double precio = scanner.nextDouble();
                    scanner.nextLine();

                    Producto producto = new Producto(nombre, categoria, precio);
                    productos.add(producto);
                }

                backupOutputStream.writeObject(productos);
                backupOutputStream.flush();

                double sumaCalculada = backupInputStream.readDouble();
                System.out.println("Total a pagar (respaldo): " + sumaCalculada);

                backupSocket.close();
            } catch (IOException a) {
                System.out.println("No se pudo conectar al servidor de respaldo. Verifique la conexión.");
            }
        }
    }
}
