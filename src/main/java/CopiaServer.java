// copia server
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CopiaServer {
    public static void main(String[] args) {
        int serverPort = 12356;

        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            System.out.println("Esperando conexiones en el puerto " + serverPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Conexión aceptada desde " + clientSocket.getInetAddress());

                LocalDateTime connectionTime = LocalDateTime.now();
                String formattedTime = connectionTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                handleClient(clientSocket, formattedTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void handleClient(Socket clientSocket, String formattedTime) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            Object receivedObject = inputStream.readObject();
            if (receivedObject instanceof List<?> objectList) {
                List<Producto> productos = new ArrayList<>();

                for (Object obj : objectList) {
                    if (obj instanceof Producto) {
                        productos.add((Producto) obj);
                    }
                }

                List<Producto> productosConIVA = new ArrayList<>();

                for (Producto producto : productos) {
                    try (Socket ivaSocket = new Socket("localhost", 12346);
                         ObjectOutputStream ivaOutputStream = new ObjectOutputStream(ivaSocket.getOutputStream());
                         ObjectInputStream ivaInputStream = new ObjectInputStream(ivaSocket.getInputStream())) {
                        ivaOutputStream.writeObject(producto);
                        ivaOutputStream.flush();
                        Producto productoConIVA = (Producto) ivaInputStream.readObject();
                        productosConIVA.add(productoConIVA);

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }


                double sumaPrecios = 0;
                for (Producto producto : productosConIVA) {
                    sumaPrecios += producto.getPrecio();
                }

                Socket sumaSocket = new Socket("localhost", 12347);
                ObjectOutputStream sumaOutputStream = new ObjectOutputStream(sumaSocket.getOutputStream());
                ObjectInputStream sumaInputStream = new ObjectInputStream(sumaSocket.getInputStream());

                sumaOutputStream.writeDouble(sumaPrecios);
                sumaOutputStream.flush();

                double sumaCalculada = sumaInputStream.readDouble();

                sumaOutputStream.close();
                sumaInputStream.close();
                sumaSocket.close();

                outputStream.writeDouble(sumaCalculada);
                outputStream.flush();

                clientSocket.close();
            } else {
                // Manejo de error si el objeto recibido no es una lista
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
