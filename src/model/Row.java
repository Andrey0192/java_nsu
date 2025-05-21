package model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Row implements Serializable {

    private final List<Object> values;

    public Row(List<Object> values) {
        this.values = new ArrayList<>(values);
    }

    public List<Object> getValues() {
        return Collections.unmodifiableList(values);
    }
    public void removeValue(int rowIndex) {
        values.remove(rowIndex);

    }
    public void addValue(Object value) {
        values.add(value);
    }
}

