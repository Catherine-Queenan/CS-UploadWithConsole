import java.io.*;
import java.nio.charset.StandardCharsets;

public class TestUploadServlet {

    public static void main(String[] args) {
        try {
            // Ensure the uploads directory exists
            File uploadDir = new File("uploads");
            if (!uploadDir.exists()) {
                uploadDir.mkdir();  // Create the directory if it doesn't exist
            }

            // Create a ByteArrayOutputStream to capture the output
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HttpServletResponse response = new HttpServletResponse(outputStream);
            
            // Simulate HttpServletRequest for GET
            InputStream getInputStream = new ByteArrayInputStream(new byte[0]);
            HttpServletRequest getRequest = new HttpServletRequest(getInputStream, null);
            
            // Create an instance of UploadServlet
            UploadServlet servlet = new UploadServlet();
            
            // Test handleRequest with "get" method
            System.out.println("Testing GET Method:");
            servlet.handleRequest("get", getRequest, response);
            
            // Capture the output for GET
            String getOutput = outputStream.toString();
            System.out.println("Captured Output (GET):\n" + getOutput);
            
            // Check if specific output was printed for GET
            if (getOutput.contains("Loading 'UploadServlet' class dynamically...") && 
                getOutput.contains("Invoking method 'get' dynamically...")) {
                System.out.println("GET Test passed: Correct outputs were printed.");
            } else {
                System.out.println("GET Test failed: Outputs did not match expected results.");
            }
            
            // Clear the output stream for the POST test
            outputStream.reset();

            // Simulate HttpServletRequest for POST with boundary and data
            String boundary = "------Boundary";
            String postData = boundary + "\r\n" +
                              "Content-Disposition: form-data; name=\"caption\"\r\n\r\n" +
                              "Test Caption\r\n" +
                              boundary + "\r\n" +
                              "Content-Disposition: form-data; name=\"date\"\r\n\r\n" +
                              "2024-10-13\r\n" +
                              boundary + "\r\n" +
                              "Content-Disposition: form-data; name=\"fileName\"; filename=\"testfile.txt\"\r\n" +
                              "Content-Type: text/plain\r\n\r\n" +
                              "This is a test file content.\r\n" +
                              boundary + "--\r\n";
            
            InputStream postInputStream = new ByteArrayInputStream(postData.getBytes(StandardCharsets.UTF_8));
            HttpServletRequest postRequest = new HttpServletRequest(postInputStream, boundary);

            // Test handleRequest with "post" method
            System.out.println("\nTesting POST Method:");
            response.setContentType("text/plain"); // Set the content type
            servlet.handleRequest("post", postRequest, response);
            
            // Capture the output for POST
            String postOutput = outputStream.toString();
            System.out.println("Captured Output (POST):\n" + postOutput);
            
            // Expected file name (ensure it matches how your doPost constructs it)
            String expectedFileName = "Test Caption_2024-10-13_testfile.txt";
            
            // Check if specific output was printed for POST
            if (postOutput.contains("File will be saved as: " + expectedFileName)) {
                System.out.println("POST Test passed: Expected file name found in output.");
            } else {
                System.out.println("POST Test failed: Expected file name not found in output.");
                System.out.println("Expected File Name: " + expectedFileName);
                System.out.println("Actual Output:\n" + postOutput);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
