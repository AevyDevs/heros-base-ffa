import lombok.SneakyThrows;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
                System.out.println(System.currentTimeMillis() + " RUNNING!!!!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        System.out.println(System.currentTimeMillis() + " END!");
    }

}
