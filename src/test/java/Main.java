import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    private static long start;

    @SneakyThrows
    public static void main(String[] args) {
        start = System.currentTimeMillis() - 14000;
        System.out.println(changeMapIn());
    }

    public static String changeMapIn() {
        Date date = new Date(180000 - (System.currentTimeMillis() - start));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        return simpleDateFormat.format(date);
    }

}
