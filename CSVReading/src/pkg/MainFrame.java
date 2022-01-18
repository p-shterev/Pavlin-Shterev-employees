package pkg;

import com.opencsv.CSVReader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class MainFrame extends JFrame {
    private JTable jTable;
    private JButton openCSV;
    private JLabel jLabel;
    private JFileChooser fileChooser;
    private JToolBar toolBar;
    private JComboBox jComboBox;
    private String maxTimeTwoWorkers="";
    private String[] dateFormat = {
            "yyyy-MM-dd", "yyyy/MM/dd", "yyyy.MM.dd",
            "yyyy-dd-MM", "yyyy/dd/MM", "yyyy.dd.MM",
            "MM-dd-yyyy", "MM/dd/yyyy", "MM.dd.yyyy",
            "MMM-dd-yyyy", "MMM/dd/yyyy", "MMM.dd.yyyy",
            "MMMM-dd-yyyy", "MMMM/dd/yyyy", "MMMM.dd.yyyy"
    };


    MyDataTableModel dataTableModel;
    public MainFrame() throws HeadlessException{
        this.setVisible(true);
        this.setBounds(100,100,900,600);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        init();
        System.out.println("MainFrame.<init>(1)");
    }

    private void init(){
        jTable = new JTable();
        this.add(new JScrollPane(jTable), BorderLayout.CENTER);

        openCSV = new JButton("Choose a file to read");
        setActions();
        fileChooser = new JFileChooser();
        toolBar = new JToolBar();
        toolBar.add(openCSV);

        jComboBox = new JComboBox(dateFormat);
        toolBar.add(jComboBox);
        jComboBox.setMaximumSize(new Dimension(200,100));
        this.add(toolBar, BorderLayout.NORTH);

        dataTableModel = new MyDataTableModel();
        jTable.setModel(dataTableModel);
        dataTableModel.fireTableStructureChanged();
        updateWindow();

    }
    private void setActions(){

        openCSV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              int i = fileChooser.showOpenDialog(MainFrame.this);
              if (i == JFileChooser.APPROVE_OPTION){
                  File file = fileChooser.getSelectedFile();
                  ArrayList<String[]> all = readCSV(file);
                  String[] cols = all.get(0);
                  all.remove(0);
                  MyDataTableModel dataTableModel = new MyDataTableModel(all, cols);
                  jTable.setModel(dataTableModel);
                  try {
                      findMaxTimeTwoWorkers(all);
                  } catch (ParseException ex) {
                      ex.printStackTrace();
                  }
                  dataTableModel.fireTableDataChanged();
              }
            }
        });

    }


    private ArrayList<String[]> readCSV(File file){
        ArrayList<String[]> data = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(file);
            CSVReader csvReader = new CSVReader(fileReader);
            Iterator<String[]> rows = csvReader.iterator();
            while(rows.hasNext()){
                String[] cols = rows.next();
                data.add(cols);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return new ArrayList<>();
        }
        return data;
    }

    public String takeCurrentDate(){
        String selected_text = (String) jComboBox.getItemAt(jComboBox.getSelectedIndex());
        DateFormat dateFormat = new SimpleDateFormat(selected_text);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public long stringToDate(String dateString) throws ParseException {
        String selected_text = (String) jComboBox.getItemAt(jComboBox.getSelectedIndex());

        Date date = new SimpleDateFormat(selected_text).parse(dateString);
        return date.getTime();
    }

    public void updateWindow(){
        this.setVisible(false);
        this.setVisible(true);
    }

    public void findMaxTimeTwoWorkers(ArrayList<String[]> employees) throws ParseException {
        int empID = 0;
        int project = 1;
        int DateFrom = 2;
        int DateTo = 3;
        long maxTime = 0;
        String maxProject = "";
        String maxEmp1 ="";
        String maxEmp2 = "";
        for(int i = 0; i < employees.size()-1; i++){
            for (int j = i+1; j<employees.size();j++){
                String[] emp1 = employees.get(i);
                String[] emp2 = employees.get(j);
                if(emp1[project].equals(emp2[project])){
                    System.out.println(emp1[empID]+" "+emp2[empID]);
                    if(emp1[DateTo].contains("NULL")) {
                        emp1[DateTo] = takeCurrentDate();
                    }
                    if(emp2[DateTo].contains("NULL")) {
                        emp2[DateTo] = takeCurrentDate();
                    }
                    if(stringToDate(emp2[DateTo]) - stringToDate(emp1[DateFrom]) < 0 || stringToDate(emp1[DateTo]) - stringToDate(emp2[DateFrom]) < 0){
                        continue;
                    }
                    long leftBorder= Math.max(stringToDate(emp1[DateFrom]), stringToDate(emp2[DateFrom]));
                    long rightBorder = Math.min(stringToDate(emp1[DateTo]), stringToDate(emp2[DateTo]));

                    long twoWorkersTime = rightBorder - leftBorder;

                    if(maxTime < twoWorkersTime){
                        maxTime = twoWorkersTime;
                        maxProject = emp1[project];
                        maxEmp1 = emp1[empID];
                        maxEmp2 = emp2[empID];
                    }

                }


            }

        }
        maxTime = (maxTime / (1000 * 60 * 60 * 24))+1;
        System.out.println(maxEmp1+ " "+ maxEmp2+ " "+ maxProject+" "+ maxTime);
        maxTimeTwoWorkers = "  result "+maxEmp1+ ", "+ maxEmp2+ ", "+ maxProject+", "+ maxTime;
        jLabel = new JLabel(maxTimeTwoWorkers);
        toolBar.add(jLabel);
        updateWindow();

    }


}
