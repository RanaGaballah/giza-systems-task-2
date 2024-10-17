import java.util.Random;

public class ThreadingTask {
    public static void main(String[] args) {
        Thread thread1 = new Thread(new Task("Thread 1: processing 1"));
        Thread thread2 = new Thread(new Task("Thread 2: processing 2"));
        Thread thread3 = new Thread(new Task("Thread 3: processing 3"));
        
        thread1.start();
        thread2.start();
        thread3.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException e) {
            System.err.println("Main thread interrupted: " + e.getMessage());
        }

        System.out.println("All threads have finished. Exiting main thread.");
    }
}

class Task implements Runnable {
    private final String message;

    public Task(String message) {
        this.message = message;
    }

    @Override
    public void run() {
        try {
            Random random = new Random();
            int sleepTime = random.nextInt(2000) + 1000; 
            Thread.sleep(sleepTime);
            System.out.println(message);
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
        }
    }
}