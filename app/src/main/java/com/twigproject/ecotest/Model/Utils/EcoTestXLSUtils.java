package com.twigproject.ecotest.Model.Utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;


import com.twigproject.ecotest.Model.EcoTest;
import com.twigproject.ecotest.Model.TestSession;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Creates and saves current test-session report to external storage in spreed sheet format
 * @author Max Ermakov
 * @see org.apache.poi.hssf.usermodel.HSSFWorkbook
 */
public class EcoTestXLSUtils {
    /**
     * Current app context
     */
    private Context mContext;
    /**
     * XLS report file name
     */
    private String mFilename;

    /**
     * Creates utility object for creating and saving session xls reports
     * @param context app context
     * @param filename XLS report file name
     */
    public EcoTestXLSUtils(Context context, String filename) {
        mContext = context;
        mFilename = filename;
    }

    /**
     * Storing session test-list to workbook, formatting & styling it
     * @param wb spreed sheet workbook
     * @param tests  session test-list
     */
    private void makeReport(HSSFWorkbook wb,ArrayList<EcoTest> tests){
        HSSFSheet sheet = wb.createSheet(TestSession.get(mContext).getEquipmentType());
        sheet.setDefaultColumnWidth(21);
        Cell c = null;
        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        int i=1;
        Row row=sheet.createRow(0);
        for (EcoTest test:tests){                         //making header for every working equipment unit
            if (test.isAtWork()) {
                c=row.createCell(i);
                c.setCellValue(test.getEquipmentType()
                        +" #"+test.getEquipmentNumber());
                c.setCellStyle(cs);
                i++;
            }
        }
                                                          //making left side table column with index names
        row=sheet.createRow(1);
        c=row.createCell(0);
        c.setCellValue("Start time:");
        row=sheet.createRow(2);
        c=row.createCell(0);
        c.setCellValue("End time:");
        row=sheet.createRow(3);
        c=row.createCell(0);
        c.setCellValue("O2,%:");
        row=sheet.createRow(4);
        c=row.createCell(0);
        c.setCellValue("CO,ppm:");
        row=sheet.createRow(5);
        c=row.createCell(0);
        c.setCellValue("NOx,ppm:");
        row=sheet.createRow(6);
        c=row.createCell(0);
        c.setCellValue("Temperature:");

        i=1;                                              //filling table with tests values
        DateFormat format=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        for(EcoTest test:tests){
            if(test.isAtWork()){
                row=sheet.getRow(1);
                c=row.createCell(i);
                c.setCellValue(format.format(test.getStartTime().toDate()));
                row=sheet.getRow(2);
                c=row.createCell(i);
                c.setCellValue(format.format(test.getEndTime().toDate()));
                row=sheet.getRow(3);
                c=row.createCell(i);
                c.setCellValue(test.getO2()!=null?test.getO2():0);
                row=sheet.getRow(4);
                c=row.createCell(i);
                c.setCellValue(test.getCO()!=null?test.getCO():0);
                row=sheet.getRow(5);
                c=row.createCell(i);
                c.setCellValue(test.getNOx()!=null?test.getNOx():0);
                row=sheet.getRow(6);
                c=row.createCell(i);
                c.setCellValue(test.getTemperature()!=null?test.getTemperature():0);
                i++;
            }
        }
    }

    /**
     * Saves filled workbook as xls file to external storage
     * @param tests - current session test - list
     * @return URI of report file
     */
    public Uri saveXLSFile(ArrayList<EcoTest>tests){
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + "/ecotest/com/twigproject/xlsreports");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File report = new File(directory,mFilename);
        FileOutputStream out = null;
        HSSFWorkbook wb=new HSSFWorkbook();
        makeReport(wb, tests);
        try {
            out= new FileOutputStream(report);
            wb.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Uri.fromFile(report);
    }

    /**
     * Checks is external storage read only
     * @return boolean is is external storage read only
     */
    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }
    /**
     * Checks is external storage available
     * @return boolean is is external storage available
     */
    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
}
