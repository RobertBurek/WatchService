package com.example.microservices;

import com.example.microservices.service.FileWatcher;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa uruchomieniowa projektu mikrosserwis dla maestroFinansów
 * śledzenie zmian w folderze oraz zapis danych z pliku, gdy plik się pojawi
 * Plik zapisanyh w formacie *.csv
 *
 * @author Robert Burek
 *
 */

@Log4j2
@SpringBootApplication
public class MicroservicesApplication {

    public static List<String> wierszePliku = new ArrayList<>();

    /**
     * Stałe zmieniające kolor komunikatów w log.info
     *
     * ANSI_RED - czerwony
     * ANSI_HIGHGREEN  - jasnozielony
     * ANSI_GREEN_ - zielony podreślony
     * ANSI_VIOLET - fioletowy
     * ANSI_BLUE - niebieski
     * ANSI_YELLOW - żłóty
     * ANSI_RESET - czyszczenie ustawień
     */

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_HIGHGREEN = "\u001B[92m";
    public static final String ANSI_GREEN_ = "\u001B[32;4m";
    public static final String ANSI_VIOLET = "\u001B[35m";
    public static final String ANSI_BLUE = "\u001B[94m";
    public static final String ANSI_YELLOW = "\u001B[33;1m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static void main(String[] args) throws IOException, InterruptedException, SQLException {

//        SpringApplication.run(MicroservicesApplication.class, args);

        final ConfigurableApplicationContext ctx = SpringApplication.run(MicroservicesApplication.class, args);
        final FileWatcher bean = ctx.getBean(FileWatcher.class);
        bean.FileWatcherMetod();

//        new FileWatcher();
    }

}
