import java.io.*;

public class UploadServlet {

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
            out.println("Caption: <input type=\"text\" name=\"caption\"/>");
            out.println("Date: <input type=\"date\" name=\"date\" />");
            out.println("<input type=\"file\" name=\"fileName\"/>");
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
