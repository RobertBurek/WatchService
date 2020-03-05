package com.example.microservices.service;

import com.example.microservices.model.Transaction;
import com.example.microservices.repsitory.RepoApi;
import com.example.microservices.repsitory.RepoImp;
import com.sun.nio.file.SensitivityWatchEventModifier;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.example.microservices.MicroservicesApplication.*;

/**
 * Klasa śledząca zmiany w folderze oraz zapis danych z pliku,
 * gdy plik się pojawi w formacie *.csv
 * <p>
 * Entity w formacie pól:
 * long id;
 * String dateT;
 * String timeT;
 * String courseT;
 * String volumeT;
 *
 * @author Robert Burek
 */

@Log4j2
@Service
public class FileWatcher {

    RepoApi repoApi;

    public FileWatcher(RepoImp repoImp) {
        this.repoApi = repoImp;
    }

//    private List<Transaction> linesTransaction = new ArrayList<>();
    long totalTime = 0;

    public void FileWatcherMetod() throws IOException, InterruptedException, SQLException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
//        Path path = Paths.get(System.getProperty("user.home") + "\\Desktop");
        Path path = Paths.get("C:\\Users\\user\\Documents\\Notowania 4 MAX\\Pliki CSV");
        log.info("Śledzony folder: " + path);
//        path.register(
//                watchService,
//                StandardWatchEventKinds.ENTRY_MODIFY);
        path.register(
                watchService,
                new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_MODIFY},
                SensitivityWatchEventModifier.HIGH);
        WatchKey key;
        while ((key = watchService.take()) != null) {
            long millisActualTime = System.currentTimeMillis();
            List<WatchEvent<?>> listEvents = key.pollEvents();
            for (WatchEvent<?> event : listEvents) {
                log.info(ANSI_HIGHGREEN + "File/folder affected: " + event.context() + ". Ilość zdarzeń: " + listEvents.size() + ANSI_RESET);
                if (event.context().toString().toLowerCase().endsWith(".csv")) {
                    String nameTable = event.context().toString().replace(".", "_");
//                    repoApi.createTables(nameTable);
//                    repoApi.saveAllToTable(nameTable, readFromFile(path + "\\" + event.context()));
                    Thread thread = new Thread(() -> {
                        try {
                            log.info(Thread.currentThread().getName());
                            repoApi.createTables(nameTable);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        repoApi.saveAllToTable(nameTable, FileWatcher.this.readFromFile(path + "\\" + event.context()));
                    });
                    log.info(ANSI_RED + "Wykonane w odrębym wątek: " + (thread.getName()) + ANSI_RESET);
                    thread.start();

                } else
                    log.info(ANSI_RED + "No reading. Folder affected: " + event.context() + ANSI_RESET);
            }
            key.reset();
            long executionTime = System.currentTimeMillis() - millisActualTime;
            log.info("Czas wykonania działania: " + (totalTime += executionTime));
        }
    }

    public Iterable<Transaction> readFromFile(String name) {
//        linesTransaction.clear();
        List<Transaction> linesTransaction = new ArrayList<>();
        Scanner scanner = null;
        File text = new File(name);
        try {
            scanner = new Scanner(text);
            String dateT = "";
            String timeT = "";
            String courseT = "";
            String volumeT = "";
            while (scanner.hasNextLine()) {
                Scanner lineScanner = new Scanner(scanner.nextLine());
                lineScanner.useDelimiter(",");
                if (lineScanner.hasNextLine()) dateT = lineScanner.next();
                if (lineScanner.hasNextLine()) timeT = lineScanner.next();
                if (lineScanner.hasNextLine()) courseT = lineScanner.next();
                if (lineScanner.hasNextLine()) volumeT = lineScanner.next();
                linesTransaction.add(new Transaction(dateT, timeT, courseT, volumeT));
                lineScanner.close();
            }
        } catch (FileNotFoundException e) {
            log.info("Błąd: " + e.getMessage());
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        log.info(ANSI_YELLOW + "Ścieżka i plik: " + name + "  -> ilość wierszy: " + linesTransaction.size() + ANSI_RESET);
        return linesTransaction;
    }
}
