public class C extends Thread {
   public void run() {
      System.out.println("Thread C started");
      for(int k = 1; k <= 4; k++) {
        System.out.println("\t From ThreadC: k= " + k);
      }
      System.out.println("Exit from C");
   }
}