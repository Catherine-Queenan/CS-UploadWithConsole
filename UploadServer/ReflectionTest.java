import java.io.*;
import java.lang.reflect.Method;

public class ReflectionTest {
    public static void main(String[] args) {
        try {
            // Simulating HttpServletRequest with dummy InputStream
            String requestData = "--boundary\r\n" +
                    "Content-Disposition: form-data; name=\"caption\"\r\n\r\n" +
                    "Test\r\n" +
                    "--boundary\r\n" +
                    "Content-Disposition: form-data; name=\"date\"\r\n\r\n" +
                    "2024-01-01\r\n" +
                    "--boundary\r\n" +
                    "Content-Disposition: form-data; name=\"fileName\"; filename=\"test.txt\"\r\n" +
                    "Content-Type: text/plain\r\n\r\n" +
                    "This is some test content for the file upload.\r\n" +
                    "--boundary--\r\n"; // Simulated form data with file

            InputStream inputStream = new ByteArrayInputStream(requestData.getBytes());
            String boundary = "boundary"; // Set the same boundary used in requestData
            HttpServletRequest request = new HttpServletRequest(inputStream, boundary);

            // Simulating HttpServletResponse with ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HttpServletResponse response = new HttpServletResponse(outputStream);

            // Create an instance of your class that contains handleRequest
            ReflectionTest handler = new ReflectionTest();

            // Array of method names to test
            String[] methodNames = { "get", "post" }; // Testing both GET and POST

            // Loop through method names and call handleRequest for each
            for (String methodName : methodNames) {
                // Reset the output stream for each method call
                outputStream.reset();

                // Call handleRequest with the current method name
                handler.handleRequest(methodName, request, response);

                // Output the response captured in the output stream
                String responseOutput = outputStream.toString();
                System.out.println("Captured Response for method '" + methodName + "':");
                System.out.println(responseOutput);
            }

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Using reflection to dynamically access the GET or POST method of the servlet
     * while printing updates on progress to the output stream.
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
            Method method = servletClass.getDeclaredMethod(methodToInvoke, HttpServletRequest.class,
                    HttpServletResponse.class);
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
        } catch (Exception e) {
            System.err.println("Error: An unexpected error occurred: " + e.getMessage());
            e.printStackTrace(); // For debugging purposes
        }
    }
}
