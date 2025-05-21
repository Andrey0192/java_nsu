package io;

import core.TableDataList;
import model.Column;
import model.Row;
import model.Table;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Утилиты для импорта/экспорта таблиц:
 * - сохранение и загрузка всех таблиц (.db)
 * - экспорт/импорт одной таблицы в CSV
 */
public class DatabaseIO {

    /**
     * Загружает таблицы из .db-файла (файл содержит Map<String, Table>).
     */
    public static void importAllTables(TableDataList db, String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            Map<String, Table> loaded = (Map<String, Table>) ois.readObject();
            db.clear(); // очищаем текущие
            for (Map.Entry<String, Table> entry : loaded.entrySet()) {
                db.loadTable(entry.getKey(), entry.getValue());
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to import tables from " + fileName, e);
        }
    }

    /**
     * Сохраняет все таблицы в файл как Map<String, Table>.
     */
    public static void exportAllTables(TableDataList db, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(new HashMap<>(db.getTables())); // серилизуем копию
        } catch (IOException e) {
            throw new RuntimeException("Failed to export tables to " + fileName, e);
        }
    }

    /**
     * Экспортирует одну таблицу в CSV.
     */
    public static void exportTableToCsv(TableDataList db, String tableName, String fileName) {
        Table table = db.getTable(tableName);
        if (table == null) throw new IllegalArgumentException("No table: " + tableName);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            // заголовок
            List<Column> columns = table.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                bw.write(columns.get(i).getName());
                if (i < columns.size() - 1) bw.write(',');
            }
            bw.newLine();

            // строки
            for (Row row : table.getRows()) {
                List<Object> values = row.getValues();
                for (int i = 0; i < values.size(); i++) {
                    bw.write(String.valueOf(values.get(i)));
                    if (i < values.size() - 1) bw.write(',');
                }
                bw.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to export " + tableName + " to scv", e);
        }
    }

    /**
     * Импортирует CSV-файл в существующую таблицу.
     */
    public static void importTableFromCsv(TableDataList db, String tableName, String fileName) {
        Table table = db.getTable(tableName);
        if (table == null) throw new IllegalArgumentException("No table: " + tableName);

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String header = br.readLine(); // пропускаем заголовок
            if (header == null) throw new IOException("CSV file is empty");

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                List<Object> values = new ArrayList<>();
                for (String s : parts) {
                    values.add(s.trim());
                }
                table.addRow(new Row(values));
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to import scv to table " + tableName, e);
        }
    }
}
