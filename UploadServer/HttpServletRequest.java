import java.io.*;
public class HttpServletRequest {
   private InputStream inputStream = null;
   private BufferedReader in = null;
   private OutputStream outputStream = null;
   public HttpServletRequest(BufferedReader in, InputStream inputStream) {
      this.inputStream = inputStream; 
      this.in = in;
      this.outputStream = outputStream;
   }
   
   public InputStream getInputStream() {return inputStream;}
   public BufferedReader getBufferedIn() {return in;}
   public OutputStream getOutputStream() {return outputStream;}

}