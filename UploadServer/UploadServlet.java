import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UploadServlet extends HttpServlet {

   /**
    * Using reflection to dynamically access the GET or POST method of the servlet
    * while
    * printing updates on progress to the output stream.
    */
    public void handleRequest(String methodName, HttpServletRequest request, HttpServletResponse response) 
        throws ServletNotFoundException, MethodNotFoundException {
        try (PrintWriter out = response.getWriter()) {
            out.println("Loading 'UploadServlet' class dynamically...");
            System.out.println("Loading 'UploadServlet' class dynamically...");

            // Load the servlet class dynamically
            Class<?> servletClass = Class.forName("UploadServlet");
            out.println("Class 'UploadServlet' loaded successfully: " + servletClass.getName());
            System.out.println("Class 'UploadServlet' loaded successfully: " + servletClass.getName());

            // Create an instance of the servlet class
            Object servletInstance = servletClass.getDeclaredConstructor().newInstance();
            out.println("Instance of 'UploadServlet' created successfully.");
            System.out.println("Instance of 'UploadServlet' created successfully.");

            // Reflectively determine the method to call (GET or POST)
            String methodToInvoke = methodName.equalsIgnoreCase("get") ? "doGet" : "doPost";
            Method method = servletClass.getDeclaredMethod(methodToInvoke, HttpServletRequest.class, HttpServletResponse.class);
            out.println("Method '" + methodToInvoke + "' determined successfully.");
            System.out.println("Method '" + methodToInvoke + "' determined successfully.");

            // Invoke the method dynamically
            out.println("Invoking method '" + methodToInvoke + "' dynamically with parameters:");
            out.println("Request: " + request);
            out.println("Response: " + response);
            System.out.println("Invoking method '" + methodToInvoke + "' dynamically with parameters:");
            System.out.println("Request: " + request);
            System.out.println("Response: " + response);

            method.invoke(servletInstance, request, response);
            out.println("Method '" + methodToInvoke + "' invoked successfully.");
            System.out.println("Method '" + methodToInvoke + "' invoked successfully.");
            out.flush();
        } catch (ClassNotFoundException e) {
            System.err.println("Error: The requested servlet class 'UploadServlet' could not be found.");
            throw new ServletNotFoundException("The requested servlet class 'UploadServlet' could not be found.", e);
        } catch (NoSuchMethodException e) {
            System.err.println("Error: The requested method '" + methodName + "' could not be found.");
            throw new MethodNotFoundException("The requested method '" + methodName + "' could not be found.", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println("Error: An error occurred while invoking the method '" + methodName + "'.");
            throw new ServletInvocationException("An error occurred while invoking the method '" + methodName + "'.", e);
        } catch (Exception e) {
            System.err.println("Error: An unexpected error occurred: " + e.getMessage());
            e.printStackTrace(); // For debugging purposes
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

         //handleRequest("get", request, response);

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

         //handleRequest("post", request, response);

         File uploadDir = new File("uploads");
         if (!uploadDir.exists()) {
            uploadDir.mkdir();
         }
         // Extract boundary from the content-type header
         String boundary = request.getBoundary();

         if (boundary == null) {
            System.out.println("No boundary found in the request.");
            return;
         }

         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         byte[] buffer = new byte[1024];
         int bytesRead;
         String input = "";

         while (!input.contains("--" + boundary + "--") && ((bytesRead = inputStream.read(buffer)) != -1)) {
            baos.write(buffer, 0, bytesRead);
            input = baos.toString(StandardCharsets.UTF_8);
         }
         byte[] inputData = baos.toByteArray();
         String regex = "--" + boundary;
         String[] formParts = input.split(regex);

         Map<String, String> formInputs = new HashMap<>();
         byte file[] = null;

         int offset = 0;
         for (String part : formParts) {
            if (part.contains("Content-Disposition: form-data; name=\"caption\"")) {
               String caption = part.split("\r\n\r\n")[1].trim();
               formInputs.put("caption", caption);

            } else if (part.contains("Content-Disposition: form-data; name=\"date\"")) {
               String date = part.split("\r\n\r\n")[1].trim();
               formInputs.put("date", date);

            } else if (part.contains("Content-Disposition: form-data; name=\"fileName\"; filename=\"")) {
               String filename = part.split("filename=\"")[1].split("\"")[0];
               formInputs.put("filename", filename);

               int fileDataStart = part.indexOf("\r\n\r\n") + 4;
               int fileDataEnd = input.indexOf("--" + boundary + "--");

               int headerBytes = input.substring(0, offset + fileDataStart).getBytes(StandardCharsets.UTF_8).length;
               file = Arrays.copyOfRange(inputData, headerBytes, fileDataEnd);
            }
            offset += part.length() + regex.getBytes().length;
         }

         String fileName = formInputs.get("caption") + "_" + formInputs.get("date") + "_" + formInputs.get("filename");
         System.out.println(fileName);

         File filePath = new File(uploadDir, fileName);
         try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            fileOut.write(file);

         } catch (IOException e){
            e.printStackTrace();
         }



         // Parse multipart form data
         // String caption = null;
         // String date = null;
         // File uploadedFile = null;

         // String fileName = null;

         // while (!(line = in.readLine()).contains("--" + boundary + "--")) {
         // // System.out.println("line: "+line);
         // // Read until boundary
         // if (line.contains("Content-Disposition: form-data; name=\"caption\"")) {
         // in.readLine(); // skip Content-Type or empty line
         // caption = in.readLine(); // extract caption
         // System.out.println("caption: " + caption);
         // } else if (line.contains("Content-Disposition: form-data; name=\"date\"")) {
         // in.readLine(); // skip Content-Type or empty line
         // date = in.readLine(); // extract date
         // System.out.println("date: " + date);
         // } else if (line.contains("Content-Disposition: form-data; name=\"fileName\";
         // filename=\"")) {
         // // Extract file name from the line
         // fileName = line.substring(line.indexOf("filename=\"") + 10, line.length() -
         // 1);
         // System.out.println("File name received: " + fileName);

         // // Skip headers
         // line = in.readLine(); // skip Content-Type header
         // System.out.println(line);
         // line = in.readLine(); // skip empty line
         // System.out.println(line);
         // System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

         // // Read file content and save it
         // String fileSaveName = caption + "_" + date + "_" + fileName;
         // // File file = new File(uploadDir, fileSaveName);

         // // try (FileOutputStream fileOut = new FileOutputStream(file)) {
         // // byte[] buffer = new byte[1024];
         // // int bytesRead;

         // // ByteArrayOutputStream baos = new ByteArrayOutputStream();
         // // boolean boundaryFound = false;

         // // while ((bytesRead = inputStream.read(buffer)) != -1) {
         // // // fileOut.write(buffer, 0, bytesRead);

         // // baos.write(buffer, 0, bytesRead);
         // // String accumulatedData = baos.toString("ISO-8859-1");
         // // int boundaryIndex = accumulatedData.indexOf("--" + boundary + "--");
         // // if (boundaryIndex != -1) {

         // // fileOut.write(baos.toByteArray(), 0, boundaryIndex);
         // // boundaryFound = true;
         // // break;
         // // }
         // // }

         // if (handleFileUpload(inputStream, uploadDir, boundary, fileSaveName)) {
         // break;
         // }
         // }
         // }

         // System.out.println("uploadedFile " + file);

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

   private boolean handleFileUpload(InputStream inputStream, File uploadDir, String boundary, String filename) {
      // Create a File object to save the uploaded file
      File file = new File("uploads", filename); // Change this to your desired filename

      try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();) {
         // InputStream inputStream = req.getInputStream()) { // Use InputStream from
         // socket

         byte[] buffer = new byte[1024];
         int bytesRead;

         boolean boundaryFound = false;

         // Read data from the input stream
         while ((bytesRead = inputStream.read(buffer)) != -1) {
            // Write bytes to ByteArrayOutputStream for processing
            byteArrayOutputStream.write(buffer, 0, bytesRead);

            // Convert accumulated bytes to string to check for boundary
            String accumulatedData = byteArrayOutputStream.toString("ISO-8859-1");

            // Check for the boundary
            int boundaryIndex = accumulatedData.indexOf("--" + boundary + "--");
            if (boundaryIndex != -1) {
               // Write only up to the boundary
               byte[] bytes = byteArrayOutputStream.toByteArray();
               try (FileOutputStream fileOut = new FileOutputStream(file)) {
                  fileOut.write(bytes, 0, boundaryIndex);
               }

               boundaryFound = true;
               break; // Exit loop after finding boundary
            }
         }

         if (boundaryFound) {
            System.out.println("File uploaded successfully: " + file.getAbsolutePath());
         } else {
            System.out.println("Boundary not found. File may be incomplete.");
         }

      } catch (IOException e) {
         e.printStackTrace(); // Handle exceptions appropriately
      }
      return true;
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