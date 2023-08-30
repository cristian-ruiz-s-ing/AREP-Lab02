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
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream();

            String request = in.readLine();
            String filePath = null;//getRequestFilePath(request);

            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    byte[] fileData = null;//readFileData(file);
                    out.write(fileData);
                }
            }

            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //private String getRequestFilePath(String request) {
        // Parse the HTTP request and extract the requested file path
        // Return the appropriate local file path based on the request
    //}

    //private byte[] readFileData(File file) throws IOException {
        // Read the content of the file into a byte array
        // Return the byte array
    //}
}
