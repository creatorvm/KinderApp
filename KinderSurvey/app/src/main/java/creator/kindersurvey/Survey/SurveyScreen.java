package creator.kindersurvey.Survey;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import creator.kindersurvey.R;
import creator.kindersurvey.db.DatabaseHandler;
import creator.kindersurvey.feedback.FeedbackActivity;
import creator.kindersurvey.model.EmojiModel;
import creator.kindersurvey.util.AppConstants;
import cz.msebera.android.httpclient.Header;
import pl.droidsonroids.gif.GifImageView;

public class SurveyScreen extends AppCompatActivity {

    String PatientType = "";
    SweetAlertDialog pDialog;
    int QuestionNumber = 0;
    int totalQuestionPosition = 0;

    int averageFeedback = 0;
    String average = "";

    List<Integer> Answers = new ArrayList<Integer>();
    List<String> responseDescription = new ArrayList<String>();

    TextView MalayalamQuestionTextView, EnglishQuestionTextView;

    List<SurveyQuestions> surveyQuestionsList = new ArrayList<>();
    ArrayList<EmojiModel> answerEmojiArrayList;
    LinearLayout linearLayoutexcellent = null;
    LinearLayout linearLayoutverygood = null;
    LinearLayout linearLayoutgood = null;
    LinearLayout linearLayoutfair = null;
    LinearLayout linearLayoutpoor = null;

    ImageButton previousImageButton = null;
    ImageButton nextImageButton = null;

    static Activity surveyScreenActivity = null;

    GifImageView babyGifImageView = null;
    ImageView cloudImageView = null;
    ImageView cloud2 = null;

    TextView excellentTextView = null;
    TextView veryGoodTextView = null;
    TextView goodTextView = null;
    TextView fairTextView = null;
    TextView poorTextView = null;
    SharedPreferences sharedPreferences = null;

    String roomNumber = "";
    String uhidNumber = "";
    String contactNumber = "";
    String emailAddress = "";
    String patientName = "";
    String admissionDate = "";
    String dischargeDate = "";

    Animation questionSlideDownAnimation = null;

    LinearLayout naLinearLayout = null;
    LinearLayout emojiLinearLayout = null;

    LinearLayout less15minLinearLayout = null;
    LinearLayout between15and30LinearLayout = null;
    LinearLayout between30and1hrLinearLayout = null;
    LinearLayout above1hrLinearLayout = null;

    TextView less15minTextView = null;
    TextView between15and30TextView = null;
    TextView between30and1hrTextView = null;
    TextView above1hrTextView = null;
    TextView notRelevantTextView = null;

    boolean survey = false;

    Uri notification;
    Ringtone ringtone;
    MediaPlayer mPlayer;

    DatabaseHandler databaseHandler;


    protected LinearLayout setLayout(int layoutId, final String emoji) {

        LinearLayout layout = (LinearLayout) findViewById(layoutId);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                try {
                    playSound();
                    Log.e("Sound", notification.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                v.startAnimation(AnimationUtils.loadAnimation(SurveyScreen.this, R.anim.zoomin));
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        v.clearAnimation();
                        moveToNextQuestion(emoji);
                    }
                }, 300);
            }
        });
        return layout;
    }

    void playSound() {
        ringtone.stop();
        ringtone.play();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Creating this activity's object
        surveyScreenActivity = this;
        databaseHandler = DatabaseHandler.getInstance(SurveyScreen.this);

        previousImageButton = findViewById(R.id.previousImageButton);
        nextImageButton = findViewById(R.id.nextImageButton);
        babyGifImageView = findViewById(R.id.babyGifImageView);
        cloudImageView = findViewById(R.id.cloudImageView);
        excellentTextView = findViewById(R.id.excellentTextView);
        veryGoodTextView = findViewById(R.id.veryGoodTextView);
        goodTextView = findViewById(R.id.goodTextView);
        fairTextView = findViewById(R.id.fairTextView);
        poorTextView = findViewById(R.id.poorTextView);
        cloud2 = findViewById(R.id.cloud2);
        sharedPreferences = getSharedPreferences(AppConstants.PREF, MODE_PRIVATE);
        notification = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.original_hangouts_sms_tone);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
        mPlayer = MediaPlayer.create(SurveyScreen.this, R.raw.original_hangouts_sms_tone);


        linearLayoutexcellent = (LinearLayout) findViewById(R.id.survey_ll_excellent);
        linearLayoutverygood = (LinearLayout) findViewById(R.id.survey_ll_verygood);
        linearLayoutgood = (LinearLayout) findViewById(R.id.survey_ll_good);
        linearLayoutfair = (LinearLayout) findViewById(R.id.survey_ll_fair);
        linearLayoutpoor = (LinearLayout) findViewById(R.id.survey_ll_poor);

        naLinearLayout = findViewById(R.id.naLinearLayout);
        emojiLinearLayout = findViewById(R.id.emojiLinearLayout);

        less15minLinearLayout = findViewById(R.id.less15min);
        between15and30LinearLayout = findViewById(R.id.between15and30);
        between30and1hrLinearLayout = findViewById(R.id.between30and1hr);
        above1hrLinearLayout = findViewById(R.id.above1hr);

        less15minTextView = findViewById(R.id.lessThan15TextView);
        between15and30TextView = findViewById(R.id.between15and30TextView);
        between30and1hrTextView = findViewById(R.id.between30and1hrTextView);
        above1hrTextView = findViewById(R.id.above1hrTextView);
        notRelevantTextView = findViewById(R.id.nrTextView);

        setImageLoop();

        linearLayoutexcellent = setLayout(R.id.survey_ll_excellent, AppConstants.EXCELLENT);
        linearLayoutverygood = setLayout(R.id.survey_ll_verygood, AppConstants.VERY_GOOD);
        linearLayoutgood = setLayout(R.id.survey_ll_good, AppConstants.GOOD);
        linearLayoutfair = setLayout(R.id.survey_ll_fair, AppConstants.FAIR);
        linearLayoutpoor = setLayout(R.id.survey_ll_poor, AppConstants.POOR);

        less15minLinearLayout = setLayout(R.id.less15min, AppConstants.LESS_THAN_15);
        between15and30LinearLayout = setLayout(R.id.between15and30, AppConstants.BETWEEN_15_AND_30);
        between30and1hrLinearLayout = setLayout(R.id.between30and1hr, AppConstants.BETWEEN_30_AND_1HR);
        above1hrLinearLayout = setLayout(R.id.above1hr, AppConstants.ABOVE_1HR);
        naLinearLayout = setLayout(R.id.naLinearLayout, AppConstants.NOT_RELEVANT);

        MalayalamQuestionTextView = (TextView) findViewById(R.id.malayalamquestionTv);
        EnglishQuestionTextView = (TextView) findViewById(R.id.englishquestionTV);

        questionSlideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        Intent intent = getIntent();
        PatientType = intent.getStringExtra(AppConstants.PATIENT_TYPE);


        excellentTextView.setTextSize(20);


        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            patientName = bundle.getString(AppConstants.PATIENT_NAME);
            uhidNumber = bundle.getString(AppConstants.UHID);
            contactNumber = bundle.getString(AppConstants.CONTACT_NUMBER);
            emailAddress = bundle.getString(AppConstants.EMAIL_ID);
            if (PatientType.equals(AppConstants.IP_PATIENT)) {
                admissionDate = bundle.getString(AppConstants.ADMINSSION_DATE);
                dischargeDate = bundle.getString(AppConstants.DISCHARGE_DATE);
                roomNumber = bundle.getString(AppConstants.ROOM_NUMBRER);
            }
        }

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        surveyQuestionsList = databaseHandler.getQuestionsWithPatientType(PatientType);
        Log.e("Test", "Patient Type : " + PatientType);
        if (surveyQuestionsList.size() != 0) {
            if (Answers.size() == 0) {
                for (SurveyQuestions surveyQuestions : surveyQuestionsList) {
                    Log.e("Test", "Questions : " + surveyQuestions.getMalayalamQuestion());
                    Answers.add(0);
                    responseDescription.add("");
                    totalQuestionPosition = Answers.size() - 1;
                }
            }
            if (answerEmojiArrayList == null) {
                answerEmojiArrayList = databaseHandler.getAllQuestionResponses();
            }
        } else {
            finish();
            Toast.makeText(SurveyScreen.this, "Please sync one more time... Questions not updated...", Toast.LENGTH_SHORT).show();
        }
        UpdateQuestion(QuestionNumber, "");
        previousImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestionNumber = QuestionNumber - 1;
                if (QuestionNumber == 0) {
                    if (previousImageButton.getVisibility() == View.VISIBLE) {
                        previousImageButton.setVisibility(View.GONE);
                    }
                }

                String emojiText = "";
                for (EmojiModel emojiModel : answerEmojiArrayList) {
                    if (emojiModel.getEmojiId() == Answers.get(QuestionNumber)) {
                        emojiText = emojiModel.getEmojiText();
                    }
                }

                UpdateQuestion(QuestionNumber, emojiText);
                if (QuestionNumber < totalQuestionPosition) {
                    if (nextImageButton.getVisibility() == View.GONE) {
                        nextImageButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (nextImageButton.getVisibility() == View.VISIBLE) {
                        nextImageButton.setVisibility(View.GONE);
                    }
                }
            }
        });

        nextImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestionNumber = QuestionNumber + 1;

                if (previousImageButton.getVisibility() == View.GONE) {
                    previousImageButton.setVisibility(View.VISIBLE);
                }

                String emojiText = "";
                for (EmojiModel emojiModel : answerEmojiArrayList) {
                    if (emojiModel.getEmojiId() == Answers.get(QuestionNumber)) {
                        emojiText = emojiModel.getEmojiText();
                    }
                }


                if (QuestionNumber <= totalQuestionPosition) {
                    if (emojiText.equals("")) {
                        nextImageButton.setVisibility(View.GONE);
                    } else {
                        nextImageButton.setVisibility(View.VISIBLE);
                    }
                }

                UpdateQuestion(QuestionNumber, emojiText);
            }
        });
    }

    private void setImageLoop() {
        // Need a thread to get the real size or the parent
        // container, after the UI is displayed
        babyGifImageView.post(new Runnable() {
            @Override
            public void run() {


                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                final float screen_width_half = metrics.widthPixels / 2;
                final float distance = screen_width_half - 400;


                TranslateAnimation outAnim =
                        new TranslateAnimation(
                                -distance, metrics.widthPixels, 0, 0);
                // move from 0 (START) to width (PARENT_SIZE)
                outAnim.setInterpolator(new LinearInterpolator());
                outAnim.setRepeatMode(Animation.INFINITE); // repeat the animation
                outAnim.setRepeatCount(Animation.INFINITE);
                outAnim.setDuration(30000);

                final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.setInterpolator(new LinearInterpolator());
                animator.setDuration(30000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        final float progress = (float) animation.getAnimatedValue();
                        final float width = cloudImageView.getWidth();
                        final float translationX = width * progress;
                        cloudImageView.setTranslationX(translationX);
                        cloud2.setTranslationX(translationX - width);
                    }
                });
                animator.start();
                babyGifImageView.startAnimation(outAnim); // start first anim
            }
        });
    }


    void moveToNextQuestion(String emoji) {
        int emojiId = 0;
        for (EmojiModel emojiModel : answerEmojiArrayList) {
            if (emoji.equals(emojiModel.getEmojiText())) {
                emojiId = emojiModel.getEmojiId();
            }
        }

        if (QuestionNumber <= totalQuestionPosition) {
            Answers.set(QuestionNumber, emojiId);
            if (emoji.equals(AppConstants.LESS_THAN_15) || emoji.equals(AppConstants.BETWEEN_15_AND_30)
                    || emoji.equals(AppConstants.BETWEEN_30_AND_1HR) || emoji.equals(AppConstants.ABOVE_1HR)
                    || emoji.equals(AppConstants.NOT_RELEVANT)) {
                responseDescription.set(QuestionNumber, emoji);
            } else {
                responseDescription.set(QuestionNumber, "");
            }
        }
        if (previousImageButton.getVisibility() == View.GONE) {
            previousImageButton.setVisibility(View.VISIBLE);
        }

        QuestionNumber = QuestionNumber + 1;

        String emojiText = "";

        if (QuestionNumber <= totalQuestionPosition) {

            for (EmojiModel emojiModel : answerEmojiArrayList) {
                if (emojiModel.getEmojiId() == Answers.get(QuestionNumber)) {
                    emojiText = emojiModel.getEmojiText();
                }
            }

            if (emojiText.equals("")) {
                nextImageButton.setVisibility(View.GONE);
            } else {
                nextImageButton.setVisibility(View.VISIBLE);
            }
        }

        if (QuestionNumber <= totalQuestionPosition) {
            UpdateQuestion(QuestionNumber, emojiText);
        } else {
            calculateFeedback();
        }
    }

    private void calculateFeedback() {
        int totalFeedback = 0;
        int response = 0;
        for (int index = 0; index < Answers.size(); index++) {
            for (EmojiModel emojiModel : answerEmojiArrayList) {
                if (emojiModel.getEmojiId() == Answers.get(index)) {
                    if (emojiModel.getEmojiText().equals(AppConstants.EXCELLENT)) {
                        totalFeedback = totalFeedback + AppConstants.EXCELLENT_RATING;
                    } else if (emojiModel.getEmojiText().equals(AppConstants.VERY_GOOD)) {
                        totalFeedback = totalFeedback + AppConstants.VERY_GOOD_RATING;
                    } else if (emojiModel.getEmojiText().equals(AppConstants.GOOD)) {
                        totalFeedback = totalFeedback + AppConstants.GOOD_RATING;
                    } else if (emojiModel.getEmojiText().equals(AppConstants.FAIR)) {
                        totalFeedback = totalFeedback + AppConstants.FAIR_RATING;
                    } else if (emojiModel.getEmojiText().equals(AppConstants.POOR)) {
                        totalFeedback = totalFeedback + AppConstants.POOR_RATING;
                    }
                    break;
                }
            }
        }

        for (int index = 0; index < responseDescription.size(); index++) {
            if (!responseDescription.get(index).equals("")) {
                response = response + 1;
            }
        }

        int totalQuestions = totalQuestionPosition + 1;
        totalQuestions = totalQuestions - response;
        if (totalQuestions == 0) {
            totalQuestions = totalQuestionPosition + 1;
        }
        averageFeedback = totalFeedback / totalQuestions;


        if (averageFeedback >= AppConstants.EXCELLENT_RATING) {
            average = AppConstants.EXCELLENT;
        } else if (averageFeedback >= AppConstants.VERY_GOOD_RATING && averageFeedback < AppConstants.EXCELLENT_RATING) {
            average = AppConstants.VERY_GOOD;
        } else if (averageFeedback >= AppConstants.GOOD_RATING && averageFeedback < AppConstants.VERY_GOOD_RATING) {
            average = AppConstants.GOOD;
        } else if (averageFeedback >= AppConstants.FAIR_RATING && averageFeedback < AppConstants.GOOD_RATING) {
            average = AppConstants.FAIR;
        } else if (averageFeedback >= AppConstants.POOR_RATING && averageFeedback < AppConstants.FAIR_RATING) {
            average = AppConstants.POOR;
        } else if (averageFeedback < AppConstants.POOR_RATING) {
            average = AppConstants.POOR;
        }

        LayoutInflater li = LayoutInflater.from(SurveyScreen.this);
        View promptsView = li.inflate(R.layout.comment_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                SurveyScreen.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String comments = userInput.getText().toString();
                                if (comments.length() > AppConstants.INPUT_LENGTH) {
                                    Toast.makeText(SurveyScreen.this, "Comment is too long", Toast.LENGTH_SHORT).show();
                                    userInput.setText("");
                                } else {
//                                    addSurvey(userInput.getText().toString());
                                    addSurvey(comments);
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                addSurvey("");
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
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

    private void addSurvey(String comment) {

//        ShowProgress(true);
        HashMap hashMap = new HashMap();
        for (int answersIndex = 0; answersIndex < Answers.size(); answersIndex++) {
            SurveyQuestions surveyQuestions = surveyQuestionsList.get(answersIndex);
            int answerId = Answers.get(answersIndex);
            hashMap.put(surveyQuestions.getId(), answerId);
        }

        HashMap responseHashMap = new HashMap();
        for (int answersIndex = 0; answersIndex < responseDescription.size(); answersIndex++) {
            SurveyQuestions surveyQuestions = surveyQuestionsList.get(answersIndex);
            String response = responseDescription.get(answersIndex);
            responseHashMap.put(surveyQuestions.getId(), response);
        }

        HashMap nameHashMap = new HashMap();
        nameHashMap.put(AppConstants.PATIENT_NAME, patientName);

        if (!roomNumber.equals("")) {
            nameHashMap.put(AppConstants.ROOM_NUMBRER, roomNumber);
        }

        if (!admissionDate.equals("")) {
            nameHashMap.put(AppConstants.ADMINSSION_DATE, admissionDate);
        }

        if (!dischargeDate.equals("")) {
            nameHashMap.put(AppConstants.DISCHARGE_DATE, dischargeDate);
        }

        if (!contactNumber.equals("")) {
            nameHashMap.put(AppConstants.CONTACT_NUMBER, contactNumber);
        }
        if (!emailAddress.equals("")) {
            nameHashMap.put(AppConstants.EMAIL_ID, emailAddress);
        }

        nameHashMap.put(AppConstants.PATIENT_TYPE, PatientType);
        nameHashMap.put(AppConstants.UHID_NUMBER, uhidNumber);
        nameHashMap.put(AppConstants.FEEDBACK, comment);

        HashMap requestParams = new HashMap();
        requestParams.put(AppConstants.FEEDBACK_POST_USER, nameHashMap);
        requestParams.put(AppConstants.FEEDBACK_POST_ANSWERS, hashMap);
        requestParams.put(AppConstants.ANSWER_DESC, responseHashMap);

        if (isOnline()) {
            new SendFeedbackAsyncTask().execute(requestParams);
        } else {
            databaseHandler.addResponse(requestParams);
            Toast.makeText(SurveyScreen.this, "Can't connect to server now! Sync data to save it.", Toast.LENGTH_LONG).show();
        }
        showFeedback();
//        ShowProgress(false);
    }

    void showFeedback() {
        Log.e("Feedback", "showFeedback()");
        Intent intent = new Intent(SurveyScreen.this, FeedbackActivity.class);
        intent.putExtra(AppConstants.AVERAGE, average);
        intent.putExtra(AppConstants.AVERAGE_RATING, averageFeedback);
        startActivityForResult(intent, 0);
    }

    private class SendFeedbackAsyncTask extends AsyncTask<HashMap, Void, Void> {

        @Override
        protected Void doInBackground(HashMap... requestParams) {
            ObjectMapper mapperObj = new ObjectMapper();
            mapperObj.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            String jsonResp = "";
            try {
                jsonResp = mapperObj.writeValueAsString(requestParams[0]);
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

            } catch (UnsupportedEncodingException e) {

            } catch (IOException e) {
                Log.e("MYAPP", "exception", e);
            }
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    void ShowProgress(Boolean show) {
        if (show) {
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();
        } else {
            pDialog.cancel();
        }
    }

    void ParseWSResponse(JSONObject response) {
        int answer_id = 0;
        boolean needDesc = false;
        JSONArray categories = response.optJSONArray(AppConstants.JSON_CATEGORY_NAME);
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
                    Answers.add(0);
                    responseDescription.add("");
                    totalQuestionPosition = Answers.size() - 1;
                    break;
                }
            }
        }
        JSONArray Answersarr = response.optJSONArray(AppConstants.JSON_ANSWERS);
        for (int Answerarrindex = 0; Answerarrindex < Answersarr.length(); Answerarrindex++) {
            EmojiModel emojiModel = new EmojiModel();
            JSONObject ans_Obj = Answersarr.optJSONObject(Answerarrindex);
            emojiModel.setEmojiId(ans_Obj.optInt(AppConstants.JSON_ANSWER_ID));
            emojiModel.setEmojiText(ans_Obj.optString(AppConstants.JSON_ANSWER_TXT));
            emojiModel.setEmojiRating(ans_Obj.optInt(AppConstants.JSON_ANSWER_RATING));
            answerEmojiArrayList.add(emojiModel);
        }
//        new DatabaseHandler(SurveyScreen.this).addQuestions(surveyQuestionsList, PatientType);
        UpdateQuestion(QuestionNumber, "");
    }

    void UpdateQuestion(int questionNumber, String emojiText) {

        //Checking whether it is an already answered question or not
        if (Answers.size() != 0) {
            String MalayalamQuestion, EnglishQuestion;
            SurveyQuestions surveyQuestions = surveyQuestionsList.get(questionNumber);
            MalayalamQuestion = surveyQuestions.getMalayalamQuestion();
            EnglishQuestion = surveyQuestions.getEnglishQuestion();

            showResponse(surveyQuestions.getResponseid());

            MalayalamQuestionTextView.setText(MalayalamQuestion);
            EnglishQuestionTextView.setText(EnglishQuestion);

            MalayalamQuestionTextView.startAnimation(questionSlideDownAnimation);
            EnglishQuestionTextView.startAnimation(questionSlideDownAnimation);

            clearAllSelectedEmoji(excellentTextView);
            clearAllSelectedEmoji(veryGoodTextView);
            clearAllSelectedEmoji(goodTextView);
            clearAllSelectedEmoji(fairTextView);
            clearAllSelectedEmoji(poorTextView);
            clearAllSelectedEmoji(less15minTextView);
            clearAllSelectedEmoji(between15and30TextView);
            clearAllSelectedEmoji(between30and1hrTextView);
            clearAllSelectedEmoji(above1hrTextView);
            clearAllSelectedEmoji(notRelevantTextView);
            if (Answers.get(questionNumber) != 0) {
                if (!emojiText.equals("")) {
                    showSelectedEmoji(emojiText);
                }
            } else {
                showSelectedEmoji(emojiText);
            }
        }
//        ShowProgress(false);
    }

    void showResponse(boolean needDesc) {
        if (!needDesc) {
            if (answerEmojiArrayList != null) {
                if (answerEmojiArrayList.size() != 0) {
                    for (EmojiModel emojiModel : answerEmojiArrayList) {
                        if (emojiModel.getEmojiRating() == AppConstants.EXCELLENT_EMOJI_RATING_NUMBER) {
                            linearLayoutexcellent.setVisibility(View.VISIBLE);
                            visibilityGone();
                        } else if (emojiModel.getEmojiRating() == AppConstants.VERY_GOOD_EMOJI_RATING_NUMBER) {
                            linearLayoutverygood.setVisibility(View.VISIBLE);
                            visibilityGone();
                        } else if (emojiModel.getEmojiRating() == AppConstants.GOOD_EMOJI_RATING_NUMBER) {
                            linearLayoutgood.setVisibility(View.VISIBLE);
                            visibilityGone();
                        } else if (emojiModel.getEmojiRating() == AppConstants.FAIR_EMOJI_RATING_NUMBER) {
                            linearLayoutfair.setVisibility(View.VISIBLE);
                            visibilityGone();
                        } else if (emojiModel.getEmojiRating() == AppConstants.POOR_EMOJI_RATING_NUMBER) {
                            linearLayoutpoor.setVisibility(View.VISIBLE);
                            visibilityGone();
                        }
                    }
                    naLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        } else if (needDesc) {
            linearLayoutexcellent.setVisibility(View.GONE);
            linearLayoutverygood.setVisibility(View.GONE);
            linearLayoutgood.setVisibility(View.GONE);
            linearLayoutfair.setVisibility(View.GONE);
            linearLayoutpoor.setVisibility(View.GONE);
            less15minLinearLayout.setVisibility(View.VISIBLE);
            between15and30LinearLayout.setVisibility(View.VISIBLE);
            between30and1hrLinearLayout.setVisibility(View.VISIBLE);
            above1hrLinearLayout.setVisibility(View.VISIBLE);
            naLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    public void visibilityGone() {
        less15minLinearLayout.setVisibility(View.GONE);
        between15and30LinearLayout.setVisibility(View.GONE);
        between30and1hrLinearLayout.setVisibility(View.GONE);
        above1hrLinearLayout.setVisibility(View.GONE);
        naLinearLayout.setVisibility(View.GONE);
    }

    //Showing selected emojis
    void showSelectedEmoji(String emojiText) {
        if (emojiText.equals(AppConstants.EXCELLENT)) {
            excellentTextView.setTextSize(20);
            excellentTextView.setTextColor(getResources().getColor(R.color.title_blue));
        } else if (emojiText.equals(AppConstants.VERY_GOOD)) {
            veryGoodTextView.setTextSize(20);
            veryGoodTextView.setTextColor(getResources().getColor(R.color.title_blue));
        } else if (emojiText.equals(AppConstants.GOOD)) {
            goodTextView.setTextSize(20);
            goodTextView.setTextColor(getResources().getColor(R.color.title_blue));
        } else if (emojiText.equals(AppConstants.FAIR)) {
            fairTextView.setTextSize(20);
            fairTextView.setTextColor(getResources().getColor(R.color.title_blue));
        } else if (emojiText.equals(AppConstants.POOR)) {
            poorTextView.setTextSize(20);
            poorTextView.setTextColor(getResources().getColor(R.color.title_blue));
        } else if (emojiText.equals(AppConstants.LESS_THAN_15)) {
            less15minTextView.setTextSize(20);
            less15minTextView.setTextColor(getResources().getColor(R.color.title_blue));
        } else if (emojiText.equals(AppConstants.BETWEEN_15_AND_30)) {
            between15and30TextView.setTextSize(20);
            between15and30TextView.setTextColor(getResources().getColor(R.color.title_blue));
        } else if (emojiText.equals(AppConstants.BETWEEN_30_AND_1HR)) {
            between30and1hrTextView.setTextSize(20);
            between30and1hrTextView.setTextColor(getResources().getColor(R.color.title_blue));
        } else if (emojiText.equals(AppConstants.ABOVE_1HR)) {
            above1hrTextView.setTextSize(20);
            above1hrTextView.setTextColor(getResources().getColor(R.color.title_blue));
        } else if (emojiText.equals(AppConstants.NOT_RELEVANT)) {
            notRelevantTextView.setTextSize(20);
            notRelevantTextView.setTextColor(getResources().getColor(R.color.title_blue));
        }
    }

    //Clearing selected emojis
    void clearAllSelectedEmoji(TextView emojiTxtviews) {
        emojiTxtviews.setTextColor(getResources().getColor(R.color.feedback_text));
        emojiTxtviews.setTextSize(14);
    }

    public void inwokeWS() throws JSONException {
        SurveyRestClient.get(PatientType, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                ParseWSResponse(response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }
}
