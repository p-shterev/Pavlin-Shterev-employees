package pkg;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class MyDataTableModel extends DefaultTableModel {
    private ArrayList<String[]> values;
    private String[] columns;

    public MyDataTableModel(ArrayList<String[]> values, String[] columns){
        this.values = values;
        this.columns = columns;

    }
    public MyDataTableModel(){}

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public void setValues(ArrayList<String[]> values) {
        this.values = values;
    }

    @Override
    public Object getValueAt(int row, int column) {
       return values.get(row)[column];
    }

    @Override
    public String getColumnName(int column) {
       return columns[column];
    }
    @Override
    public int getColumnCount() {
        if(columns == null)
            return 0;
        return columns.length;
    }

    @Override
    public int getRowCount() {
        if(values == null)
            return 0;
        return values.size();
    }

}
