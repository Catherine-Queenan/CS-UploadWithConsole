import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UploadServlet extends HttpServlet {

   /**
    * Using reflection to dynamically access the GET or POST method of the servlet
    * while
    * printing updates on progress to the output stream.
    */
   public void handleRequest(String methodName, HttpServletRequest request, HttpServletResponse response)
         throws ServletNotFoundException, MethodNotFoundException {
      // Initialize PrintWriter to send response
      try (PrintWriter out = response.getWriter()) {
         // Logging to signify loading of the method and instantiation of the servlet
         // instance dynamically
         out.println("Loading 'UploadServlet' class dynamically...");
         // Load the servlet class dynamically
         Class<?> servletClass = Class.forName("UploadServlet");
         // Dynamically instantiating an instance
         Object servletInstance;
         try {
            servletInstance = servletClass.getDeclaredConstructor().newInstance();
         } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
               | InvocationTargetException e) {
            // Throwing a custom unchecked exception for instantiation errors (based on the
            // suggested initial exceptions)
            throw new ServletInvocationException(
                  "An error occurred while instantiating the servlet class 'UploadServlet'.", e);
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
         // Throwing a custom unchecked exception for invocation errors (based on the
         // suggested initial exceptions)
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
         InputStream inputStream = request.getInputStream();
         BufferedReader in = request.getBufferedIn();

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
               System.out.println("date: " + date);
            } else if (line.contains("Content-Disposition: form-data; name=\"fileName\"; filename=\"")) {
               // Extract file name from the line
               fileName = line.substring(line.indexOf("filename=\"") + 10, line.length() - 1);
               System.out.println("File name received: " + fileName);

               // Skip headers
               line = in.readLine(); // skip Content-Type header
               System.out.println(line);
               line = in.readLine(); // skip empty line
               System.out.println(line);
               System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

               // Read file content and save it
               String fileSaveName = caption + "_" + date + "_" + fileName;
               File file = new File(uploadDir, fileSaveName);
               try (OutputStream fileOut = new BufferedOutputStream(new FileOutputStream(file))) {
                  byte[] buffer = new byte[1024];
                  int bytesRead;
                  
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  StringBuilder checkBuffer = new StringBuilder();

                  String currentContent = "";
                  while ((bytesRead = dataInputStream.read(buffer)) != -1) {
                     // fileOut.write(buffer, 0, bytesRead);
                     
                     baos.write(buffer, 0, bytesRead);

                     byte[] allBytes = baos.toByteArray();
                     currentContent = new String(allBytes, 0, allBytes.length, StandardCharsets.ISO_8859_1);
                     if (currentContent.contains("--" + boundary + "--")) {
                        // Find the position of the boundary
                        int boundaryIndex = currentContent.indexOf("\r\n--" + boundary + "--");
                        // Write only up to the boundary
                        fileOut.write(allBytes, 0, boundaryIndex);
                        break;
                     } 
                  }
                  if (currentContent.contains("--" + boundary + "--")) {
                     uploadedFile = file;
                     break;
                  }
               }
            }
         }

         System.out.println("uploadedFile " + uploadedFile);

         // Send response back to the client
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
      File dir = new File("./uploads");
      String[] files = dir.list();

      if (files != null) {
         Arrays.sort(files);
         for (String file : files) {
            filesList.append("<li>");
            if (file.endsWith(".jpg") || file.endsWith(".png") || file.endsWith(".gif")) {
               filesList.append("<img src='/uploads/").append(file).append("' width='100' height='100'/>");
            }
            filesList.append(file).append("</li>");
         }
      }
      return filesList.toString();
   }
}