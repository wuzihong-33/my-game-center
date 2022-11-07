import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 游戏服务中心
 */
//@SpringBootApplication(scanBasePackages= {"com.mygame"})
//@EnableMongoRepositories(basePackages= {"com.mygame"})
public class WebGameCenterServerMain {
    public static void main(String[] args) {
        SpringApplication.run(WebGameCenterServerMain.class, args);
    }

}
