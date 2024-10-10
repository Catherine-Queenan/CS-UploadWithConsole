import java.io.*;
public class HttpServletRequest {
   private InputStream inputStream = null;
   private int contentLength = 0;
   public HttpServletRequest(InputStream inputStream, int contentLength) {
      this.inputStream = inputStream;
      this.contentLength = contentLength; 
   }
   
   public InputStream getInputStream() {return inputStream;}

   public int getContentLength() {return contentLength;}
}