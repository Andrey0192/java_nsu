package gui;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        // Запускаем JavaFX–приложение, класс DatabaseGui должен
        // наследовать javafx.application.Application
        Application.launch(DatabaseClientGui.class, args);
    }
}
