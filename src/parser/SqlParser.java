package parser;

import java.util.*;
import java.util.regex.*;

/**
 * Простой SQL-парсер, поддерживает следующие команды:
 * <ul>
 *   <li>CREATE TABLE</li>
 *   <li>DROP TABLE</li>
 *   <li>INSERT INTO (многократные значения)</li>
 *   <li>DELETE FROM ... [WHERE]</li>
 *   <li>SELECT ... FROM ... [WHERE] [SORT]</li>
 *   <li>ALTER TABLE ... ADD COLUMN</li>
 *   <li>ALTER TABLE ... RENAME COLUMN</li>
 *   <li>ALTER TABLE ... DROP COLUMN</li>
 * </ul>
 */
public class SqlParser {

    /**
     * Тип SQL-команды.
     */
    public enum Type {
        CREATE, DROP, INSERT, UPDATE, DELETE, SELECT,
        ALTER_ADD, ALTER_RENAME, ALTER_DROP,
        UNKNOWN
    }

    /**
     * Условие WHERE: имя колонки, оператор и строковое значение для сравнения.
     */
    public static class Condition {
        public String column;
        public String operator;
        public String value;

        /**
         * @param c имя колонки
         * @param o оператор сравнения (=, !=, >, < и т.п.)
         * @param v значение для сравнения (без кавычек)
         */
        public Condition(String c, String o, String v) {
            column = c;
            operator = o;
            value = v;
        }
    }

    /**
     * Структура для представления разобранной SQL-команды.
     */
    public static class Command {
        public Type type = Type.UNKNOWN;
        public String table;

        // Параметры для CREATE и ALTER ADD
        public List<String> createColNames = new ArrayList<>();
        public List<String> createColTypes = new ArrayList<>();
        public List<Boolean> createColUnique = new ArrayList<>();
        public List<Boolean> createColNotNull = new ArrayList<>();

        // Параметры для ALTER RENAME и DROP
        public List<String> renameColOld = new ArrayList<>();
        public List<String> renameColNew = new ArrayList<>();

        // Для INSERT (множество строк)
        public List<List<String>> multiValues = new ArrayList<>();

        // Для DELETE и SELECT
        public List<Condition> where = new ArrayList<>();

        // Для SELECT
        public boolean selectAll;
        public List<String> selectCols = new ArrayList<>();
        public String sortColumn;

        @Override
        public String toString() {
            return "Command{" +
                    "type=" + type +
                    ", table='" + table + '\'' +
                    ", createCols=" + createColNames +
                    ", createTypes=" + createColTypes +
                    ", unique=" + createColUnique +
                    ", notNull=" + createColNotNull +
                    ", multiValues=" + multiValues +
                    ", where=" + where +
                    ", selectAll=" + selectAll +
                    ", selectCols=" + selectCols +
                    ", sort='" + sortColumn + '\'' +
                    '}';
        }
    }

    // Регулярные выражения для распознавания SQL
    private static final Pattern CREATE_REGEX = Pattern.compile(
            "CREATE\\s+TABLE\\s+(\\w+)\\s*\\((.+?)\\)\\s*;?",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern DROP_REGEX = Pattern.compile(
            "DROP\\s+TABLE\\s+(\\w+)\\s*;?",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern INSERT_REGEX = Pattern.compile(
            "INSERT\\s+INTO\\s+(\\w+)\\s*\\(\\s*(\\([^)]*\\)(?:\\s*,\\s*\\([^)]*\\))*)\\s*\\)\\s*;?",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern DELETE_REGEX = Pattern.compile(
            "DELETE\\s+FROM\\s+(\\w+)(?:\\s+WHERE\\s+(.+?))?\\s*;?",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern SELECT_REGEX = Pattern.compile(
            "SELECT\\s+(.+?)\\s+FROM\\s+(\\w+)(?:\\s+WHERE\\s+(.+?))?(?:\\s+SORT\\s+(\\w+))?\\s*;?",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern ALTER_ADD_REGEX = Pattern.compile(
            "ALTER\\s+TABLE\\s+(\\w+)\\s+ADD\\s+COLUMN\\s*\\((.+?)\\)\\s*;?",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern ALTER_RENAME_REGEX = Pattern.compile(
            "ALTER\\s+TABLE\\s+(\\w+)\\s+RENAME\\s+COLUMN\\s*\\((.+?)\\)\\s*;?",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern ALTER_DROP_REGEX = Pattern.compile(
            "ALTER\\s+TABLE\\s+(\\w+)\\s+DROP\\s+COLUMN\\s+(\\w+)\\s*;?",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Разбирает строку SQL и возвращает объект команды.
     *
     * @param sql текст SQL-запроса
     * @return объект Command с заполненными полями
     */
    public Command parse(String sql) {
        String s = sql.trim();
        Matcher m;
        Command cmd = new Command();

        // CREATE TABLE
        if ((m = CREATE_REGEX.matcher(s)).matches()) {
            cmd.type = Type.CREATE;
            cmd.table = m.group(1);
            String colsDef = m.group(2);
            for (String part : colsDef.split("\\s*,\\s*")) {
                String[] tok = part.trim().split("\\s+");
                cmd.createColNames.add(tok[0]);
                cmd.createColTypes.add(tok[1].toUpperCase());
                cmd.createColUnique.add(Arrays.asList(tok).contains("unique"));
                cmd.createColNotNull.add(Arrays.asList(tok).contains("not-null"));
            }
            return cmd;
        }

        // DROP TABLE
        if ((m = DROP_REGEX.matcher(s)).matches()) {
            cmd.type = Type.DROP;
            cmd.table = m.group(1);
            return cmd;
        }

        // INSERT INTO ... ( ... ), ( ... ), ...
        if ((m = INSERT_REGEX.matcher(s)).matches()) {
            cmd.type = Type.INSERT;
            cmd.table = m.group(1);
            String rows = m.group(2);
            Matcher rowM = Pattern.compile("\\(([^)]*)\\)").matcher(rows);
            while (rowM.find()) {
                cmd.multiValues.add(splitTopLevel(rowM.group(1)));
            }
            return cmd;
        }

        // DELETE FROM ... [WHERE ...]
        if ((m = DELETE_REGEX.matcher(s)).matches()) {
            cmd.type = Type.DELETE;
            cmd.table = m.group(1);
            if (m.group(2) != null) {
                cmd.where = parseConditions(m.group(2));
            }
            return cmd;
        }

        // SELECT ... FROM ... [WHERE ...] [SORT ...]
        if ((m = SELECT_REGEX.matcher(s)).matches()) {
            cmd.type = Type.SELECT;
            String sel = m.group(1).trim();
            cmd.selectAll = sel.equals("*");
            if (!cmd.selectAll) {
                cmd.selectCols = Arrays.asList(sel.split("\\s*,\\s*"));
            }
            cmd.table = m.group(2);
            if (m.group(3) != null) {
                cmd.where = parseConditions(m.group(3));
            }
            cmd.sortColumn = m.group(4);
            return cmd;
        }

        // ALTER TABLE ... ADD COLUMN (...)
        if ((m = ALTER_ADD_REGEX.matcher(s)).matches()) {
            cmd.type = Type.ALTER_ADD;
            cmd.table = m.group(1);
            for (String part : m.group(2).split("\\s*,\\s*")) {
                String[] tok = part.trim().split("\\s+");
                cmd.createColNames.add(tok[0]);
                cmd.createColTypes.add(tok[1].toUpperCase());
                cmd.createColUnique.add(Arrays.asList(tok).contains("unique"));
                cmd.createColNotNull.add(Arrays.asList(tok).contains("not-null"));
            }
            return cmd;
        }

        // ALTER TABLE ... RENAME COLUMN (...)
        if ((m = ALTER_RENAME_REGEX.matcher(s)).matches()) {
            cmd.type = Type.ALTER_RENAME;
            cmd.table = m.group(1);
            for (String part : m.group(2).split("\\s*,\\s*")) {
                String[] tok = part.trim().split("\\s+");
                cmd.renameColOld.add(tok[0]);
                cmd.renameColNew.add(tok[1]);
            }
            return cmd;
        }

        // ALTER TABLE ... DROP COLUMN ...
        if ((m = ALTER_DROP_REGEX.matcher(s)).matches()) {
            cmd.type = Type.ALTER_DROP;
            cmd.table = m.group(1);
            cmd.renameColOld.add(m.group(2)); // единственное имя колонки для удаления
            return cmd;
        }

        // Нераспознанная команда
        return cmd;
    }

    /**
     * Разбивает строку аргументов INSERT на отдельные значения,
     * учитывая вложенные списки в квадратных скобках.
     *
     * @param s содержимое скобок без внешних круглых скобок
     * @return список токенов верхнего уровня
     */
    private List<String> splitTopLevel(String s) {
        List<String> out = new ArrayList<>();
        int depth = 0;
        StringBuilder cur = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '[') depth++;
            if (c == ']') depth--;
            if (c == ',' && depth == 0) {
                out.add(cur.toString().trim());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        if (!cur.isEmpty()) {
            out.add(cur.toString().trim());
        }
        return out;
    }

    /**
     * Парсит строку условий WHERE, разделённых по AND.
     *
     * @param where часть запроса после WHERE
     * @return список объектов Condition
     */
    private List<Condition> parseConditions(String where) {
        List<Condition> res = new ArrayList<>();
        for (String part : where.split("\\s+AND\\s+")) {
            Matcher cm = Pattern.compile("(\\w+)\\s*(=|!=|<>|>=|<=|>|<)\\s*(.+)",
                    Pattern.CASE_INSENSITIVE).matcher(part.trim());
            if (cm.matches()) {
                String val = cm.group(3).replaceAll("^['\"]|['\"]$", "");
                res.add(new Condition(cm.group(1), cm.group(2), val));
            }
        }
        return res;
    }
}
