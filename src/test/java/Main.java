import lombok.SneakyThrows;
import org.bukkit.entity.Player;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
                System.out.println(System.currentTimeMillis() + " RUNNING!!!!");

                Object string = "prova";
                String string1 = convert(string);
                System.out.println(string1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        System.out.println(System.currentTimeMillis() + " END!");
    }

    public static String convert(Object object) {
        String string = null;
        if (object instanceof Player) {
            string = ((Player) object).getName();
            System.out.println("PLAYER: " + string);
        } else if (object instanceof String) {
            string = (String) object;
            System.out.println("STRING: " + string);
        }
        return string;
    }

}
