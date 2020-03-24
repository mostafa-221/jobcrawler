package nl.ordina.jobcrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class JobcrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobcrawlerApplication.class, args);
	}

}
