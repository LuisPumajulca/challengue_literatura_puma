package com.example.challengue_literatura_puma.model;

public enum Idioma {
    ES("es"),
    EN("en"),
    FR("fr"),
    PT("pt");

    private String idioma;

    Idioma(String idioma) {
        this.idioma = idioma;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public static Idioma fromString(String text) {
        for (Idioma idioma : Idioma.values()) {
            if (idioma.idioma.equalsIgnoreCase(text)) {
                return idioma;
            }
        }
        throw new IllegalArgumentException("Ningun Libro Encontrado " + text + " found");
    }
}
