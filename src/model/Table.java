package model;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Таблица в памяти с возможностью сохранения в файл.
 */
public class Table implements Serializable {

    private final String name;
    private final List<Column> columns;
    private final List<Row> rows = new ArrayList<>();

    public Table(String name, List<Column> columns) {
        this.name = name;
        this.columns = new ArrayList<>(columns);
    }

    public String getName() {
        return name;
    }

    public List<Column> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public List<Row> getRows() {
        return Collections.unmodifiableList(rows);
    }


    public void addRow(Row row) {
        // Проверка not-null и unique
        for (int i = 0; i < columns.size(); i++) {
            Column col = columns.get(i);
            Object val = row.getValues().get(i);
            if (col.isNotNull() && val == null) {
                throw new IllegalArgumentException(
                        "Column " + col.getName() + " must not be null");
            }
            if (col.isUnique()) {
                for (Row existing : rows) {
                    if (Objects.equals(existing.getValues().get(i), val)) {
                        throw new IllegalArgumentException(
                                "Duplicate value for unique column " + col.getName() + ": " + val);
                    }
                }
            }
        }
        rows.add(row);
    }



    /**
     * Добавляет новую колонку в таблицу:
     * 1) проверяем, что имя не пустое и не дублируется,
     * 2) добавляем Column к метаданным,
     * 3) для каждой уже существующей строки добавляем значение null (или default).
     */
    public void addColumn(Column col) {
        String name = col.getName();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name must not be null or empty");
        }
        for (Column existing : columns) {
            if (existing.getName().equalsIgnoreCase(name)) {
                throw new IllegalArgumentException("Duplicate column name: " + name);
            }
        }
        columns.add(col);
        for (Row row : rows) {
            row.addValue(null);
        }
    }


    public void deleteColumn(String colName) {
        int idx = -1;
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getName().equalsIgnoreCase(colName)) {
                idx = i;
                break;
            }
        }
        if (idx < 0) {
            throw new IllegalArgumentException("No such column: " + colName);
        }
        columns.remove(idx);
        for (Row r : rows) {
//            r.getValues().remove(idx);
            r.removeValue(idx);
        }
    }


    public void renameCol(String nameColumn, String newName) {
        for (Column col1 : columns) {
            if (col1.getName().equals(nameColumn)) {
                col1.setName(newName);
            }
        }
    }


    public void saveToFile(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {
            oos.writeObject(this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save table " + filePath, e);
        }
    }


    public static Table readFromFile(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            return (Table) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read table from " + filePath, e);
        }
    }


}
