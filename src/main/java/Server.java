import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Server {
    public static void main(String[] args) {
        int serverPort = 12345;

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Esperando conexiones en el puerto " + serverPort);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    LocalDateTime connectionTime = LocalDateTime.now();
                    String formattedTime = connectionTime.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
                    System.out.println("Conexión aceptada desde " + clientSocket.getInetAddress() + " a las " + formattedTime);
                    handleClient(clientSocket, formattedTime);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket, String formattedTime) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            List<Producto> productos = (List<Producto>) inputStream.readObject();
            List<Producto> productosConIVA = new ArrayList<>();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<Producto>> future = executor.submit(() -> {
                List<Producto> productosConIvaTemp = new ArrayList<>();
                for (Producto producto : productos) {
                    try {
                        Socket ivaSocket = new Socket();
                        SocketAddress ivaAddress = new InetSocketAddress("localhost", 12346);
                        ivaSocket.connect(ivaAddress, 10000); // 10 seconds timeout

                        ObjectOutputStream ivaOutputStream = new ObjectOutputStream(ivaSocket.getOutputStream());
                        ObjectInputStream ivaInputStream = new ObjectInputStream(ivaSocket.getInputStream());

                        ivaOutputStream.writeObject(producto);
                        ivaOutputStream.flush();

                        Producto productoConIVA = (Producto) ivaInputStream.readObject();
                        productosConIvaTemp.add(productoConIVA);

                        ivaOutputStream.close();
                        ivaInputStream.close();
                        ivaSocket.close();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        // In case of exception, handle the operation locally
                        double nuevoPrecio = producto.getPrecio() * 1.19;
                        producto.setPrecio(nuevoPrecio);
                        productosConIvaTemp.add(producto);
                    }
                }
                return productosConIvaTemp;
            });

            try {
                productosConIVA = future.get(10, TimeUnit.SECONDS); // Wait up to 10 seconds
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
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
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
