package com.example.challengue_literatura_puma;

import com.example.challengue_literatura_puma.model.DatosLibro;
import com.example.challengue_literatura_puma.repository.ILibroRepository;
import com.example.challengue_literatura_puma.service.ConsumoAPI;
import com.example.challengue_literatura_puma.service.ConvierteDatos;


import java.util.Scanner;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private static final String BASE_URL = "https://gutendex.com/books";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private ILibroRepository repository;

    public Principal(ILibroRepository repository) {
        this.repository = repository;
    }


    public void mostrarMenu(){
        var opcion = -1;
        while (opcion != 0){
            var menu = """
                    1 - Buscar Libro
                         
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroWeb();
                    break;
            }
        }
    }

    private DatosLibro getDatosLibros(){
        System.out.println("Ingrese eel nombre del libro a buscar");
        var nombreLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(BASE_URL + nombreLibro.replace(" ", "+"));
        System.out.println(json);
        DatosLibro datos = convierteDatos.obtenerDatos(json, DatosLibro.class);
        return datos;
    }
    private void buscarLibroWeb() {
    }

}
