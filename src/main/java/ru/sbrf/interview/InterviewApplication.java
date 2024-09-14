package ru.sbrf.interview;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.sbrf.interview.service.DemoService;

@SpringBootApplication
public class InterviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(DemoService demoService) {
        return args -> {
            try {
                demoService.saveAuthorWithBooks();
            } catch (Exception e) {
                //ignore
            }
        };
    }

}
