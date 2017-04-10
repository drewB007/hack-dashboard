package cap.ddg.hack;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ComponentScan("cap.ddg.hack")
public class HackdashApplication {

	public static void main(String[] args) {
		SpringApplication.run(HackdashApplication.class, args);
		//ApplicationContext context = new AnnotationConfigApplicationContext(RESTConfiguration.class);

	}


}
