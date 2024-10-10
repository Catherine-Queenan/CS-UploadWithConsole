import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class UploadServer {
    public static void main(String[] args) throws IOException {
        // Set up the server to listen on port 8082
        ServerSocket serverSocket = new ServerSocket(8082);
        System.out.println("Server is running on http://localhost:8082 ...");

        while (true) {
            try (Socket clientSocket = serverSocket.accept()) {
                handleClient(clientSocket);
            }
        }
    }

    // Function to handle the client request
    private static void handleClient(Socket clientSocket) throws IOException {
        // Set up input and output streams
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        OutputStream out = clientSocket.getOutputStream();

        // Read the HTTP request line
        String requestLine = in.readLine();
        System.out.println("Request: " + requestLine);

        if (requestLine.startsWith("GET")) {
            // Handle GET request by serving the form
            serveForm(out);
        } else if (requestLine.startsWith("POST")) {
            // Handle POST request for file upload
            handleFileUpload(in, out);
        }
    }

    // Function to serve the HTML form
    private static void serveForm(OutputStream out) throws IOException {
        String form = """
                <html>
                <body>
                <h1>File Upload Form</h1>
                <form method='POST' enctype='multipart/form-data' action='/'>
                Caption: <input type="text" name="caption"/><br><br>
                Date: <input type="date" name="date"/><br><br>
                <input type="file" name="fileName"/><br><br>
                <input type="submit" value="Submit"/>
                </form>
                </body>
                </html>
                """;

        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + form.length() + "\r\n" +
                "\r\n" + form;

        out.write(httpResponse.getBytes());
        out.flush();
    }

    // Function to handle file upload and save the file
    private static void handleFileUpload(BufferedReader in, OutputStream out) throws IOException {
        // Create a directory for uploaded files if it doesn't exist
        File uploadDir = new File("uploads");
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        // Read and process the HTTP headers
        String line;
        while (!(line = in.readLine()).isEmpty()) {
            System.out.println("Header: " + line);
        }

        // Skip the form data boundary lines and headers to reach the file content
        in.readLine(); // Skip first boundary line
        in.readLine(); // Skip Content-Disposition header
        in.readLine(); // Skip Content-Type header
        in.readLine(); // Skip empty line

        // Read the file content (this is simplified for smaller files)
        StringBuilder fileContent = new StringBuilder();
        char[] buffer = new char[1024];
        int numRead;
        while ((numRead = in.read(buffer)) != -1) {
            fileContent.append(buffer, 0, numRead);
            if (fileContent.toString().contains("--")) {
                break; // Stop reading after the final boundary line
            }
        }

        // Save the uploaded file
        String caption = "sample";  // Extract from the form if needed
        String date = "2024-10-10"; // Extract from the form if needed
        String filename = caption + "_" + date + "_uploadedFile.txt"; // Customize file name
        File uploadedFile = new File(uploadDir, filename);

        Files.write(uploadedFile.toPath(), fileContent.toString().getBytes());

        // Send response back to the client
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<html><body><h2>File uploaded successfully!</h2></body></html>";
        out.write(response.getBytes());
        out.flush();
    }
}
