package com.example.challengue_literatura_puma.service;

public interface IConvierteDatos {
     <T> T obtenerDatos(String json, Class<T> clase);
}
