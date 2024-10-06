import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.time.LocalDate;
import java.nio.file.Files;


public class UploadClient {
    public UploadClient() { }

    public String uploadFile() {
        String listing = "";
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a caption for the image: ");
        String caption = scanner.nextLine();
        System.out.println("Enter the file path: ");
        String filePath = scanner.nextLine();

        //Get current Date
        String date = LocalDate.now().toString();

        try {
            Socket socket = new Socket("localhost", 8081);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStream out = socket.getOutputStream();

            // // Hard-coded values for caption and date
            // String caption = "Sample Caption"; 
            // String date = "2024-10-05"; 

            // HTTP Post Boundary
            String boundary = "----Boundary";

            // HTTP Post Request Headers
            String request = "POST /upload/upload HTTP/1.1\r\n" +
                             "Host: localhost:8081\r\n" +
                             "Content-Type: multipart/form-data; boundary=" + boundary + "\r\n" +
                             "Content-Length: ";

            // HTTP Post Body
            StringBuilder body = new StringBuilder();
body.append("--").append(boundary).append("\r\n")
    .append("Content-Disposition: form-data; name=\"caption\"\r\n\r\n")
    .append(caption).append("\r\n")
    .append("--").append(boundary).append("\r\n")
    .append("Content-Disposition: form-data; name=\"date\"\r\n\r\n")
    .append(date).append("\r\n")
    .append("--").append(boundary).append("\r\n")
    .append("Content-Disposition: form-data; name=\"File\"; filename=\"")
    // Extract filename from filePath
    .append(new File(filePath).getName()).append("\"\r\n")
    .append("Content-Type: ").append(Files.probeContentType(new File(filePath).toPath())).append("\r\n\r\n");
;

            // Read the file into a byte array
            FileInputStream fis = new FileInputStream(filePath);
            byte[] fileBytes = fis.readAllBytes();
            fis.close();

            // Final boundary
            String endBoundary = "\r\n--" + boundary + "--\r\n";

            // Calculate the content length (header + body + file + final boundary)
            int contentLength = body.length() + fileBytes.length + endBoundary.length();
            request += contentLength + "\r\n\r\n";

            // Send HTTP Post Request
            // Send Header
            out.write(request.getBytes());
            // Send Body
            out.write(body.toString().getBytes());
            // Send File
            out.write(fileBytes);
            // Send Final Boundary
            out.write(endBoundary.getBytes());

            out.flush();
            socket.shutdownOutput();

            System.out.println("Upload completed!");

            // Handle the response:
            String filename = "";
            while ((filename = in.readLine()) != null) {
                listing += filename + "\n";
            }
            socket.shutdownInput();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        return listing;
    }

}
