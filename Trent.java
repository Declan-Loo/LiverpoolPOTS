import java.util.Scanner;

class Trent {
  public static void main(String[] args) {
    Thread[] threads = new Thread[10];
    
    // Create all threads first
    for(int i = 0; i < 10; i++){
        threads[i] = new Thread(new VoteThread(i));
    }
    
    // Start all threads at the same time
    for(int i = 0; i < 10; i++){
        threads[i].start();
    }
    
    System.out.println("Started 10 threads!");
    System.out.println("Type 'quit' to stop all threads:");
    
    Scanner scanner = new Scanner(System.in);
    while (true) {
        String input = scanner.nextLine();
        if ("quit".equalsIgnoreCase(input.trim())) {
            System.out.println("Stopping all threads...");
            VoteThread.stopAllThreads();
            break;
        }
    }
    
    scanner.close();

  }

}
