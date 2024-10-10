import java.io.*;
import java.lang.reflect.*;

public class UploadServlet {

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
   protected void doPost(HttpServletRequest request, HttpServletResponse response) {
      try {
         InputStream in = request.getInputStream();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         byte[] content = new byte[1024]; // Buffer size
         int bytesRead;
         while ((bytesRead = in.read(content)) != -1) {
            baos.write(content, 0, bytesRead);
         }

         // Save uploaded file with a timestamped name
         long timestamp = System.currentTimeMillis();
         FileOutputStream fos = new FileOutputStream(new File(timestamp + ".html"));
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
