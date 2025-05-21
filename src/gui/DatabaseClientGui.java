// DatabaseClientGui.java
package gui;
import core.DatabaseManager;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.*;
import model.Column;
import model.Row;
import model.Table;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

public class DatabaseClientGui extends Application {
    private DatabaseManager mgr;
    private ListView<String> tablesList;
    private TableView<Column> schemaTable;
    private TableView<ObservableList<String>> dataTable;
    private TextArea sqlArea;
    private Label statusBar;
    private String currentTableName;


    @Override
    public void start(Stage stage) {
        mgr = new DatabaseManager("storage");
        // Left pane: list of tables
        tablesList = new ListView<>();
        refreshTables();
        tablesList.getSelectionModel().selectedItemProperty().addListener((_,_,sel) -> {
            if (sel != null) showTable(sel);
        });

        // SQL editor
        sqlArea = new TextArea();
        sqlArea.setPromptText("Enter SQL...");
        sqlArea.setPrefRowCount(3);
        Button execSql = new Button("Execute SQL");
        execSql.setOnAction(_ -> executeSql());

        HBox sqlBox = new HBox(5, new Label("SQL:"), sqlArea, execSql);
        HBox.setHgrow(sqlArea, Priority.ALWAYS);
        sqlBox.setPadding(new Insets(5));

        // Schema tab
        schemaTable = new TableView<>();
        schemaTable.setPlaceholder(new Label("No schema"));
        schemaTable.setEditable(false);
        Button addCol    = new Button("Add Column");
        Button editCol   = new Button("Rename Column");
        Button deleteCol = new Button("Drop Column");
        addCol.setOnAction(_ -> onAddColumn());
        editCol.setOnAction(_ -> onRenameColumn());
        deleteCol.setOnAction(_ -> onDropColumn());
        HBox schemaBtns = new HBox(5, addCol, editCol, deleteCol);
        VBox schemaPane = new VBox(5, schemaTable, schemaBtns);
        schemaPane.setPadding(new Insets(5));

        // Data tab
        dataTable = new TableView<>();
        dataTable.setEditable(true);
        dataTable.setPlaceholder(new Label("No data"));
        VBox dataPane = new VBox(dataTable);
        dataPane.setPadding(new Insets(5));

        TabPane tabs = new TabPane(
                new Tab("Schema", schemaPane),
                new Tab("Data", dataPane)
        );
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Import/Export buttons
        Button impDB = new Button("Import DB");
        Button expDB = new Button("Export DB");
        Button impCSV = new Button("Import CSV");
        Button expCSV = new Button("Export CSV");
        impDB.setOnAction(_ -> chooseAndImportDB());
        expDB.setOnAction(_ -> chooseAndExportDB());
        impCSV.setOnAction(_ -> chooseAndImportCSV());
        expCSV.setOnAction(_ -> chooseAndExportCSV());
        HBox ioBox = new HBox(5, impDB, expDB, impCSV, expCSV);
        ioBox.setPadding(new Insets(5));

        statusBar = new Label("Ready");

        BorderPane root = new BorderPane();
        root.setLeft(new VBox(new Label("Tables"), tablesList));
        root.setTop(new VBox(sqlBox, ioBox));
        root.setCenter(tabs);
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("Database Client");
        stage.setScene(scene);
        stage.show();
    }

    private void executeSql() {
        String sql = sqlArea.getText().trim();
        if (sql.isEmpty()) {
            statusBar.setText("SQL is empty");
            return;
        }
        try {
            mgr.executeSql(sql);
            statusBar.setText("Executed SQL");
            refreshTables();
            if (sql.toUpperCase().startsWith("SELECT")) {
                showTable(currentTableName);
            }
        } catch (Exception ex) {
            statusBar.setText("Error: " + ex.getMessage());
        }
    }

    private void refreshTables() {
        tablesList.setItems(FXCollections.observableArrayList(mgr.getAllTableNames()));
    }

    private void showTable(String name) {
        // Schema
        Table tbl = mgr.getDatabase().getTable(name);
        setCurrentTableName(name);
        if (tbl == null) {
            statusBar.setText("No such table: " + name);
            return;
        }
        List<Column> cols = tbl.getColumns();
        schemaTable.getColumns().clear();
        schemaTable.getItems().setAll(cols);
        for (Column c : cols) {
            TableColumn<Column,String> col = new TableColumn<>(c.getName());
            col.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getName()));
            schemaTable.getColumns().add(col);
        }

        // Data
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        for (Row r : tbl.getRows()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            r.getValues().forEach(v -> row.add(v==null?"":v.toString()));
            data.add(row);
        }
        dataTable.getColumns().clear();
        dataTable.setItems(data);
        for (int i = 0; i < cols.size(); i++) {
            final int idx = i;
            TableColumn<ObservableList<String>,String> col = new TableColumn<>(cols.get(i).getName());
            if ("date".equalsIgnoreCase(cols.get(i).getType())) {
                col.setCellFactory(_ -> new DatePickerCell());
            } else {
                col.setCellFactory(TextFieldTableCell.forTableColumn());
            }
            col.setOnEditCommit(ev -> {
                String newVal = ev.getNewValue();
                ev.getRowValue().set(idx, newVal);
                // update underlying Row object
                tbl.getRows().get(ev.getTablePosition().getRow()).getValues().set(idx, newVal);
                mgr.executeSql("UPDATE " + name + " SET " + cols.get(idx).getName() +
                        "='" + newVal + "' WHERE /* add condition */;");
                mgr.exportAll("storage"); // or onTableModified callback
                statusBar.setText("Cell updated");
            });
            col.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().get(idx)));
            dataTable.getColumns().add(col);
        }
        statusBar.setText("Showing table: " + name);
    }

    private void onAddColumn() {
        TextInputDialog dlg = new TextInputDialog("colName type");
        dlg.setHeaderText("Enter: <name> <type> [unique] [not-null]");
        dlg.showAndWait().ifPresent(def -> {
            mgr.executeSql("ALTER TABLE " + currentTableName +
                    " ADD COLUMN (" + def + ");");
            showTable(currentTableName);
        });
    }

    private void onRenameColumn() {
        Column c = schemaTable.getSelectionModel().getSelectedItem();
        if (c == null) return;
        TextInputDialog dlg = new TextInputDialog(c.getName() + " newName");
        dlg.setHeaderText("Enter: <oldName> <newName>");
        dlg.showAndWait().ifPresent(def -> {
            String[] tok = def.split("\\s+");
            mgr.executeSql("ALTER TABLE " + currentTableName +
                    " RENAME COLUMN (" + tok[0] + " " + tok[1] + ");");
            showTable(currentTableName);
        });
    }

    private void onDropColumn() {
        Column c = schemaTable.getSelectionModel().getSelectedItem();
        if (c == null) return;
        mgr.executeSql("ALTER TABLE " + currentTableName +
                " DROP COLUMN " + c.getName() + ";");
        showTable(currentTableName);
    }

    private void chooseAndImportDB() {
        File f = chooseFile("Open Database", true);
        if (f != null) {
            mgr.importAll(f.getPath());
            refreshTables();
            statusBar.setText("Imported DB from " + f.getName());
        }
    }

    private void chooseAndExportDB() {
        File f = chooseFile("Save Database", false);
        if (f != null) {
            mgr.exportAll(f.getPath());
            statusBar.setText("Exported DB to " + f.getName());
        }
    }

//    private void chooseAndImportCSV() {
//        File f = chooseFile("Open CSV", true);
//        if (f != null) {
//            mgr.importCsv(currentTableName, f.getPath());
//            showTable(currentTableName);
//            statusBar.setText("Imported CSV from " + f.getName());
//        }
//    }
//
//    private void chooseAndExportCSV() {
//        File f = chooseFile("Save CSV", false);
//        if (f != null) {
//            mgr.exportCsv(currentTableName, f.getPath());
//            statusBar.setText("Exported CSV to " + f.getName());
//        }
//    }

    // Сделайте так:
    private void chooseAndImportCSV() {
        String tbl = tablesList.getSelectionModel().getSelectedItem();
        if (tbl == null) {
            statusBar.setText("No table selected for CSV import");
            return;
        }
        File f = chooseFile("Open CSV", true);
        if (f != null) {
            mgr.importCsv(tbl, f.getPath());
            showTable(tbl);
            statusBar.setText("Imported CSV into " + tbl);
        }
    }

    // Аналогично для экспорта:
    private void chooseAndExportCSV() {
        String tbl = tablesList.getSelectionModel().getSelectedItem();
        if (tbl == null) {
            statusBar.setText("No table selected for CSV export");
            return;
        }
        File f = chooseFile("Save CSV", false);
        if (f != null) {
            mgr.exportCsv(tbl, f.getPath());
            statusBar.setText("Exported CSV from " + tbl);
        }
    }

    private File chooseFile(String title, boolean open) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        return open
                ? fc.showOpenDialog(tablesList.getScene().getWindow())
                : fc.showSaveDialog(tablesList.getScene().getWindow());
    }

    @Override
    public void stop() {
        mgr.exportAll("storage/my-database.db");
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void setCurrentTableName(String currentTableName) {
        this.currentTableName = currentTableName;
    }

    // Cell with DatePicker for 'date' columns
    private static class DatePickerCell extends TableCell<ObservableList<String>,String> {
        private final DatePicker dp = new DatePicker();
        public DatePickerCell() {
            dp.setOnAction(_ -> commitEdit(dp.getValue().toString()));
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
        @Override public void startEdit() {
            super.startEdit();
            dp.setValue(LocalDate.parse(getItem()));
            setGraphic(dp);
        }
        @Override public void cancelEdit() {
            super.cancelEdit();
            setText(getItem());
            setGraphic(null);
        }
        @Override protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else if (isEditing()) {
                dp.setValue(LocalDate.parse(item));
                setGraphic(dp);
            } else {
                setText(item);
                setGraphic(null);
            }
        }
    }
}
