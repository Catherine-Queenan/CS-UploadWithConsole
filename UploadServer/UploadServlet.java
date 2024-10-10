import java.io.*;

public class UploadServlet extends HttpServlet {

   // Handle GET request to serve the HTML form
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) {
      try {
         // Set content type to HTML
         response.setContentType("text/html");

         // Get the PrintWriter to send the HTML response
         PrintWriter out = response.getWriter();

         // HTML form with file upload
         out.println("<html><body>");
         out.println("<h1>File Upload Form</h1>");
         out.println("<form method='POST' enctype='multipart/form-data' action=\"http://localhost:8082\">");
         out.println("Caption: <input type=\"text\" name=\"caption\"/><br><br>");
         out.println("Date: <input type=\"date\" name=\"date\" /><br><br>");
         out.println("<input type=\"file\" name=\"fileName\"/><br><br>");
         out.println("<input type=\"submit\" value=\"Submit\" />");
         out.println("</form>");
         out.println("</body></html>");

         // Ensure the response is sent
         out.flush();

      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   // Handle POST request to process the file upload
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) {
      try {
         InputStream in = request.getInputStream();
         int contentLength = request.getContentLength();

         BufferedReader reader = new BufferedReader(new InputStreamReader(in));

         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         byte[] content = new byte[4]; // Buffer size


         int bytesRead;
         int totalBytesRead = 0;      
   
         while (totalBytesRead < contentLength && (bytesRead = in.read(content)) != -1) {
            String line = new String(content);  
            System.out.println(line); 

            baos.write(content, 0, bytesRead);
         
            totalBytesRead += bytesRead;
         }

         // Save uploaded file with a timestamped name
         long timestamp = System.currentTimeMillis();
         System.out.println(timestamp);
         FileOutputStream fos = new FileOutputStream(new File(timestamp + ".txt"));
         baos.writeTo(fos);
         fos.close();

         // Send response showing the list of files in the directory
         PrintWriter out = response.getWriter();
         File dir = new File(".");
         String[] files = dir.list();
         out.println("<html><body><h2>Uploaded Files:</h2><ul>");
         for (String file : files) {
            out.println("<li>" + file + "</li>");
         }
         out.println("</ul></body></html>");
         out.flush();

      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }
}
