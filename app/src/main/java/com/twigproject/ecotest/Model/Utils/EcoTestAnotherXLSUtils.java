package com.twigproject.ecotest.Model.Utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.twigproject.ecotest.Model.EcoTest;
import com.twigproject.ecotest.R;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by swat_cat on 19.10.2014.
 */
public class EcoTestAnotherXLSUtils {
    private static final int START_ROW_BOILERS=111;
    private static final int START_COLUMN=13;
    static final private String TAG="com.twigproject.ecotest.Model.AnotherXLSUtils";
    private  static final String boilers_template_filename="eco_report_template.xls";
    private Context mContext;
    private String mFilename;

    public EcoTestAnotherXLSUtils(Context context, String filename) {
        mContext = context;
        mFilename = filename;
    }

    private void makeReport(HSSFWorkbook workbook,ArrayList<EcoTest>tests){
        HSSFSheet sheet=workbook.getSheetAt(0);
        //CellView view=new CellView();
        //view.setHidden(true);
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

        int row = START_ROW_BOILERS;
        int column=START_COLUMN;
        Cell c = null;
        for(EcoTest test:tests){
            if(!test.isAtWork()){
               sheet.setColumnHidden(column,true);
               column++;
            }
            else {
                Date startTime = test.getStartTime().toDate();
                String s = format.format(startTime);
                Row WBrow =sheet.getRow(row);
                c=WBrow.createCell(column);
                c.setCellValue(s);
                row++;

                WBrow =sheet.getRow(row);
                c=WBrow.createCell(column);
                Date endTime = test.getEndTime().toDate();
                s=format.format(endTime);
                c.setCellValue(s);
                row += 2;

                WBrow =sheet.getRow(row);
                c=WBrow.createCell(column);
                c.setCellValue(test.getO2());
                row = row + 2;

                WBrow =sheet.getRow(row);
                c=WBrow.createCell(column);
                c.setCellValue(test.getTemperature());
                row++;

                WBrow =sheet.getRow(row);
                c=WBrow.createCell(column);
                c.setCellValue(test.getCO());
                row++;

                WBrow =sheet.getRow(row);
                c=WBrow.createCell(column);
                c.setCellValue(test.getNOx());
                row = START_ROW_BOILERS;
                column++;
            }
        }
    }

    public Uri sendXlsReport(ArrayList<EcoTest>tests){
        AssetManager assetManager = mContext.getAssets();
        InputStream fin=null;
        try {
            fin = assetManager.open(boilers_template_filename);
            Log.d(TAG, "XLS template loaded");
        } catch (IOException e) {
            Log.e(TAG,"XLS template loading error");
            e.printStackTrace();
        }

        HSSFWorkbook workbook = null;
        try {
            workbook=new HSSFWorkbook(fin);
            Log.d(TAG,"workbook from template created");
        } catch (IOException e) {
            e.printStackTrace();
        }
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + "/ecotest/com/twigproject/xlsreports");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        FileOutputStream fout=null;
        File report = new File(directory,mFilename);

        makeReport(workbook, tests);
        try {
            fout = new FileOutputStream(report);
            workbook.write(fout);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                fin.close();
                fout.close();
            } catch (IOException e) {
            }
        }
        return Uri.fromFile(report);
    }
}
