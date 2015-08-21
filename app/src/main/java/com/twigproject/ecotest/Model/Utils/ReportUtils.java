package com.twigproject.ecotest.Model.Utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Easily creates report directory and files
 * @author Max Ermakov
 */
public class ReportUtils {
    /**
     * Creates directory or ref to directory for reports on external storage
     * @return ref to reports directory file object
     */
    public static File getReportPath(){
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + "/ecotest/com/twigproject/reports");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    /**
     * Creates file with giving file name
     * @param filename  file name
     * @return ref to report file object
     */
    public static File getReportFile(String filename){
        File directory = getReportPath();
        //Now create the file in the above directory and write the contents into it
        File newFile = new File(directory, filename);
        try {
            newFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFile;
    }
}
