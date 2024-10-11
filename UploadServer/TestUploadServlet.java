import java.io.*;

public class TestUploadServlet {
    
    public static void main(String[] args) {
        try {
            // Create a ByteArrayOutputStream to capture the output
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            // Simulate HttpServletResponse
            HttpServletResponse response = new HttpServletResponse(outputStream);
            
            // Simulate HttpServletRequest for GET
            BufferedReader getReader = new BufferedReader(new StringReader("Simulated input for GET"));
            InputStream getInputStream = new ByteArrayInputStream(new byte[0]);
            HttpServletRequest getRequest = new HttpServletRequest(getReader, outputStream, getInputStream);
            
            // Create an instance of UploadServlet
            UploadServlet servlet = new UploadServlet();
            
            // Test handleRequest with "get" method
            System.out.println("Testing GET Method:");
            servlet.handleRequest("get", getRequest, response);
            
            // Capture the output for GET
            String getOutput = outputStream.toString();
            System.out.println("Captured Output (GET):\n" + getOutput);
            
            // Optional: Check if specific output was printed for GET
            if (getOutput.contains("Loading 'UploadServlet' class dynamically...") && 
                getOutput.contains("Invoking method 'get' dynamically...")) {
                System.out.println("GET Test passed: Correct outputs were printed.");
            } else {
                System.out.println("GET Test failed: Outputs did not match expected results.");
            }
            
            // Clear the output stream for the POST test
            outputStream.reset();

            // Simulate HttpServletRequest for POST
            BufferedReader postReader = new BufferedReader(new StringReader("Simulated input for POST"));
            InputStream postInputStream = new ByteArrayInputStream(new byte[0]);
            HttpServletRequest postRequest = new HttpServletRequest(postReader, outputStream, postInputStream);
            
            // Test handleRequest with "post" method
            System.out.println("\nTesting POST Method:");
            servlet.handleRequest("post", postRequest, response);
            
            // Capture the output for POST
            String postOutput = outputStream.toString();
            System.out.println("Captured Output (POST):\n" + postOutput);
            
            // Optional: Check if specific output was printed for POST
            if (postOutput.contains("Loading 'UploadServlet' class dynamically...") && 
                postOutput.contains("Invoking method 'post' dynamically...")) {
                System.out.println("POST Test passed: Correct outputs were printed.");
            } else {
                System.out.println("POST Test failed: Outputs did not match expected results.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
