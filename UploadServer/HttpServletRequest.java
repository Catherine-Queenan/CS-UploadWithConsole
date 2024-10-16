import java.io.*;
public class HttpServletRequest {
   private InputStream inputStream = null;
   private String boundary = null;
   public HttpServletRequest(InputStream inputStream, String boundary) {
      this.inputStream = inputStream; 
      this.boundary = boundary;
   }
   
   public InputStream getInputStream() {return inputStream;}
   public String getBoundary() {return boundary;}
}