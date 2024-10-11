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
            handleFileUpload(in, out, clientSocket.getInputStream());
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
    private static void handleFileUpload(BufferedReader in, OutputStream out, InputStream inputStream) throws IOException {
        // Create a directory for uploaded files if it doesn't exist
        File uploadDir = new File("uploads");
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        // Extract boundary from the content-type header
        String boundary = "";
        String line;
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Content-Type: multipart/form-data")) {
                // Extract the boundary
                int boundaryIndex = line.indexOf("boundary=");
                if (boundaryIndex != -1) {
                    boundary = line.substring(boundaryIndex + 9);
                }
            }
        }

        if (boundary.isEmpty()) {
            System.out.println("No boundary found in the request.");
            return;
        }

        // Parse multipart form data
        String caption = null;
        String date = null;
        File uploadedFile = null;

        String fileName = null;

        DataInputStream dataInputStream = new DataInputStream(inputStream);
        while (!(line = in.readLine()).contains("--" + boundary + "--")) {
            // System.out.println("line: "+line);
            // Read until boundary
            if (line.contains("Content-Disposition: form-data; name=\"caption\"")) {
                in.readLine(); // skip Content-Type or empty line
                caption = in.readLine(); // extract caption
                System.out.println("caption: " + caption);
            } else if (line.contains("Content-Disposition: form-data; name=\"date\"")) {
                in.readLine(); // skip Content-Type or empty line
                date = in.readLine(); // extract date
                System.out.println("date: " +date);
            } else if (line.contains("Content-Disposition: form-data; name=\"fileName\"; filename=\"")) {
                // Extract file name from the line
                fileName = line.substring(line.indexOf("filename=\"") + 10, line.length() - 1);
                System.out.println("File name received: " + fileName);

                // Skip headers
                in.readLine(); // skip Content-Type header
                in.readLine(); // skip empty line

                // Read file content and save it
                String fileSaveName = caption + "_" + date + "_" + fileName;
                File file = new File(uploadDir, fileSaveName);
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    String checkBuffer = "";
                    while ((bytesRead = dataInputStream.read(buffer)) != -1) {
                        fileOut.write(buffer, 0, bytesRead);
                        checkBuffer = new String(buffer, 0, bytesRead);
                        // Stop writing at the boundary
                        if (checkBuffer.contains("--" + boundary)) {
                            uploadedFile = file;
                            break;
                        }
                    } 
                    if (checkBuffer.contains("--" + boundary)) {
                        uploadedFile = file;
                        break;
                    }
                    uploadedFile = file;
                }
            }
        }

        System.out.println("uploadedFile " + uploadedFile);

        // Send response back to the client
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<html><body><h2>File uploaded successfully!</h2><br>" +
                "File saved as: " + (uploadedFile != null ? uploadedFile.getName() : "No file uploaded") +
                "<br>Caption: " + caption + "<br>Date: " + date +
                "</body></html>";

        out.write(response.getBytes());
        out.flush();
    }
}
