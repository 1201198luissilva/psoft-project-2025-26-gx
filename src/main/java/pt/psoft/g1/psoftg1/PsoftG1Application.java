package pt.psoft.g1.psoftg1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pt.psoft.g1.psoftg1.external.service.BookExternalService;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class PsoftG1Application implements CommandLineRunner {

	private final BookExternalService bookExternalService;

	public static void main(String[] args) {
		SpringApplication.run(PsoftG1Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Testing external book service...");
		bookExternalService.getIsbnByTitle("The Great Gatsby");
		log.info("External book service test completed.");
	}

}
