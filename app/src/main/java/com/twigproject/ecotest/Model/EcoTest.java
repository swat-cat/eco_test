package com.twigproject.ecotest.Model;

import org.json.JSONException;
import org.json.JSONObject;

import org.joda.time.*;

/**
 * Creates entity of ecology test of fuel gases of single unit of heating equipment for current test session
 * @author Max Ermakov max1ermakov@gmail.com
 */
public class EcoTest {
    /**
     * String key for storing / extracting fields values to JSONObject
     */
    private static final String JSON_EQUIPMENT_TYPE = "type";
    private static final String JSON_EQUIPMENT_NUMBER="number";
    private static final String JSON_START_TIME="start_time";
    private static final String JSON_END_TIME="end_time";
    private static final String JSON_O2="O2";
    private static final String JSON_CO="CO";
    private static final String JSON_NOX="NOx";
    private static final String JSON_TEMPERATURE="temperature";
    private static final String JSON_ATWORK="AtWork";

    /**
     * type of heating equipment
     */
    private String mEquipmentType;
    /**
     * number of testing unit
     */
    private int mEquipmentNumber;
    /**
     * time of tests start
     */
    private DateTime mStartTime;
    /**
     * time off tests end
     */
    private DateTime mEndTime;
    /**
     * gas analyser oxigen value, percents
     */
    private Double O2;
    /**
     * gas analyzer CO,ppm (1 ppm == 1/1000000)
     */
    private Double CO;
    /**
     *sum of values NO+NO2, ppm
     */
    private Double NOx;
    /**
     * temperature of burn gases, degrees Celcium
     */
    private Double temperature;
    /**
     * Shows is equipment at work or staying
     */
    private boolean isAtWork;

    /**
     * Default constructor
     */
    public EcoTest() {
        mStartTime=new DateTime();
        mEndTime=mStartTime.plusMinutes(5);
    }

    /**
     * Creates new Ecology test entity of single equipment unit of given type
     * @param type equipment type
     */
    public EcoTest(String type) {
        mEquipmentType=type;
        mStartTime=new DateTime();
        mEndTime=mStartTime.plusMinutes(5);
        setAtWork(true);
    }

    /**
     * Creates new Ecology test entity of single equipment unit of given type and number
     * @param number equipment number - integer, positive
     * @param type equipment type
     */
    public EcoTest(int number,String type) {
        mEquipmentType=type;
        mEquipmentNumber=number;
        mStartTime=DateTime.now();
        mEndTime=mStartTime.plusMinutes(5);
        setAtWork(true);
    }

    /**
     * Re-creates Ecology test entity of single equipment unit of given type and number with
     * help of JSONObject extracted from different data sources
     * @param json JSONObject storing ecotest entity
     * @throws JSONException
     * @see  org.json.JSONObject
     */
    public EcoTest (JSONObject json)throws JSONException{
        mEquipmentType=json.getString(JSON_EQUIPMENT_TYPE);
        mEquipmentNumber=json.getInt(JSON_EQUIPMENT_NUMBER);
        mStartTime=new DateTime(json.getLong(JSON_START_TIME));
        mEndTime=new DateTime(json.getLong(JSON_END_TIME));
        if (json.has(JSON_O2)) {
            O2=json.getDouble(JSON_O2);
        }
        if (json.has(JSON_CO)) {
            CO=json.getDouble(JSON_CO);
        }
        if (json.has(JSON_NOX)) {
            NOx=json.getDouble(JSON_NOX);
        }
        if (json.has(JSON_TEMPERATURE)) {
            temperature=json.getDouble(JSON_TEMPERATURE);
        }
        isAtWork=json.getBoolean(JSON_ATWORK);
    }

    /**
     * puts EcoTest entity to json object
     * @return EcoTest entity stored to JSONObject
     * @throws JSONException
     * @see org.json.JSONObject
     */
    JSONObject toJSON()throws JSONException{
        JSONObject json=new JSONObject();
        json.put(JSON_EQUIPMENT_TYPE,mEquipmentType);
        json.put(JSON_EQUIPMENT_NUMBER,mEquipmentNumber);
        json.put(JSON_START_TIME,mStartTime.getMillis());
        json.put(JSON_END_TIME,mEndTime.getMillis());
        if (O2!=null) {
            json.put(JSON_O2,O2);
        }
        if (CO!=null) {
            json.put(JSON_CO,CO);
        }
        if (NOx!=null) {
            json.put(JSON_NOX,NOx);
        }
        if (temperature!=null) {
            json.put(JSON_TEMPERATURE,temperature);
        }
        json.put(JSON_ATWORK,isAtWork);
        return json;
    }

    /**
     * Converts ppm index value to mg/m3 index value,
     * 28 is CO molV
     * 446.4 is empirical number
     * 21 is default oxygen content in air
     * 3 is default oxygen content in fuel gases
     * @param COppm CO value for converting in mg/m3
     * @param O2 current O2 content in fuel gas
     * @return current CO content in fuel gas in mg/m3
     */
    public static Double COppmToMg(double COppm,double O2){
        return 446.4*28*COppm*(21-3)/(21-O2)*0.0001;
    }

    /**
     * Converts ppm index value to mg/m3 index value,
     * 46 is NO2 molV
     * 446.4 is empirical number
     * 21 is default oxygen content in air
     * 3 is default oxygen content in fuel gases
     * @param NOxppm NOx value for converting in mg/m3
     * @param O2 current O2 content in fuel gas
     * @return current NOx content in fuel gas in mg/m3
     */
    public static Double NOxppmToMg(double NOxppm, double O2){
        return 446.4*46*NOxppm*(21-3)/(21-O2)*0.0001;
    }

    //------ Setters & Getters

    public String getEquipmentType() {
        return mEquipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        mEquipmentType = equipmentType;
    }

    public int getEquipmentNumber() {
        return mEquipmentNumber;
    }

    public void setEquipmentNumber(int equipmentNumber) {

        if (equipmentNumber>0&&equipmentNumber< TestSession.equipment.get(mEquipmentType)) {
            mEquipmentNumber = equipmentNumber;
        }
    }

    public DateTime getStartTime() {
        return mStartTime;
    }

    public void setStartTime(DateTime startTime) {
        mStartTime = startTime;
    }

    public DateTime getEndTime() {
        return mEndTime;
    }

    public void setEndTime(DateTime endTime) {
        mEndTime = endTime;
    }

    public Double getO2() {
        return O2;
    }

    public void setO2(double o2) {
        O2 = o2;
    }

    public Double getCO() {
        return CO;
    }

    public void setCO(double CO) {
        this.CO = CO;
    }

    public Double getNOx() {
        return NOx;
    }

    public void setNOx(double NOx) {
        this.NOx = NOx;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public boolean isAtWork() {
        return isAtWork;
    }

    public void setAtWork(boolean isAtWork) {
        this.isAtWork = isAtWork;
    }
}
