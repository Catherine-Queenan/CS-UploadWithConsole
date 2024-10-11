import java.io.*;
import java.net.*;

public class UploadServerThread extends Thread {
   private Socket socket = null;

   public UploadServerThread(Socket socket) {
      super("UploadServerThread");
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
    
         HttpServletRequest req = new HttpServletRequest(reader, in); 
         // Set up the response output stream
         OutputStream baos = new ByteArrayOutputStream();
         HttpServletResponse res = new HttpServletResponse(baos);
         
         // Initialize the UploadServlet
         HttpServlet servlet = new UploadServlet();

         // Call the appropriate method based on the request type
         if (requestLine.startsWith("GET")) {
            // Simulate HttpServletRequest for GET
            servlet.doGet(req, res);
         } else if (requestLine.startsWith("POST")) {
            // Simulate HttpServletRequest for POST
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
