//IvaNode
import java.io.*;
import java.net.*;

public class IvaNode {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12346);

            while (true) {
                try (Socket socket = serverSocket.accept();
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                     ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                    Producto producto = (Producto) in.readObject();

                    if (!producto.getCategoria().equalsIgnoreCase("Canasta")) {
                        double nuevoPrecio = producto.getPrecio() * 1.19;
                        producto.setPrecio(nuevoPrecio);
                    }

                    out.writeObject(producto);
                    out.flush();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
