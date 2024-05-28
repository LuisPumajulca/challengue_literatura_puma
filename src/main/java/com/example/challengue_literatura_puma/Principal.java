package com.example.challengue_literatura_puma;

import com.example.challengue_literatura_puma.model.*;
import com.example.challengue_literatura_puma.repository.IAutoresRepository;
import com.example.challengue_literatura_puma.service.ConsumoAPI;
import com.example.challengue_literatura_puma.service.ConvierteDatos;


import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private IAutoresRepository repository;

    public Principal(IAutoresRepository repository) {
        this.repository = repository;
    }


    public void mostrarMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    --------------------------------------------
                                 📑 MENU PRINCIPAL 📑
                    --------------------------------------------
                    1 - Buscar Libro por Titulo
                    2 - Buscar Autor por Nombre
                    3 - Listar Libros Registrados
                    4 - Listar Autores Registrados
                    5 - Listar Autores Vivos
                    6 - Listar Libros por Idioma
                    7 - Listar Autores por año
                    8 - Top 10 Libros mas buscados
                    9 - Generar Estadisticas
                         
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    buscarAutorPorNombre();
                    break;
                case 3:
                    listarLibrosRegistrados();
                    break;
                case 4:
                    listarAutoresRegistrados();
                    break;
                case 5:
                    listarAutoresVivos();
                    break;
                case 6:
                    listarLibrosPorIdioma();
                    break;
                case 7:
                    listarAutoresPorAnio();
                    break;
                case 8:
                    top10Libros();
                    break;
                case 9:
                    generarEstadisticas();
                    break;
                case 0:
                    System.out.println("Cerrando Aplicación...");
                    break;
                default:
                    System.out.println("Opción Invalida!, intente nuevamente");
            }
        }
    }


    private void buscarLibroPorTitulo() {
        System.out.println("""
            ---------------------------------------
             🔍 BÚSQUEDA DE LIBRO POR TÍTULO 🔍
            ---------------------------------------
            Ingrese el nombre del libro que desea buscar:
            """);
        var nombre = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + nombre.replace(" ", "+").toLowerCase());

        if (!json.isEmpty() && !json.contains("\"count\":0,\"next\":null,\"previous\":null,\"results\":[]")) {
            var datos = convierteDatos.obtenerDatos(json, Datos.class);
            Optional<DatosLibro> libroBuscado = datos.libros().stream().findFirst();

            if (libroBuscado.isPresent()) {
                mostrarInformacionLibro(libroBuscado.get());

                try {
                    manejarLibroEncontrado(libroBuscado.get(), nombre);
                } catch (Exception e) {
                    System.out.println("Advertencia! " + e.getMessage());
                }
            } else {
                System.out.println("Libro no encontrado!");
            }
        }
    }

    private void mostrarInformacionLibro(DatosLibro libro) {
        String autores = libro.autores().stream()
                .map(DatosAutores::nombre)
                .collect(Collectors.joining(", "));
        System.out.println("""
            -------------------------------
            📚 Libro Encontrado 📚
            -------------------------------
            Título: %s
            Autor(es): %s
            Descargas: %d
            """.formatted(libro.titulo(), autores, libro.descargas()));
    }

    private void manejarLibroEncontrado(DatosLibro libroBuscado, String nombre) {
        List<Libro> libroEncontrado = List.of(new Libro(libroBuscado));
        DatosAutores autorAPI = libroBuscado.autores().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el autor en el API."));

        Optional<Autores> autorBD = repository.buscarAutorPorNombre(autorAPI.nombre());
        Optional<Libro> libroOptional = repository.buscarLibroPorNombre(nombre);

        if (libroOptional.isPresent()) {
            System.out.println("Libro ya guardado");
        } else {
            Autores autores = autorBD.orElseGet(() -> {
                Autores nuevoAutor = new Autores(autorAPI);
                repository.save(nuevoAutor);
                return nuevoAutor;
            });

            autores.setLibros(libroEncontrado);
            repository.save(autores);
            System.out.println("Libro y autor guardados exitosamente.");
        }
    }

    public void buscarAutorPorNombre() {
        System.out.println("""
            -------------------------------
             📙 BUSCAR AUTOR POR NOMBRE 📙
            -------------------------------
            """);
        System.out.println("Ingrese el nombre del autor a buscar:");
        var nombre = teclado.nextLine();
        Optional<Autores> autores = repository.buscarAutorPorNombre(nombre);

        if (autores.isPresent()) {
            mostrarInformacionAutor(autores.get());
        } else {
            System.out.println("""
                -------------------------------
                ❌ Autor no encontrado
                -------------------------------
                """);
        }
    }

    private void mostrarInformacionAutor(Autores autor) {
        String libros = autor.getLibros().stream()
                .map(Libro::getTitulo)
                .collect(Collectors.joining(", "));

        System.out.println("""
            -------------------------------
            📘 Autor Encontrado 📘
            -------------------------------
            Nombre: %s
            Fecha de Nacimiento: %s
            Fecha de Fallecimiento: %s
            Libros: %s
            -------------------------------
            """.formatted(autor.getNombre(),
                        autor.getNacimiento(),
                        autor.getFallecimiento(),
                        libros.isEmpty() ? "Ninguno" : libros));
    }

    public void listarLibrosRegistrados() {
        List<Libro> libros = repository.buscarTodosLosLibros();

        if (libros.isEmpty()) {
            System.out.println("""
                -------------------------------
                ❌ No hay libros registrados
                -------------------------------
                """);
            return;
        }

        libros.forEach(this::mostrarInformacionLibro);
    }

    private void mostrarInformacionLibro(Libro libro) {
        System.out.println("""
            -------------------------------
            📚 Libro Registrado 📚
            -------------------------------
            Título: %s
            Autor: %s
            Idioma: %s
            Número de descargas: %d
            -------------------------------
            """.formatted(libro.getTitulo(),
                libro.getAutores().getNombre(),
                libro.getIdioma().getIdioma(),
                libro.getDescargas()));
    }

    public void listarAutoresRegistrados() {
        System.out.println("""
            ----------------------------------
             📗 LISTAR AUTORES REGISTRADOS 📗
            ----------------------------------
            """);
        List<Autores> autores = repository.findAll();

        if (autores.isEmpty()) {
            System.out.println("""
                ----------------------------------
                ❌ No hay autores registrados
                ----------------------------------
                """);
            return;
        }

        autores.forEach(this::mostrarInformacionAutor);
    }

    public void listarAutoresVivos() {
        System.out.println("""
            -----------------------------
              📒 LISTAR AUTORES VIVOS 📒
            -----------------------------
            """);
        System.out.println("Introduzca un año para verificar el autor(es) que desea buscar:");

        try {
            var fecha = Integer.valueOf(teclado.nextLine());
            List<Autores> autores = repository.buscarAutoresVivos(fecha);

            if (autores.isEmpty()) {
                System.out.println("""
                    ------------------------------------
                    ❌ No hay autores vivos en el año registrado
                    ------------------------------------
                    """);
            } else {
                autores.forEach(this::mostrarInformacionAutorVivo);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ingresa un año válido: " + e.getMessage());
        }
    }

    private void mostrarInformacionAutorVivo(Autores autor) {
        String libros = autor.getLibros().stream()
                .map(Libro::getTitulo)
                .collect(Collectors.joining(", "));

        System.out.println("""
            ----------------------------------
             📘 Autor Vivo 📘
            ----------------------------------
            Nombre: %s
            Fecha de Nacimiento: %s
            Fecha de Fallecimiento: %s
            Libros: %s
            ----------------------------------
            """.formatted(autor.getNombre(),
                autor.getNacimiento(),
                autor.getFallecimiento() != null ? autor.getFallecimiento() : "N/A",
                libros.isEmpty() ? "Ninguno" : libros));
    }

    public void listarLibrosPorIdioma() {
        System.out.println("""
            --------------------------------
             📘 LISTAR LIBROS POR IDIOMA 📘
            --------------------------------
            """);
        var menu = """
            ---------------------------------------------------
            Seleccione el idioma del libro que desea encontrar:
            ---------------------------------------------------
            1 - Español
            2 - Francés
            3 - Inglés
            4 - Portugués
            ----------------------------------------------------
            """;
        System.out.println(menu);

        try {
            var opcion = Integer.parseInt(teclado.nextLine());

            switch (opcion) {
                case 1 -> buscarLibrosPorIdioma("es");
                case 2 -> buscarLibrosPorIdioma("fr");
                case 3 -> buscarLibrosPorIdioma("en");
                case 4 -> buscarLibrosPorIdioma("pt");
                default -> System.out.println("Opción inválida!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Opción no válida: " + e.getMessage());
        }
    }

    private void buscarLibrosPorIdioma(String idioma) {
        try {
            Idioma idiomaEnum = Idioma.valueOf(idioma.toUpperCase());
            List<Libro> libros = repository.buscarLibrosPorIdioma(idiomaEnum);
            if (libros.isEmpty()) {
                System.out.println("""
                    ------------------------------------------------
                    ❌ No hay libros registrados en ese idioma
                    ------------------------------------------------
                    """);
            } else {
                libros.forEach(this::mostrarInformacionLibro);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Introduce un idioma válido en el formato especificado.");
        }
    }

    public void listarAutoresPorAnio () {
        System.out.println("""
                    ------------------------------
                     📓 LISTAR AUTORES POR AÑO 📓
                    ------------------------------
                     """);
        var menu = """
                    ------------------------------------------
                    Ingresa una opción para listar los autores
                    -------------------------------------------
                    1 - Listar autor por Año de Nacimiento
                    2 - Listar autor por año de Fallecimiento
                    -------------------------------------------
                    """;
        System.out.println(menu);
        try {
            var opcion = Integer.valueOf(teclado.nextLine());
            switch (opcion) {
                case 1:
                    ListarAutoresPorNacimiento();
                    break;
                case 2:
                    ListarAutoresPorFallecimiento();
                    break;
                default:
                    System.out.println("Opción inválida!");
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Opción no válida: " + e.getMessage());
        }
    }

    public void ListarAutoresPorNacimiento () {
        System.out.println("""
                    ---------------------------------------------
                     📖 BUSCAR AUTOR POR SU AÑO DE NACIMIENTO 📖
                    ---------------------------------------------
                    """);
        System.out.println("Introduzca el año de nacimiento del autor que desea buscar:");
        try {
            var nacimiento = Integer.valueOf(teclado.nextLine());
            List<Autores> autores = repository.listarAutoresPorNacimiento(nacimiento);
            if (autores.isEmpty()) {
                System.out.println("No existen autores con año de nacimiento igual a " + nacimiento);
            } else {
                System.out.println();
                autores.forEach(a -> System.out.println(
                        "Autor: " + a.getNombre() +
                                "\nFecha de Nacimiento: " + a.getNacimiento() +
                                "\nFecha de Fallecimiento: " + a.getFallecimiento() +
                                "\nLibros: " + a.getLibros().stream().map(l -> l.getTitulo()).collect(Collectors.toList()) + "\n"
                ));
            }
        } catch (NumberFormatException e) {
            System.out.println("Año no válido: " + e.getMessage());
        }
    }

    public void ListarAutoresPorFallecimiento () {
        System.out.println("""
                    ---------------------------------------------------------
                     📖  BUSCAR LIBROS POR AÑO DE FALLECIMIENTO DEL AUTOR 📖
                    ----------------------------------------------------------
                     """);
        System.out.println("Introduzca el año de fallecimiento del autor que desea buscar:");
        try {
            var fallecimiento = Integer.valueOf(teclado.nextLine());
            List<Autores> autores = repository.listarAutoresPorFallecimiento(fallecimiento);
            if (autores.isEmpty()) {
                System.out.println("No existen autores con año de fallecimiento igual a " + fallecimiento);
            } else {
                System.out.println();
                autores.forEach(a -> System.out.println(
                        "Autor: " + a.getNombre() +
                                "\nFecha de Nacimiento: " + a.getNacimiento() +
                                "\nFecha de Fallecimeinto: " + a.getFallecimiento() +
                                "\nLibros: " + a.getLibros().stream().map(l -> l.getTitulo()).collect(Collectors.toList()) + "\n"
                ));
            }
        } catch (NumberFormatException e) {
            System.out.println("Opción no válida: " + e.getMessage());
        }
    }

    public void top10Libros() {
        System.out.println("""
        -------------------------------------
           📚 TOP 10 LIBROS MÁS BUSCADOS 📚
        -------------------------------------
        """);
        List<Libro> libros = repository.top10Libros();
        System.out.println();
        libros.forEach(l -> System.out.println("""
        ----------------- LIBRO 📚 ----------------
        Título: %s
        Autor: %s
        Idioma: %s
        Número de descargas: %d
        -------------------------------------------
        """.formatted(l.getTitulo(), l.getAutores().getNombre(), l.getIdioma().getIdioma(), l.getDescargas())));
    }

    public void generarEstadisticas() {
        System.out.println("""
        ----------------------------
         📊 GENERAR ESTADÍSTICAS 📊
        ----------------------------
        """);

        var json = consumoAPI.obtenerDatos(URL_BASE);
        var datos = convierteDatos.obtenerDatos(json, Datos.class);

        IntSummaryStatistics est = datos.libros().stream()
                .filter(l -> l.descargas() > 0)
                .collect(Collectors.summarizingInt(DatosLibro::descargas));

        Integer media = (int) est.getAverage();

        System.out.println("""
        --------- ESTADÍSTICAS \uD83D\uDCCA ------------
        Media de descargas: %d
        Máxima de descargas: %d
        Mínima de descargas: %d
        Total registros para calcular las estadísticas: %d
        ---------------------------------------------------
        """.formatted(media, est.getMax(), est.getMin(), est.getCount()));
    }
}
