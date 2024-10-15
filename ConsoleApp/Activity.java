import java.io.*;
public class Activity {
   public static void main(String[] args) throws IOException {
      new Activity().onCreate();
   }
   public Activity() {
   }
   public void onCreate() {
      AsyncTask UploadAsyncTask = new UploadAsyncTask().execute(); 
      System.out.println("Waiting for Callback");

   }
}