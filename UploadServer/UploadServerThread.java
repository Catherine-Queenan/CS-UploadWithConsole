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

         

         // Split the request line by spaces to get the HTTP method (GET or POST)
         String[] requestParts = requestLine.split(" ");
         String method = requestParts[0]; // This will be "GET" or "POST"
         for(int i = 0; i < requestParts.length; i++){
            System.out.println(requestParts[i]);
         }
         
         String line;
         int contentLength = -1;
         while (!(line = reader.readLine()).isEmpty()) {
            System.out.println(line);
            if (line.startsWith("Content-Length: ")) {
                contentLength = Integer.parseInt(line.substring(16).trim());
            }
         }
        
        HttpServletRequest req = new HttpServletRequest(in, contentLength); 
         // Set up the response output stream
         OutputStream baos = new ByteArrayOutputStream();
         HttpServletResponse res = new HttpServletResponse(baos);
         
         // Initialize the UploadServlet
         HttpServlet servlet = new UploadServlet();

         // Call the appropriate method based on the request type
         if ("GET".equalsIgnoreCase(method)) {
            // Simulate HttpServletRequest for GET
            servlet.doGet(req, res);
         } else if ("POST".equalsIgnoreCase(method)) {
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
