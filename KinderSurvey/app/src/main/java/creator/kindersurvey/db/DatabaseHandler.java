package creator.kindersurvey.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import creator.kindersurvey.Survey.SurveyQuestions;
import creator.kindersurvey.model.EmojiModel;
import creator.kindersurvey.util.AppConstants;

/**
 * Created by Development-2 on 11-01-2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    static DatabaseHandler databaseHandler;

    private DatabaseHandler(Context context) {
        super(context, AppConstants.DATABASE_NAME, null, AppConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_ENG_QUESTION_TABLE = "CREATE TABLE " + AppConstants.TABLE_QUESTIONS + "(" + AppConstants.ID
                + " TEXT," + AppConstants.ENG_QUES_TEXT + " TEXT," + AppConstants.MAL_QUES_TEXT + " TEXT,"
                + AppConstants.PATIENT_TYPE + " TEXT," + AppConstants.NEED_DESCR + " TEXT" + ")";
        String CREATE_QUESTION_RESPONSE_TABLE = "CREATE TABLE " + AppConstants.TABLE_QUESTION_RESPONSE + "("
                + AppConstants.ID + " INTEGER PRIMARY KEY," + AppConstants.PATIENT_NAME + " TEXT,"
                + AppConstants.ROOM_NUMBRER + " TEXT," + AppConstants.ADMINSSION_DATE + " TEXT,"
                + AppConstants.DISCHARGE_DATE + " TEXT," + AppConstants.CONTACT_NUMBER + " TEXT,"
                + AppConstants.EMAIL_ID + " TEXT," + AppConstants.PATIENT_TYPE + " TEXT," + AppConstants.UHID + " TEXT,"
                + AppConstants.QUES_ID + " TEXT," + AppConstants.RES_ID + " TEXT," + AppConstants.RES_DESCR + " TEXT,"
                + AppConstants.FEEDBACK + " TEXT" + ")";
        String CREATE_RESPONSE_TABLE = "CREATE TABLE " + AppConstants.TABLE_RESPONSE + "(" + AppConstants.ID
                + " TEXT," + AppConstants.RES_TEXT + " TEXT," + AppConstants.RES_RATING + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_ENG_QUESTION_TABLE);
        sqLiteDatabase.execSQL(CREATE_QUESTION_RESPONSE_TABLE);
        sqLiteDatabase.execSQL(CREATE_RESPONSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
// Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AppConstants.TABLE_QUESTIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AppConstants.TABLE_QUESTION_RESPONSE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AppConstants.TABLE_RESPONSE);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public static DatabaseHandler getInstance(Context context) {
        if (databaseHandler == null) {
            databaseHandler = new DatabaseHandler(context);
        }
        return databaseHandler;
    }

    public void addQuestions(List<SurveyQuestions> surveyQuestionsList, String patientType) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        String englishQuestion = "";
        for (SurveyQuestions surveyQuestions : surveyQuestionsList) {
            englishQuestion = surveyQuestions.getEnglishQuestion();
            if (englishQuestion.contains("'")) {
                englishQuestion = englishQuestion.replace("'", "''");
            }
            String insertQuery = "INSERT OR REPLACE INTO " + AppConstants.TABLE_QUESTIONS + " VALUES ("
                    + "'" + String.valueOf(surveyQuestions.getId()) + "'" + ", " + "'" + englishQuestion
                    + "'" + ", " + "'" + surveyQuestions.getMalayalamQuestion() + "'" + ", " + "'" + patientType + "'" + ", "
                    + "'" + String.valueOf(surveyQuestions.getResponseid()) + "'" + ");";
            if (writableDatabase.isOpen()) {
                writableDatabase.execSQL(insertQuery);
            }
        }
    }

    public List<SurveyQuestions> getAllQuestions() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        List<SurveyQuestions> surveyQuestionsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + AppConstants.TABLE_QUESTIONS;
        Cursor cursor = readableDatabase.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            SurveyQuestions surveyQuestions = new SurveyQuestions();
            surveyQuestions.setId(cursor.getInt(cursor.getColumnIndex(AppConstants.ID)));
            surveyQuestions.setEnglishQuestion(cursor.getString((cursor.getColumnIndex(AppConstants.ENG_QUES_TEXT))));
            surveyQuestions.setMalayalamQuestion(cursor.getString(cursor.getColumnIndex(AppConstants.MAL_QUES_TEXT)));
            if (cursor.getString(cursor.getColumnIndex(AppConstants.NEED_DESCR)).equals("Y")) {
                surveyQuestions.setResponseid(true);
            } else {
                surveyQuestions.setResponseid(false);
            }
            surveyQuestionsList.add(surveyQuestions);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return surveyQuestionsList;
    }

    public void addResponse(HashMap responseHashMap) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        HashMap nameHashMap = null;
        HashMap userResponseHashMap = null;
        HashMap userAnswerDescrHashMap = null;
        if (responseHashMap != null) {
            Object nameObject = responseHashMap.get(AppConstants.FEEDBACK_POST_USER);
            if (nameObject instanceof HashMap) {
                nameHashMap = (HashMap) nameObject;
            }
            Object userResponseObject = responseHashMap.get(AppConstants.FEEDBACK_POST_ANSWERS);
            if (userResponseObject instanceof HashMap) {
                userResponseHashMap = (HashMap) userResponseObject;
            }
            Object userAnswerDescrObject = responseHashMap.get(AppConstants.ANSWER_DESC);
            if (userAnswerDescrObject instanceof HashMap) {
                userAnswerDescrHashMap = (HashMap) userAnswerDescrObject;
            }
            String patientName = (String) nameHashMap.get(AppConstants.PATIENT_NAME);
            String roomNumber = (String) nameHashMap.get(AppConstants.ROOM_NUMBRER);
            String admissionDate = (String) nameHashMap.get(AppConstants.ADMINSSION_DATE);
            String dischargeDate = (String) nameHashMap.get(AppConstants.DISCHARGE_DATE);
            String contactNumber = (String) nameHashMap.get(AppConstants.CONTACT_NUMBER);
            String emailId = (String) nameHashMap.get(AppConstants.EMAIL_ID);
            String patientType = (String) nameHashMap.get(AppConstants.PATIENT_TYPE);
            String uhidNumber = (String) nameHashMap.get(AppConstants.UHID_NUMBER);
            String feedback = (String) nameHashMap.get(AppConstants.FEEDBACK);

            Set keySet = userResponseHashMap.keySet();
            Iterator iterator = keySet.iterator();
            Set set = userAnswerDescrHashMap.keySet();
            Iterator iterator1 = set.iterator();
            while (iterator.hasNext()) {
                Object next = iterator.next();
                while (iterator1.hasNext()) {
                    Object next1 = iterator1.next();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(AppConstants.PATIENT_NAME, patientName);
                    contentValues.put(AppConstants.ROOM_NUMBRER, roomNumber);
                    contentValues.put(AppConstants.ADMINSSION_DATE, admissionDate);
                    contentValues.put(AppConstants.DISCHARGE_DATE, dischargeDate);
                    contentValues.put(AppConstants.CONTACT_NUMBER, contactNumber);
                    contentValues.put(AppConstants.EMAIL_ID, emailId);
                    contentValues.put(AppConstants.PATIENT_TYPE, patientType);
                    contentValues.put(AppConstants.UHID, uhidNumber);
                    contentValues.put(AppConstants.QUES_ID, next.toString());
                    contentValues.put(AppConstants.RES_ID, userResponseHashMap.get(next).toString());
                    contentValues.put(AppConstants.RES_DESCR, userAnswerDescrHashMap.get(next1).toString());
                    contentValues.put(AppConstants.FEEDBACK, feedback);
                    writableDatabase.insert(AppConstants.TABLE_QUESTION_RESPONSE, null, contentValues);
                    break;
                }
            }
        }
    }

    public ArrayList<HashMap> getAllResponse() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<HashMap> hashMapArrayList = new ArrayList<>();
        Cursor cursor = readableDatabase.query(AppConstants.TABLE_QUESTION_RESPONSE, null, null, null, null, null, null);
        String firstPatientName = "";
        String firstRoomNumber = "";
        String firstAdmissionDate = "";
        String firstDischargeDate = "";
        String firstContactNumber = "";
        String firstEmailId = "";
        String firstPatientType = "";
        String firstUhidNumber = "";
        String firstQuestionId = "";
        String firstAnswerId = "";
        String firstAnswerDescr = "";
        String firstFeedback = "";

        HashMap responseHashMap = null;
        HashMap responseDescrHashMap = null;
        HashMap userHashMap = null;
        HashMap allResponseHashMap = null;
        HashMap responseIdHashMap = null;
        int index = 0;

        while (cursor.moveToNext()) {
            String responseId = cursor.getString(cursor.getColumnIndex(AppConstants.ID));
            String patientName = cursor.getString(cursor.getColumnIndex(AppConstants.PATIENT_NAME));
            String roomNumber = cursor.getString(cursor.getColumnIndex(AppConstants.ROOM_NUMBRER));
            String admissionDate = cursor.getString(cursor.getColumnIndex(AppConstants.ADMINSSION_DATE));
            String dischargeDate = cursor.getString(cursor.getColumnIndex(AppConstants.DISCHARGE_DATE));
            String contactNumber = cursor.getString(cursor.getColumnIndex(AppConstants.CONTACT_NUMBER));
            String emailId = cursor.getString(cursor.getColumnIndex(AppConstants.EMAIL_ID));
            String patientType = cursor.getString(cursor.getColumnIndex(AppConstants.PATIENT_TYPE));
            String uhidNumber = cursor.getString(cursor.getColumnIndex(AppConstants.UHID));
            String questionId = cursor.getString(cursor.getColumnIndex(AppConstants.QUES_ID));
            String answerId = cursor.getString(cursor.getColumnIndex(AppConstants.RES_ID));
            String answerDescr = cursor.getString(cursor.getColumnIndex(AppConstants.RES_DESCR));
            String feedback = cursor.getString(cursor.getColumnIndex(AppConstants.FEEDBACK));
            if (firstPatientName.equals("") && firstPatientType.equals("") && firstUhidNumber.equals("")) {

                firstPatientName = patientName;
                firstRoomNumber = roomNumber;
                firstAdmissionDate = admissionDate;
                firstDischargeDate = dischargeDate;
                firstContactNumber = contactNumber;
                firstEmailId = emailId;
                firstPatientType = patientType;
                firstUhidNumber = uhidNumber;
                firstQuestionId = questionId;
                firstAnswerId = answerId;
                firstAnswerDescr = answerDescr;
                firstFeedback = feedback;

                userHashMap = new HashMap();
                userHashMap.put(AppConstants.PATIENT_NAME, firstPatientName);
                userHashMap.put(AppConstants.ROOM_NUMBRER, firstRoomNumber);
                userHashMap.put(AppConstants.ADMINSSION_DATE, firstAdmissionDate);
                userHashMap.put(AppConstants.DISCHARGE_DATE, firstDischargeDate);
                userHashMap.put(AppConstants.CONTACT_NUMBER, firstContactNumber);
                userHashMap.put(AppConstants.EMAIL_ID, firstEmailId);
                userHashMap.put(AppConstants.PATIENT_TYPE, firstPatientType);
                userHashMap.put(AppConstants.UHID_NUMBER, firstUhidNumber);
                userHashMap.put(AppConstants.FEEDBACK, firstFeedback);
                responseHashMap = new HashMap();
                responseDescrHashMap = new HashMap();
                responseIdHashMap = new HashMap();
                responseHashMap.put(firstQuestionId, firstAnswerId);
                responseDescrHashMap.put(firstQuestionId, firstAnswerDescr);
                index = index + 1;
                responseIdHashMap.put(index, responseId);
            } else {
                if (firstPatientName.equals(patientName) && firstPatientType.equals(patientType) && firstUhidNumber.equals(uhidNumber)) {
                    responseHashMap.put(questionId, answerId);
                    responseDescrHashMap.put(questionId, answerDescr);
                    index = index + 1;
                    responseIdHashMap.put(index, responseId);
                } else {
                    allResponseHashMap = new HashMap();
                    allResponseHashMap.put(AppConstants.FEEDBACK_POST_USER, userHashMap);
                    allResponseHashMap.put(AppConstants.FEEDBACK_POST_ANSWERS, responseHashMap);
                    allResponseHashMap.put(AppConstants.ANSWER_DESC, responseDescrHashMap);
                    allResponseHashMap.put(AppConstants.ID_ARRAY, responseIdHashMap);
                    index = 0;
                    hashMapArrayList.add(allResponseHashMap);
                    userHashMap = new HashMap();
                    userHashMap.put(AppConstants.PATIENT_NAME, patientName);
                    userHashMap.put(AppConstants.ROOM_NUMBRER, roomNumber);
                    userHashMap.put(AppConstants.ADMINSSION_DATE, admissionDate);
                    userHashMap.put(AppConstants.DISCHARGE_DATE, dischargeDate);
                    userHashMap.put(AppConstants.CONTACT_NUMBER, contactNumber);
                    userHashMap.put(AppConstants.EMAIL_ID, emailId);
                    userHashMap.put(AppConstants.PATIENT_TYPE, patientType);
                    userHashMap.put(AppConstants.UHID_NUMBER, uhidNumber);
                    userHashMap.put(AppConstants.FEEDBACK, feedback);
                    responseHashMap = new HashMap();
                    responseDescrHashMap = new HashMap();
                    responseIdHashMap = new HashMap();
                    responseHashMap.put(questionId, answerId);
                    responseDescrHashMap.put(questionId, answerDescr);
                    index = index + 1;
                    responseIdHashMap.put(index, responseId);

                    firstPatientName = patientName;
                    firstRoomNumber = roomNumber;
                    firstAdmissionDate = admissionDate;
                    firstDischargeDate = dischargeDate;
                    firstContactNumber = contactNumber;
                    firstEmailId = emailId;
                    firstPatientType = patientType;
                    firstUhidNumber = uhidNumber;
                    firstQuestionId = questionId;
                    firstAnswerId = answerId;
                    firstAnswerDescr = answerDescr;
                    firstFeedback = feedback;
                }
            }
        }
        allResponseHashMap = new HashMap();
        allResponseHashMap.put(AppConstants.FEEDBACK_POST_USER, userHashMap);
        allResponseHashMap.put(AppConstants.FEEDBACK_POST_ANSWERS, responseHashMap);
        allResponseHashMap.put(AppConstants.ANSWER_DESC, responseDescrHashMap);
        allResponseHashMap.put(AppConstants.ID_ARRAY, responseIdHashMap);
        hashMapArrayList.add(allResponseHashMap);
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return hashMapArrayList;
    }

    public boolean deleteQuestions() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        int delete = writableDatabase.delete(AppConstants.TABLE_QUESTIONS, null, null);
        writableDatabase.close();
        if (delete > 0) {
            return true;
        }
        return false;
    }

    public boolean deleteResponses() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        int delete = writableDatabase.delete(AppConstants.TABLE_QUESTION_RESPONSE, null, null);
        writableDatabase.close();
        if (delete > 0) {
            return true;
        }
        return false;
    }

    public boolean deleteQuestionsWithRespectToPatientType(String patientType) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        int delete = writableDatabase.delete(AppConstants.TABLE_QUESTIONS, AppConstants.PATIENT_TYPE + "=?", new String[]{patientType});
        if (delete > 0) {
            return true;
        }
        return false;
    }

    public List<SurveyQuestions> getQuestionsWithPatientType(String patientType) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        List<SurveyQuestions> surveyQuestionsList = new ArrayList<>();
        Cursor cursor = readableDatabase.query(AppConstants.TABLE_QUESTIONS, null, AppConstants.PATIENT_TYPE + "=?",
                new String[]{patientType}, null, null, null, null);
        while (cursor.moveToNext()) {
            SurveyQuestions surveyQuestions = new SurveyQuestions();
            surveyQuestions.setId(cursor.getInt(cursor.getColumnIndex(AppConstants.ID)));
            surveyQuestions.setEnglishQuestion(cursor.getString((cursor.getColumnIndex(AppConstants.ENG_QUES_TEXT))));
            surveyQuestions.setMalayalamQuestion(cursor.getString(cursor.getColumnIndex(AppConstants.MAL_QUES_TEXT)));
            String needDescr = cursor.getString(cursor.getColumnIndex(AppConstants.NEED_DESCR));
            if (needDescr.equalsIgnoreCase("true")) {
                surveyQuestions.setResponseid(true);
            } else {
                surveyQuestions.setResponseid(false);
            }
            surveyQuestionsList.add(surveyQuestions);
        }
        if (cursor != null && cursor.isClosed()) {
            cursor.close();
        }
        Log.e("Response", surveyQuestionsList.size() + "");
        return surveyQuestionsList;
    }

    public boolean deleteResponse(String rowId) {
        boolean flag = false;
        SQLiteDatabase writableDatabase = getWritableDatabase();
        int delete = writableDatabase.delete(AppConstants.TABLE_QUESTION_RESPONSE, AppConstants.ID + "=?", new String[]{rowId});
        if (delete > 0) {
            flag = true;
        } else {
            flag = false;
        }
        if (writableDatabase != null && writableDatabase.isOpen()) {
            writableDatabase.close();
        }
        return flag;
    }

    public int getResponseSize() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String countQuery = "SELECT * FROM " + AppConstants.TABLE_QUESTION_RESPONSE;
        Cursor cursor = readableDatabase.rawQuery(countQuery, null);
        cursor.moveToFirst();
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return cursor.getCount();
    }

    public void addQuestionResponse(ArrayList<EmojiModel> emojiModelArrayList) {
        SQLiteDatabase writableDatabase = getWritableDatabase();

        if (emojiModelArrayList != null) {
            if (emojiModelArrayList.size() > 0) {
                for (EmojiModel emojiModel : emojiModelArrayList) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(AppConstants.ID, emojiModel.getEmojiId());
                    contentValues.put(AppConstants.RES_TEXT, emojiModel.getEmojiText());
                    contentValues.put(AppConstants.RES_RATING, emojiModel.getEmojiRating());
                    if (!writableDatabase.isOpen()) {
                        writableDatabase = getWritableDatabase();
                    }
                    if (writableDatabase.isOpen()) {
                        writableDatabase.insert(AppConstants.TABLE_RESPONSE, null, contentValues);
                    }
                }
            }
        }
    }

    public ArrayList<EmojiModel> getAllQuestionResponses() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList<EmojiModel> emojiModelArrayList = new ArrayList<>();
        Cursor cursor = readableDatabase.query(AppConstants.TABLE_RESPONSE, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            EmojiModel emojiModel = new EmojiModel();
            emojiModel.setEmojiId(cursor.getInt(cursor.getColumnIndex(AppConstants.ID)));
            emojiModel.setEmojiText(cursor.getString(cursor.getColumnIndex(AppConstants.RES_TEXT)));
            emojiModel.setEmojiRating(cursor.getInt(cursor.getColumnIndex(AppConstants.RES_RATING)));
            emojiModelArrayList.add(emojiModel);
        }
        return emojiModelArrayList;
    }

    public boolean deleteAllQuestionsResponse() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        int delete = writableDatabase.delete(AppConstants.TABLE_RESPONSE, null, null);
        if (delete > 0) {
            return true;
        }
        if (writableDatabase != null && writableDatabase.isOpen()) {
            writableDatabase.close();
        }
        return false;
    }
}