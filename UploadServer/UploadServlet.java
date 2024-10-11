import java.io.*;
import java.lang.reflect.*;
import java.util.Arrays;

public class UploadServlet extends HttpServlet {

   /**
    * Using reflection to dynamically access the GET or POST method of the servlet while
    * printing updates on progress to the output stream.
    */
   public void handleRequest(String methodName, HttpServletRequest request, HttpServletResponse response) throws ServletNotFoundException, MethodNotFoundException {
      // Initialize PrintWriter to send response
      try (PrintWriter out = response.getWriter()) {
         // Logging to signify loading of the method and instantiation of the servlet instance dynamically
         out.println("Loading 'UploadServlet' class dynamically...");
         // Load the servlet class dynamically
         Class<?> servletClass = Class.forName("UploadServlet");
         // Dynamically instantiating an instance
         Object servletInstance;
         try {
            servletInstance = servletClass.getDeclaredConstructor().newInstance();
         } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            // Throwing a custom unchecked exception for instantiation errors (based on the suggested initial exceptions)
            throw new ServletInvocationException("An error occurred while instantiating the servlet class 'UploadServlet'.", e);
         }

         // Using reflection to invoke the GET or POST method
         Method method;
         if (methodName.equalsIgnoreCase("get")) {
            method = servletClass.getDeclaredMethod("doGet", HttpServletRequest.class, HttpServletResponse.class);
         } else if (methodName.equalsIgnoreCase("post")) {
            method = servletClass.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
         } else {
            // Throwing a custom exception for method not found
            throw new MethodNotFoundException("The requested method '" + methodName + "' could not be found.", null);
         }
         method.setAccessible(true);
         // Logging the invocation of the method
         out.println("Invoking method '" + methodName + "' dynamically...");
         // Invoking the method
         method.invoke(servletInstance, request, response);

         // Sending the response
         out.flush();
      // Using the custom exceptions to handle errors
      } catch (ClassNotFoundException e) {
         // Throwing a custom exceptoion for class not found
         throw new ServletNotFoundException("The requested servlet class 'UploadServlet' could not be found.", e);
      } catch (NoSuchMethodException e) {
         // Throwing a custom exception for method not found
         throw new MethodNotFoundException("The requested method '" + methodName + "' could not be found.", e);
      } catch (IllegalAccessException | InvocationTargetException e) {
         // Throwing a custom unchecked exception for invocation errors (based on the suggested initial exceptions)
         throw new ServletInvocationException("An error occurred while invoking the method '" + methodName + "'.", e);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

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
         response.setContentType("text/html");
         PrintWriter out = response.getWriter();
         String header = "<!Doctype html><html><head><title>Uploaded Files</title></head><body><ul>";
         String footer = "</ul></body></html>";
         out.println(header + getFilesList() + footer);
         out.flush();

      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   // Helper method to get the list of files in the directory
   private String getFilesList() {
      StringBuilder filesList = new StringBuilder();
      File dir = new File(".");
      String[] files = dir.list();
      
      if (files != null) {
         Arrays.sort(files);
         for (String file : files) {
            filesList.append("<li>");
            if (file.endsWith(".jpg") || file.endsWith(".png") || file.endsWith(".gif")) {
               filesList.append("<img src='").append(file).append("' width='100' height='100'/>");
            }
            filesList.append(file).append("</li>");
         }
      }
      return filesList.toString();
   }
}
