package com.twigproject.ecotest.Model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.twigproject.ecotest.Model.EcoTest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Utility for serializing / deserializing all ecology tests of current test-session
 * @author Max Ermakov
 */
public class EcoTestJSONSerializer {
    /**
     * TAG label for LogCat
     */
    private static final String TAG="com.twigproject.ecotest.Model.Serializer";
    /**
     * Ref for current context
     */
    private Context mContext;
    /**
     * file for session serializing to / from
     */
    private String mFilename;

    /**
     * Creates new entity of serializer with given context and filename
     * @param context given app context, current by default
     * @param filename file name for serializing to / from
     */
    public EcoTestJSONSerializer(Context context, String filename){
        mContext=context;
        mFilename=filename;
    }

    /**
     * Saves given List of ecology tests to default file
     * @param tests List of test entities for all tested equipment units of current session
     * @throws IOException
     * @throws JSONException
     * @see org.json.JSONArray
     */
    public void saveTests(ArrayList<EcoTest> tests) throws IOException,JSONException {
        JSONArray array=new JSONArray();
        for (EcoTest test:tests){      //for every done test of session
            array.put(test.toJSON());  //add to JSONArray as JSONObject
        }
        Writer writer=null;
        try {                          //write JSONArray to text file
            OutputStream out=mContext.openFileOutput(mFilename,Context.MODE_MULTI_PROCESS);
            writer=new OutputStreamWriter(out);
            writer.write(array.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Serialiser IO error");
        } finally {
            if(writer!=null){
                writer.close();
            }
        }
    }

    /**
     * Loads List of tests of stored session from default file
     * @return ArrayList of EcoTests entities of single session
     * @throws JSONException
     * @throws IOException
     * @see org.json.JSONArray
     * @see java.lang.StringBuilder
     */
    public  ArrayList<EcoTest> loadTests() throws JSONException,IOException{
        ArrayList<EcoTest> tests=new ArrayList<EcoTest>();
        BufferedReader reader=null;
        try {                                                       //loading text file to StringBuilder object
            InputStream in = mContext.openFileInput(mFilename);
            reader=new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString=new StringBuilder();
            String line = null;
            while ((line=reader.readLine())!=null){
                jsonString.append(line);
            }                                                       //splitting loaded json text to JSONArray
            JSONArray array=(JSONArray)new JSONTokener(jsonString.toString()).nextValue();
            for (int i=0;i<array.length();i++){                     //for all objects of JSONArray
                tests.add(new EcoTest(array.getJSONObject(i)));     //create new entity of test and add to ArrayList
            }
        } finally {
            if(reader!=null){
                reader.close();
            }
        }
        return tests;
    }

    /**
     *  Loads List of tests of stored session from given file
     * @param report file for deserializing JSONObject
     * @return ArrayList of EcoTests entities of single session
     * @throws JSONException
     * @throws IOException
     */
    public  ArrayList<EcoTest> loadTests(File report) throws JSONException,IOException{
        ArrayList<EcoTest> tests=new ArrayList<EcoTest>();
        BufferedReader reader=null;
        try {                                                   //loading text file to StringBuilder object
            InputStream in = new FileInputStream(report);
            reader=new BufferedReader(new InputStreamReader(in,"iso-8859-1"), 8);
            StringBuilder jsonString=new StringBuilder();
            String line = null;
            while ((line=reader.readLine())!=null){
                Log.d(TAG,line);
                jsonString.append(line+'\n');
            }                                                   //splitting loaded json text to JSONArray
            JSONArray array=new JSONArray(jsonString.toString());
            for (int i=0;i<array.length();i++){                 //for all objects of JSONArray
                tests.add(new EcoTest(array.getJSONObject(i))); //create new entity of test and add to ArrayList
            }
        } finally {
            if(reader!=null){
                reader.close();
            }
        }
        return tests;
    }
}
