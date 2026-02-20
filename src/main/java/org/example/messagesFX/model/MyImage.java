package org.example.messagesFX.model;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class MyImage {

    String name;
    String data;

    public MyImage(Path file) {
        name = file.getFileName().toString();
        byte[] bytes;
        data = "";
        try {
            bytes = Files.readAllBytes(file);
            data = Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            System.err.println("Error al leer archivo: " + file);
        }
    }

    public String getData() {
        return data;
    }

}
