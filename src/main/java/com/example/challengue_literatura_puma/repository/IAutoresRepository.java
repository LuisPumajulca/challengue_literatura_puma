package com.example.challengue_literatura_puma.repository;

import com.example.challengue_literatura_puma.model.Autores;
import com.example.challengue_literatura_puma.model.Idioma;
import com.example.challengue_literatura_puma.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IAutoresRepository extends JpaRepository<Autores, Long> {

    @Query("SELECT a FROM Libro l JOIN l.autores a WHERE a.nombre LIKE %:nombre%")
    Optional<Autores> buscarAutorPorNombre(@Param("nombre") String nombre);

    @Query("SELECT l FROM Libro l JOIN l.autores a WHERE l.titulo LIKE %:nombre%")
    Optional<Libro> buscarLibroPorNombre(@Param("nombre") String nombre);

    @Query("SELECT a FROM Autores a WHERE a.fallecimiento > :fecha")
    List<Autores> buscarAutoresVivos(@Param("fecha") Integer fecha);

    @Query("SELECT l FROM Autores a JOIN a.libros l WHERE l.idioma = :idioma")
    List<Libro> buscarLibrosPorIdioma(@Param("idioma") Idioma idioma);
    @Query("SELECT l FROM Autores a JOIN a.libros l ORDER BY l.descargas DESC LIMIT 10")
    List<Libro> top10Libros();

    @Query("SELECT a FROM Autores a WHERE a.nacimiento = :fecha")
    List<Autores> listarAutoresPorNacimiento(@Param("fecha") Integer fecha);

    @Query("SELECT a FROM Autores a WHERE a.fallecimiento = :fecha")
    List<Autores> listarAutoresPorFallecimiento(@Param("fecha") Integer fecha);

    @Query("SELECT l FROM Autores a JOIN a.libros l")
    List<Libro> buscarTodosLosLibros();
}
