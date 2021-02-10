import lombok.SneakyThrows;

import java.util.concurrent.CountDownLatch;

public class Main {

    static CountDownLatch latch = new CountDownLatch(1);

    @SneakyThrows
    public static void main(String[] args) {

        Thread thread = new Thread(() -> {
            try {
                System.out.println("WAITING");
                latch.await();
                System.out.println("FINISHED WAITING");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();

        Thread.sleep(5000);

        Thread thread1 = new Thread(() -> {
            System.out.println("LATCHING");
            latch.countDown();
            System.out.println("DONE!");
        });
        thread1.start();

        Thread.sleep(5000);
    }


}
