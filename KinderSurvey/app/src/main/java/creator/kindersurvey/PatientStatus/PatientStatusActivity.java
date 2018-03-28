package creator.kindersurvey.PatientStatus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import creator.kindersurvey.InPatient.InPatientScreen;
import creator.kindersurvey.Login.Login;
import creator.kindersurvey.OutPatient.OutPatientScreen;
import creator.kindersurvey.R;
import creator.kindersurvey.Survey.SurveyQuestions;
import creator.kindersurvey.Survey.SurveyRestClient;
import creator.kindersurvey.db.DatabaseHandler;
import creator.kindersurvey.model.EmojiModel;
import creator.kindersurvey.util.AppConstants;
import cz.msebera.android.httpclient.Header;

public class PatientStatusActivity extends AppCompatActivity {
    public static boolean network = false;
    DatabaseHandler databaseHandler = null;
    List<SurveyQuestions> surveyQuestionsList = null;
    CircularProgressButton circularProgressButton = null;
    public static ArrayList<EmojiModel> answerEmojiArrayList;
    boolean ip = false;
    boolean op = false;
    int responseSize;
    ArrayList<HashMap> allResponse;
    int syncNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_status);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        surveyQuestionsList = new ArrayList<>();
        circularProgressButton = findViewById(R.id.syncProgressButton);
        databaseHandler = DatabaseHandler.getInstance(PatientStatusActivity.this);

        checkConnection();

        if (checkSyncStatus()) {
            Toast.makeText(PatientStatusActivity.this, "You can start survey...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(PatientStatusActivity.this, "Sync for updating questions...", Toast.LENGTH_SHORT).show();
        }
        findViewById(R.id.logoutTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.PREF, MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                startActivity(new Intent(PatientStatusActivity.this, Login.class));
                finish();
            }
        });

        findViewById(R.id.IPButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientStatusActivity.this, InPatientScreen.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.OPButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientStatusActivity.this, OutPatientScreen.class);
                startActivity(intent);
            }
        });


        circularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()) {
                    answerEmojiArrayList = new ArrayList<>();
                    circularProgressButton.startAnimation();
                    getQuestions(AppConstants.IP_PATIENT);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (surveyQuestionsList.size() == 0) {
                                circularProgressButton.doneLoadingAnimation(Color.parseColor("#333639"), BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));
                                Toast.makeText(PatientStatusActivity.this, "Sync Completed... Now you can start survey", Toast.LENGTH_SHORT).show();
                                circularProgressButton.revertAnimation();
                            }
                        }
                    }, 5000);
                } else {
                    Toast.makeText(PatientStatusActivity.this, "Connect your network and try again...", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    boolean checkSyncStatus() {
        boolean flag = false;
        if (answerEmojiArrayList == null) {
            answerEmojiArrayList = databaseHandler.getAllQuestionResponses();
            surveyQuestionsList = databaseHandler.getAllQuestions();
            if (answerEmojiArrayList != null && surveyQuestionsList != null) {
                if (answerEmojiArrayList.size() > 0 && surveyQuestionsList.size() > 0) {
                    flag = true;
                }
            }
        } else {
            flag = true;
        }
        return flag;
    }
    private void syncResponseToServer(int index) {
        Log.e("Test", "Inside syncResponseToServer()");
        Log.e("Test", "Response Size : " + allResponse.size());
        Log.e("Test", "Index : " + index);
        if (allResponse.size() > index) {
            HashMap responseHashMap = allResponse.get(index);
            new SyncResponseWithServer().execute(responseHashMap);
        } else {
            syncNumber = 0;
        }
    }

    private class SyncResponseWithServer extends AsyncTask<HashMap, Void, Void> {

        @Override
        protected Void doInBackground(HashMap[] hashMaps) {
            Log.e("Test", "Inside SyncResponseWithServer class");
            ObjectMapper mapperObj = new ObjectMapper();
            mapperObj.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            String jsonResp = "";
            HashMap hashMap1 = null;
            try {
                HashMap hashMap = hashMaps[0];
                hashMap1 = (HashMap) hashMap.get(AppConstants.ID_ARRAY);
                HashMap hashMap2 = (HashMap) hashMap.remove(AppConstants.ID_ARRAY);
                jsonResp = mapperObj.writeValueAsString(hashMap);
                Log.e("Test", "Hashmap name : " + ((HashMap) hashMap.get(AppConstants.FEEDBACK_POST_USER)).get(AppConstants.PATIENT_NAME));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("MYAPP", "exception", e);
            }

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            HttpURLConnection urlConnection;
            String data = jsonResp;
            String result = null;
            try {
                //Connect
                urlConnection = (HttpURLConnection) ((new URL(AppConstants.FEEDBACK_POST_URL).openConnection()));
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty(AppConstants.CONTENT_TYPE, AppConstants.JSON_CONTENT);
                urlConnection.setRequestProperty(AppConstants.ACCEPT, AppConstants.JSON_CONTENT);
                urlConnection.setRequestProperty(AppConstants.AUTHORISATION, AppConstants.AUTHORISATION_VALUE);
                urlConnection.setRequestMethod(AppConstants.FEEDBACK_POST_METHOD);
                urlConnection.connect();

                //Write
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, AppConstants.CHAR_SET));
                writer.write(data);
                writer.close();
                outputStream.close();

                //Read
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), AppConstants.CHAR_SET));

                String line = null;
                StringBuilder sb = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                result = sb.toString();

                Log.e("Response", result);

                if (result != null) {
                    if (result.equals("true")) {
                        for (int index = 1; index <= hashMap1.size(); index++) {
                            String id = (String) hashMap1.get(index);
                            databaseHandler.deleteResponse(id);
                            Log.e("Test", "Inside deleting method");
                        }
                    }
                }
                syncNumber = syncNumber + 1;
                syncResponseToServer(syncNumber);

            } catch (UnsupportedEncodingException e) {

            } catch (IOException e) {
                Log.e("MYAPP", "exception", e);
            }
            return null;
        }
    }

    public void checkConnection() {
        if (isOnline()) {
            network = true;
        } else {
            network = false;
        }
    }

    private void getQuestions(String opPatient) {
        try {
            inwokeWS(opPatient);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void inwokeWS(final String patientType) throws JSONException {
        SurveyRestClient.get(patientType, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("Kinder", "JSONArray");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("Kinder", "JSONObject");
                ParseWSResponse(response, patientType);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                Log.e("Kinder", "String");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("Kinder", "String");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("Kinder", "JSONArray");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("Kinder", "JSONObject");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.e("Kinder", "onFinish");
            }
        });
    }

    private void getResponses(JSONObject response) {
        if (response.length() >= answerEmojiArrayList.size()) {
            JSONArray Answersarr = response.optJSONArray(AppConstants.JSON_ANSWERS);
            for (int Answerarrindex = 0; Answerarrindex < Answersarr.length(); Answerarrindex++) {
                EmojiModel emojiModel = new EmojiModel();
                JSONObject ans_Obj = Answersarr.optJSONObject(Answerarrindex);
                emojiModel.setEmojiId(ans_Obj.optInt(AppConstants.JSON_ANSWER_ID));
                emojiModel.setEmojiText(ans_Obj.optString(AppConstants.JSON_ANSWER_TXT));
                if (ans_Obj.optInt(AppConstants.JSON_ANSWER_RATING) != 0) {
                    emojiModel.setEmojiRating(ans_Obj.optInt(AppConstants.JSON_ANSWER_RATING));
                }
                answerEmojiArrayList.add(emojiModel);
            }
            databaseHandler.deleteAllQuestionsResponse();
            databaseHandler.addQuestionResponse(answerEmojiArrayList);
            answerEmojiArrayList.clear();
        }
    }


    void ParseWSResponse(JSONObject response, String patientType) {
        int answer_id = 0;
        Boolean needDesc = false;
        JSONArray categories = response.optJSONArray(AppConstants.JSON_CATEGORY_NAME);
        if (surveyQuestionsList != null && surveyQuestionsList.size() != 0) {
            surveyQuestionsList.clear();
        }
        for (int categoryindex = 0; categoryindex < categories.length(); categoryindex++) {
            JSONObject category = categories.optJSONObject(categoryindex);
            JSONArray questions = category.optJSONArray(AppConstants.JSON_QUESTIONS);
            for (int questionindex = 0; questionindex < questions.length(); questionindex++) {
                JSONObject question = questions.optJSONObject(questionindex);
                int id = question.optInt(AppConstants.JSON_QUESTIONS_ID);
                String EnglishQuestion = question.optString(AppConstants.JSON_QUESTIONS_TEXT);
                JSONArray MLang = question.optJSONArray(AppConstants.JSON_MALAYALAM_QUESTIONS);

                String testdesc = question.optString(AppConstants.NEED_DESC);
                if (testdesc.equals("Y")) {
                    needDesc = true;
                } else {
                    needDesc = false;
                }
                for (int MLangindex2 = 0; MLangindex2 < MLang.length(); MLangindex2++) {
                    JSONObject MLangDict = MLang.optJSONObject(MLangindex2);
                    String MalayalamQuestion = MLangDict.optString(AppConstants.JSON_QUESTIONS_TEXT);
                    surveyQuestionsList.add(new SurveyQuestions(id, MalayalamQuestion, EnglishQuestion, answer_id, needDesc));
                    Log.e("Test", "surveyQuestionsList size : " + surveyQuestionsList.size());
                    break;
                }
            }
        }
        List<SurveyQuestions> questionsWithPatientType = databaseHandler.getQuestionsWithPatientType(patientType);
        if (questionsWithPatientType != null && questionsWithPatientType.size() != 0 && surveyQuestionsList != null && surveyQuestionsList.size() != 0) {
            Log.e("Test", "Questions");
            boolean delete = databaseHandler.deleteQuestionsWithRespectToPatientType(patientType);
            if (delete) {
                Log.e("Test", "Questions deleted");
                databaseHandler.addQuestions(surveyQuestionsList, patientType);
                Log.e("Test", "Questions added");
            }
        } else {
            Log.e("Test", "No Questions");
            databaseHandler.addQuestions(surveyQuestionsList, patientType);
        }
        surveyQuestionsList.clear();
        getResponses(response);
        if (patientType.equals(AppConstants.IP_PATIENT)) {
            ip = true;
        } else if (patientType.equals(AppConstants.OP_PATIENT)) {
            op = true;
        }
        if (ip && !op) {
            getQuestions(AppConstants.OP_PATIENT);
        } else if (op && !ip) {
            getQuestions(AppConstants.IP_PATIENT);
        }
        if (op && ip) {
            ip = false;
            op = false;
            responseSize = databaseHandler.getResponseSize();
            if (responseSize > 0) {
                allResponse = databaseHandler.getAllResponse();
                syncResponseToServer(syncNumber);
            }
        }
    }
}
