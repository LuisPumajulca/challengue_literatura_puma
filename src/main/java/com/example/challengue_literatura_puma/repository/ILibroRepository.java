package com.example.challengue_literatura_puma.repository;

import com.example.challengue_literatura_puma.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILibroRepository extends JpaRepository<Libro, Long> {
}
