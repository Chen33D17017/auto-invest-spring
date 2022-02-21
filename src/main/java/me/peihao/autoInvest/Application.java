package me.peihao.autoInvest;

import me.peihao.autoInvest.repository.FearIndexRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(FearIndexRepository fearIndexRepository) {
		return args -> {
			fearIndexRepository.UpdateFearIndex();
		};
	}

}
