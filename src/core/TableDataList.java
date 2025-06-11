package core;

import model.Column;
import model.Table;

import java.util.*;

/**
 * Класс-хранилище таблиц в памяти.
 * <p>
 * Содержит методы для создания, получения, загрузки и удаления таблиц.
 */
public class TableDataList {
    private final Map<String, Table> tables = new HashMap<>();
    private final String name;

    /**
     * Конструктор.
     *
     * @param name логическое имя базы данных (используется при отладке и печати)
     */
    public TableDataList(String name) {
        this.name = name;
    }

    /**
     * Проверяет, существует ли таблица с указанным именем.
     *
     * @param tableName имя таблицы
     * @return true, если таблица уже существует; false — иначе
     */
    public boolean hasTable(String tableName) {
        return tables.containsKey(tableName);
    }

    /**
     * Создает новую таблицу в памяти.
     *
     * @param tableName имя создаваемой таблицы
     * @param columns   список колонок для таблицы
     * @throws IllegalArgumentException если таблица с таким именем уже существует
     */
    public void createTable(String tableName, List<Column> columns) {
        if (hasTable(tableName)) {
            throw new IllegalArgumentException("Таблица уже существует: " + tableName);
        }
        tables.put(tableName, new Table(tableName, columns));
    }

    /**
     * Возвращает объект таблицы по имени.
     *
     * @param tableName имя таблицы
     * @return объект Table или null, если таблица не найдена
     */
    public Table getTable(String tableName) {
        return tables.get(tableName);
    }

    /**
     * Возвращает неизменяемое отображение всех таблиц.
     * <p>
     * Для загрузки существующих таблиц из внешнего источника используйте {@link #loadTable}.
     *
     * @return Map имен таблиц в объекты Table
     */
    public Map<String, Table> getTables() {
        return Collections.unmodifiableMap(tables);
    }

    /**
     * Загружает или заменяет таблицу из внешнего источника (например, после десериализации).
     *
     * @param tableName имя таблицы
     * @param table     объект таблицы для загрузки
     */
    public void loadTable(String tableName, Table table) {
        tables.put(tableName, table);
    }

    /**
     * Удаляет таблицу из хранилища.
     *
     * @param tableName имя удаляемой таблицы
     * @return true, если таблица существовала и была удалена; false — если таблицы не было
     */
    public boolean deleteTable(String tableName) {
        return tables.remove(tableName) != null;
    }

    /**
     * Удаляет все таблицы из хранилища.
     */
    public void clear() {
        tables.clear();
    }

}
