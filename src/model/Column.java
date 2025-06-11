package model;
import java.io.Serializable;
import java.util.Objects;

public class Column implements Serializable {

    private  String name;
    private final String type;
    private final boolean notNull;
    private final boolean unique;

    public Column(String name, String type, boolean notNull, boolean unique) {
        this.name    = Objects.requireNonNull(name);
        this.type    = Objects.requireNonNull(type);
        this.notNull = notNull;
        this.unique  = unique;
    }

    public String getName()     { return name; }
    public void setName(String name) { this.name =  name; }
    public String getType()     { return type; }
    public boolean isNotNull()  { return notNull; }
    public boolean isUnique()   { return unique; }
}

