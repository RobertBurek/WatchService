package com.example.microservices.repsitory;

import com.example.microservices.model.Transaction;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static com.example.microservices.MicroservicesApplication.*;

@Log4j2
@Repository
public class RepoImp implements RepoApi//, Runnable
{

    private Connection connection;
    private static Statement statement;

    @Override
    public void createTables(String nameTable) throws SQLException {
        log.info(ANSI_BLUE + "Tworzenie tabeli w wątku: " + Thread.currentThread().getName() + ANSI_RESET);
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

    @Override
    public void saveAllToTable(String nameTable, Iterable<Transaction> listT) {
        for (Transaction t : listT) {
            String insert = String.format(
                    "INSERT INTO %s(dateT, timeT , courseT, volumeT) VALUES ('%s','%s','%s','%s')",
                    nameTable,
                    t.getDateT(),
                    t.getTimeT(),
                    t.getCourseT(),
                    t.getVolumeT());
            try {
                log.info(ANSI_BLUE + insert + Thread.currentThread().getName() + ANSI_RESET);
                statement.executeUpdate(insert);
            } catch (SQLException e) {
                log.error(ANSI_RED + "Problem z zapisem danych do tablicy!!! Błąd: " + e.getMessage() + ANSI_RESET);
            }
        }
    }

    @Override
    public boolean isExistTable(String nameTable) {
        return false;
    }

}


