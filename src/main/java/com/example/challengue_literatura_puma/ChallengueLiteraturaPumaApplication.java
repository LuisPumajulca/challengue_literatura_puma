package com.example.challengue_literatura_puma;

import com.example.challengue_literatura_puma.repository.ILibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChallengueLiteraturaPumaApplication implements CommandLineRunner {

    @Autowired
    private ILibroRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(ChallengueLiteraturaPumaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Principal principal = new Principal(repository);
        principal.mostrarMenu();
    }
}
