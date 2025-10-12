package webapp.AwesomeCollect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AwesomeCollectApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwesomeCollectApplication.class, args);
	}

}
