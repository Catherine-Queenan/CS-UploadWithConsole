import java.net.*;
import java.io.*;

public class UploadServerThread extends Thread {
   private Socket socket = null;

   public UploadServerThread(Socket socket) {
      this.socket = socket;
   }

   public void run() {
      try {
         // Get the input stream to read the HTTP request
         InputStream in = socket.getInputStream();
         BufferedReader reader = new BufferedReader(new InputStreamReader(in));
         
         // Read the first line of the request (the request line)
         String requestLine = reader.readLine();
         if (requestLine == null || requestLine.isEmpty()) {
            socket.close();
            return;
         }

         // Split the request line by spaces to get the HTTP method (GET or POST)
         String[] requestParts = requestLine.split(" ");
         String method = requestParts[0]; // This will be "GET" or "POST"
         
         // Set up the response output stream
         OutputStream baos = new ByteArrayOutputStream();
         HttpServletResponse res = new HttpServletResponse(baos);
         
         // Initialize the UploadServlet
         UploadServlet servlet = new UploadServlet();

         // Call the appropriate method based on the request type
         if ("GET".equalsIgnoreCase(method)) {
            // Simulate HttpServletRequest for GET
            HttpServletRequest req = new HttpServletRequest(in);  
            servlet.doGet(req, res);
         } else if ("POST".equalsIgnoreCase(method)) {
            // Simulate HttpServletRequest for POST
            HttpServletRequest req = new HttpServletRequest(in);  
            servlet.doPost(req, res);
         }

         // Write the response back to the client
         OutputStream out = socket.getOutputStream();
         out.write(((ByteArrayOutputStream) baos).toByteArray());
         socket.close();

      } catch (Exception e) {
            e.printStackTrace();
      }
   }
}
