package comdpapyru.greenhome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GreenHomeApplication {
	public static void main(String[] args) {
        System.out.println(
                "    _ooOoo_\n" +
                        "   o8888888o   佛祖保佑\n" +
                        "    (| -_- |)  永无BUG");
		SpringApplication.run(GreenHomeApplication.class, args);
	}
}
