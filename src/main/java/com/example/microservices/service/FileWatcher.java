package com.example.microservices.service;

import com.example.microservices.model.Transaction;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.example.microservices.MicroservicesApplication.*;

/**
 * Klasa śledząca zmiany w folderze oraz zapis danych z pliku,
 * gdy plik się pojawi w formacie *.csv
 *
 * Entity w formacie pól:
 * long id;
 * String dateT;
 * String timeT;
 * String courseT;
 * String volumeT;
 *
 * @author Robert Burek
 *
 */

@Log4j2
@Service
public class FileWatcher {

    private Connection connection;
    private static Statement statement;

    private List<Transaction> wierszePlikuTrans = new ArrayList<>();

    public void FileWatcherMetod() throws IOException, InterruptedException, SQLException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(System.getProperty("user.home") + "\\Desktop");
        log.info("Śledzony folder: " + path);
        path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_MODIFY);
        WatchKey key;
        while ((key = watchService.take()) != null) {
            List<WatchEvent<?>> listEvents = key.pollEvents();
            for (WatchEvent<?> event : listEvents) {
                log.info(ANSI_HIGHGREEN + "File/folder affected: " + event.context() + ". Ilość zdarzeń: " + listEvents.size() + ANSI_RESET);
                if (event.context().toString().toLowerCase().endsWith(".csv")) {
                    String nameTable = event.context().toString().replace(".", "_");
                    createTables(nameTable);
                    saveAllToTable(nameTable, readFromFile(path + "\\" + event.context()));
                } else
                    log.info(ANSI_RED + "No reading. Folder affected: " + event.context() + ANSI_RESET);
            }
            key.reset();
        }
    }

    public Iterable<Transaction> readFromFile(String name) {
        wierszePlikuTrans.clear();
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
                wierszePlikuTrans.add(new Transaction(dateT, timeT, courseT, volumeT));
                lineScanner.close();
            }
        } catch (FileNotFoundException e) {
            log.info("Błąd: " + e.getMessage());
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        log.info(ANSI_YELLOW + "Ścieżka i plik: " + name + "  -> ilość wierszy: " + wierszePlikuTrans.size() + ANSI_RESET);
        return wierszePlikuTrans;
    }

    public void createTables(String nameTable) throws SQLException {
        try {
            String dbURL = String.format("jdbc:h2:mem:notowania");
            connection = DriverManager.getConnection(dbURL, "sa", "");
            statement = connection.createStatement();
        } catch (SQLException e) {
            log.info(ANSI_RED + "Problem z otwarciem polaczenia" + e.getMessage() + ANSI_RESET);
        }
        log.info(ANSI_HIGHGREEN + "Wywołanie metody tworzenia tabeli: " + ANSI_BLUE + nameTable + ANSI_RESET);
        String financialInstrument = String.format("DROP TABLE IF EXISTS %s; CREATE TABLE `%s` " +
                "(`id` INTEGER NOT NULL AUTO_INCREMENT, " +
                "`dateT` varchar(255), " +
                "`timeT` varchar(255), " +
                "`courseT` varchar(255), " +
                "`volumeT` varchar(255), " +
                "PRIMARY KEY (`id`));", nameTable, nameTable);
        try {
            statement.execute(financialInstrument);
        } catch (SQLException e) {
            log.error(ANSI_RED + "Błąd przy tworzeniu tabeli: " + nameTable + ANSI_RESET);
            log.error(ANSI_RED + "Dokładniej: " + e.getMessage() + ANSI_RESET);
        }
    }

    static void saveAllToTable(String nameTable, Iterable<Transaction> listT) {
        for (Transaction t : listT) {
            String insert = String.format(
                    "INSERT INTO %s(dateT, timeT , courseT, volumeT) VALUES ('%s','%s','%s','%s')",
                    nameTable,
                    t.getDateT(),
                    t.getTimeT(),
                    t.getCourseT(),
                    t.getVolumeT());
            try {
                log.info(ANSI_BLUE + insert + ANSI_RESET);
                statement.executeUpdate(insert);
            } catch (SQLException e) {
                log.error(ANSI_RED + "Problem z zapisem danych do tablicy!!! Błąd: " + e.getMessage() + ANSI_RESET);
            }
        }
    }
}
