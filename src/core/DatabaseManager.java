package core;

import io.DatabaseIO;
import model.Table;

import java.io.*;
import java.util.*;

/**
 * Менеджер работы с базой данных.
 *
 * <ul>
 *   <li>При запуске пытается загрузить полный бэкап из storageDir/my-database.db.</li>
 *   <li>Затем подгружает отдельные файлы таблиц (*.db), если они есть.</li>
 *   <li>После каждой модификации сохраняет изменённую таблицу и обновлённый бэкап.</li>
 *   <li>Хранит последние активные таблицы для GUI.</li>
 * </ul>
 */
public class DatabaseManager {
    private final TableDataList db;
    private final DatabaseEngine engine;
    private final String storageDir;

    /**
     * Конструктор.
     *
     * @param storageDir путь к директории для хранения файлов базы данных
     */
    public DatabaseManager(String storageDir) {
        this.storageDir = storageDir;
//        new File(storageDir).mkdirs();
        this.db = new TableDataList("default");
        this.engine = new DatabaseEngine(db, this::onTableModified, storageDir);
        loadExistingTables();
    }

    /**
     * Возвращает доступ к внутреннему объекту базы данных.
     *
     * @return структура в памяти со всеми таблицами
     */
    public TableDataList getDatabase() {
        return db;
    }

    /**
     * Получить список имён всех таблиц в базе.
     *
     * @return список имён таблиц
     */
    public List<String> getAllTableNames() {
        return new ArrayList<>(db.getTables().keySet());
    }

    /**
     * Загрузка существующих таблиц из файлов.
     *
     * <ol>
     *   <li>Пытается загрузить полный бэкап my-database.db.</li>
     *   <li>Подгружает отдельные файлы <table>.db, если они есть.</li>
     * </ol>
     */
    @SuppressWarnings("unchecked")
    private void loadExistingTables() {
        // 1) Загрузить бэкап всей БД, если есть
        File backup = new File(storageDir, "my-database.db");
        if (backup.isFile()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(backup))) {
                Object obj = ois.readObject();
                if (obj instanceof Map) {
                    Map<String, Table> all = (Map<String, Table>) obj;
                    for (Map.Entry<String, Table> e : all.entrySet()) {
                        db.loadTable(e.getKey(), e.getValue());
                    }
                    System.out.println("Загружен бэкап из my-database.db");
                }
            } catch (Exception e) {
                System.err.println("Не удалось загрузить бэкап БД: " + e.getMessage());
            }
        }

        // 2) Подгрузить одиночные файлы <table>.db (кроме бэкапа)
        File dir = new File(storageDir);
        File[] files = dir.listFiles((_, name) ->
                name.endsWith(".db") && !name.equals("my-database.db")
        );
        if (files == null) return;

        for (File f : files) {
            String fn = f.getName();                 // e.g. "users.db"
            String tableName = fn.substring(0, fn.length() - 3);
            String fullPath  = f.getAbsolutePath();
            try {
                Table tbl = Table.readFromFile(fullPath);
                db.loadTable(tableName, tbl);
                System.out.println("Загружена таблица " + tableName + " с диска");
            } catch (RuntimeException e) {
                System.err.println("Ошибка при загрузке таблицы " + tableName + ": " + e.getMessage());
            }
        }
    }

    /**
     * Callback при изменении таблицы.
     * <p>
     * Сохраняет саму таблицу в <table>.db и обновляет полный бэкап my-database.db.
     *
     * @param tableName имя изменённой таблицы
     */
    private void onTableModified(String tableName) {
        // 1) сохранить саму таблицу в отдельный файл
        Table tbl = db.getTable(tableName);
        if (tbl != null) {
            String tblPath = storageDir + File.separator + tableName + ".db";
            tbl.saveToFile(tblPath);
            System.out.println("Сохранена таблица " + tableName + " в файл");
        }
        // 2) обновить полный бэкап всей базы
        String backupPath = storageDir + File.separator + "my-database.db";
        DatabaseIO.exportAllTables(db, backupPath);
        System.out.println("Обновлён бэкап БД: my-database.db");
    }

    /**
     * Выполнить произвольный SQL-запрос.
     *
     * @param sql текст SQL
     */
    public void executeSql(String sql) {
        engine.execute(sql);
    }

    /**
     * Импортировать все таблицы из указанного файла бэкапа.
     *
     * @param fileName путь к файлу-источнику
     */
    public void importAll(String fileName) {
        DatabaseIO.importAllTables(db, fileName);
        // После импорта перезаписать свой бэкап
        String backupPath = storageDir + File.separator + "my-database.db";
        DatabaseIO.exportAllTables(db, backupPath);
    }

    /**
     * Экспортировать все таблицы в указанный файл бэкапа.
     *
     * @param fileName путь к файлу-назначению
     */
    public void exportAll(String fileName) {
        DatabaseIO.exportAllTables(db, fileName);
    }

    /**
     * Импортировать данные в одну таблицу из CSV.
     *
     * @param tableName имя целевой таблицы
     * @param fileName  путь к CSV-файлу
     */
    public void importCsv(String tableName, String fileName) {
        DatabaseIO.importTableFromCsv(db, tableName, fileName);
        // и сразу сохранить бэкап
        String backupPath = storageDir + File.separator + "my-database.db";
        DatabaseIO.exportAllTables(db, backupPath);
    }

    /**
     * Экспортировать одну таблицу в CSV.
     *
     * @param tableName имя таблицы
     * @param fileName  путь к CSV-файлу
     */
    public void exportCsv(String tableName, String fileName) {
        DatabaseIO.exportTableToCsv(db, tableName, fileName);
    }


}
