package core;

import model.Column;
import model.Row;
import model.Table;
import parser.SqlParser;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Движок для выполнения распарсенных SQL-команд над базой данных в памяти.
 * Вызывает соответствующие методы обработки и уведомляет о модификации таблицы.
 */
public class DatabaseEngine {
    private final TableDataList db;
    private final SqlParser parser = new SqlParser();
    private final Consumer<String> onTableModified;
    private final String storageDir;

    /**
     * Конструктор.
     *
     * @param db               — in-memory база данных (список таблиц)
     * @param onTableModified  — callback(tableName), вызывается после любой модификации таблицы
     * @param storageDir       — каталог для хранения файлов .db
     */
    public DatabaseEngine(TableDataList db, Consumer<String> onTableModified, String storageDir) {
        this.db = db;
        this.onTableModified = onTableModified;
        this.storageDir = storageDir;
    }

    /**
     * Основной метод: парсит SQL и маршрутизирует команду к нужному обработчику.
     *
     * @param sql — текст SQL-запроса
     */
    public void execute(String sql) {
        SqlParser.Command cmd = parser.parse(sql);
        switch (cmd.type) {
            case CREATE:       handleCreate(cmd);       break;
            case DROP:         handleDrop(cmd);         break;
            case INSERT:       handleInsert(cmd);       break;
            case DELETE:       handleDelete(cmd);       break;
            case SELECT:       handleSelect(cmd);       break;
            case ALTER_ADD:    handleAlterAdd(cmd);     break;
            case ALTER_RENAME: handleAlterRename(cmd);  break;
            case ALTER_DROP:   handleAlterDrop(cmd);    break;
            default:
                throw new IllegalArgumentException("Неподдерживаемый SQL: " + sql);
        }
    }

    /**
     * Обработка команды ALTER TABLE DROP COLUMN.
     */
    private void handleAlterDrop(SqlParser.Command c) {
        Table tbl = db.getTable(c.table);
        if (tbl == null) {
            throw new IllegalArgumentException("Нет таблицы: " + c.table);
        }
        String colToDrop = c.renameColOld.getFirst();
        tbl.deleteColumn(colToDrop);
        onTableModified.accept(c.table);
    }

    /**
     * Обработка команды CREATE TABLE.
     */
    private void handleCreate(SqlParser.Command c) {
        if (db.getTable(c.table) != null)
            throw new IllegalArgumentException("Таблица уже существует: " + c.table);

        List<Column> cols = new ArrayList<>();
        for (int i = 0; i < c.createColNames.size(); i++) {
            cols.add(new Column(
                    c.createColNames.get(i),
                    c.createColTypes.get(i),
                    c.createColNotNull.get(i),
                    c.createColUnique.get(i)
            ));
        }
        db.createTable(c.table, cols);
        onTableModified.accept(c.table);
    }

    /**
     * Обработка команды DROP TABLE.
     */
    private void handleDrop(SqlParser.Command c) {
        db.deleteTable(c.table);
        File f = new File(storageDir, c.table + ".db");
        if (f.exists() && !f.delete()) {
            throw new RuntimeException("Не удалось удалить файл " + f.getAbsolutePath());
        }
    }

    /**
     * Обработка команды INSERT INTO.
     */
    private void handleInsert(SqlParser.Command c) {
        Table tbl = db.getTable(c.table);
        if (tbl == null) {
            throw new IllegalArgumentException("Нет таблицы: " + c.table);
        }
        for (List<String> rawRow : c.multiValues) {
            List<Object> vals = new ArrayList<>();
            for (String tok : rawRow) {
                vals.add(parseValue(tok.trim()));
            }
            tbl.addRow(new Row(vals));
        }
        onTableModified.accept(c.table);
    }

    /**
     * Обработка команды DELETE FROM.
     */
    private void handleDelete(SqlParser.Command c) {
        Table tbl = db.getTable(c.table);
        if (tbl == null) {
            throw new IllegalArgumentException("Нет таблицы: " + c.table);
        }
        tbl.getRows().removeIf(r -> matchesAll(r, tbl, c.where));
        onTableModified.accept(c.table);
    }

    /**
     * Обработка команды SELECT.
     * Печатает результат в консоль. GUI использует состояние базы напрямую.
     */
    private void handleSelect(SqlParser.Command c) {
        Table tbl = db.getTable(c.table);
        if (tbl == null) {
            throw new IllegalArgumentException("Нет таблицы: " + c.table);
        }
        List<Row> out = new ArrayList<>();
        for (Row r : tbl.getRows()) {
            if (matchesAll(r, tbl, c.where)) {
                out.add(r);
            }
        }
        if (c.sortColumn != null) {
            int idx = findColumnIndex(tbl.getColumns(), c.sortColumn);
            if (idx >= 0) {
                out.sort(Comparator.comparing(r -> (Comparable) r.getValues().get(idx)));
            }
        }
        for (Row r : out) {
            if (c.selectAll) {
                System.out.println(r.getValues());
            } else {
                List<Object> row = new ArrayList<>();
                for (String col : c.selectCols) {
                    int idx = findColumnIndex(tbl.getColumns(), col);
                    if (idx >= 0) {
                        row.add(r.getValues().get(idx));
                    }
                }
                System.out.println(row);
            }
        }
    }

    /**
     * Обработка команды ALTER TABLE ADD COLUMN.
     */
    private void handleAlterAdd(SqlParser.Command c) {
        Table tbl = db.getTable(c.table);
        if (tbl == null) {
            throw new IllegalArgumentException("Нет таблицы: " + c.table);
        }
        for (int i = 0; i < c.createColNames.size(); i++) {
            tbl.addColumn(new Column(
                    c.createColNames.get(i),
                    c.createColTypes.get(i),
                    c.createColNotNull.get(i),
                    c.createColUnique.get(i)
            ));
        }
        onTableModified.accept(c.table);
    }

    /**
     * Обработка команды ALTER TABLE RENAME COLUMN.
     */
    private void handleAlterRename(SqlParser.Command c) {
        Table tbl = db.getTable(c.table);
        if (tbl == null) {
            throw new IllegalArgumentException("Нет таблицы: " + c.table);
        }
        for (int i = 0; i < c.renameColOld.size(); i++) {
            tbl.renameCol(c.renameColOld.get(i), c.renameColNew.get(i));
        }
        onTableModified.accept(c.table);
    }

    /**
     * Проверяет, удовлетворяет ли строка всем условиям WHERE.
     */
    private boolean matchesAll(Row r, Table tbl, List<SqlParser.Condition> where) {
        for (SqlParser.Condition cond : where) {
            int idx = findColumnIndex(tbl.getColumns(), cond.column);
            if (idx < 0) return false;
            if (!compare(r.getValues().get(idx), cond.operator, cond.value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Сравнивает значение v с raw по оператору op.
     */
    private boolean compare(Object v, String op, String raw) {
        if (v instanceof Number) {
            double a = ((Number) v).doubleValue();
            double b = Double.parseDouble(raw);
            switch (op) {
                case "=":  return a == b;
                case "!=": case "<>": return a != b;
                case ">":  return a > b;
                case "<":  return a < b;
                case ">=": return a >= b;
                case "<=": return a <= b;
            }
        } else {
            String s = v.toString();
            switch (op) {
                case "=":  return s.equals(raw);
                case "!=": case "<>": return !s.equals(raw);
            }
        }
        return false;
    }

    /**
     * Находит индекс колонки по имени (регистр игнорируется).
     *
     * @return индекс или -1, если не найдено
     */
    private int findColumnIndex(List<Column> cols, String name) {
        for (int i = 0; i < cols.size(); i++) {
            if (cols.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Парсит текстовое представление значения в Java-объект.
     * Поддерживает списки, булевы, целые, дробные и строки.
     */
    private Object parseValue(String raw) {
        if (raw.startsWith("[") && raw.endsWith("]")) {
            List<String> list = new ArrayList<>();
            Matcher m = Pattern.compile("\"([^\"]*)\"").matcher(raw);
            while (m.find()) {
                list.add(m.group(1));
            }
            return list;
        }
        if (raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(raw);
        }
        if (raw.matches("\\d+")) {
            return Integer.parseInt(raw);
        }
        if (raw.matches("\\d+\\.\\d+")) {
            return Double.parseDouble(raw);
        }
        return raw.replaceAll("^['\"]|['\"]$", "");
    }
}
