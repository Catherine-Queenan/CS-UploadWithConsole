import java.io.*;

public class HttpServletResponse {
    private OutputStream outputStream;
    private PrintWriter writer;
    private String contentType;

    public HttpServletResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.writer = new PrintWriter(outputStream, true);
    }

    // Simulate setting the content type
    public void setContentType(String contentType) {
        this.contentType = contentType;
        // You can include the content type in your HTTP response headers if needed
        writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type: " + contentType);
        writer.println(); // Blank line to indicate end of headers
    }

    // Simulate getting the writer for the response
    public PrintWriter getWriter() {
        return this.writer;
    }

    // Send response body
    public void sendResponse(String responseBody) throws IOException {
        writer.println(responseBody);
        writer.flush();
    }

    public void flushBuffer() throws IOException {
        outputStream.flush();
    }
}
