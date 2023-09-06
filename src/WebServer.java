import java.io.*;
import java.net.*;

public class WebServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("Server listening on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            Thread clientThread = new ClientHandler(clientSocket);
            clientThread.start();
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        OutputStream out = null;
        try {
            out = clientSocket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String request = null;
        try {
            request = in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String filePath = getRequestFilePath(request);

        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                // Verifica que la solicitud sea una solicitud GET y contiene "/api/archivos/"
                if (request.startsWith("GET /api/archivos/")) {
                    try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                        StringBuilder fileContent = new StringBuilder();
                        String line;
                        while ((line = fileReader.readLine()) != null) {
                            fileContent.append(line);
                        }
                        String response = "HTTP/1.1 200 OK\r\n\r\n" + fileContent.toString();
                        out.write(response.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Manejar otros tipos de solicitudes (por ejemplo, servir archivos estáticos)
                    byte[] fileData = new byte[0];
                    try {
                        fileData = readFileData(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        out.write(fileData);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private String getRequestFilePath(String request) {
        try {
            String[] parts = request.split(" ");
            String requestMethod = parts[0];
            String requestPath = parts[1];

            String baseDirectory = "C:/Documentos/";
            String filePath = baseDirectory + requestPath;

            if (filePath.startsWith(baseDirectory) && new File(filePath).exists()){
                return filePath;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }

    private byte[] readFileData(File file) throws IOException {
        byte[] data = null;
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            data = new byte[(int) file.length()];
            fileInputStream.read(data);

        }catch (IOException e){
            e.printStackTrace();
        }
        return data;
    }
}
