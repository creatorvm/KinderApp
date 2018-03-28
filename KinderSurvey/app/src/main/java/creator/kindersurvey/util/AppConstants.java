package creator.kindersurvey.util;

/**
 * Created by Development-2 on 15-11-2017.
 */

public interface AppConstants {
    String PREF = "sharedpref";
    String USERNAME = "username";
    String PASSWORD = "password";
    String PATIENT_NAME = "name";
    String PATIENT_COMMENTS = "patient_comments";
    String URL = "http://192.168.10.231:8080/api/survey/";
    String FEEDBACK_POST_URL = "http://192.168.10.231:8080/api/survey";
    String DATE_FORMAT = "yyyy-MM-dd";
    int INPUT_LENGTH = 255;

    int EXCELLENT_RATING = 100;
    int VERY_GOOD_RATING = 80;
    int GOOD_RATING = 60;
    int FAIR_RATING = 40;
    int POOR_RATING = 20;

    String EXCELLENT = "Excellent";
    String VERY_GOOD = "Very Good";
    String GOOD = "Good";
    String FAIR = "Fair";
    String POOR = "Poor";

    int EXCELLENT_EMOJI_RATING_NUMBER = 5;
    int VERY_GOOD_EMOJI_RATING_NUMBER = 4;
    int GOOD_EMOJI_RATING_NUMBER = 3;
    int FAIR_EMOJI_RATING_NUMBER = 2;
    int POOR_EMOJI_RATING_NUMBER = 1;
    long ONE_MSEC = 1000;
    long TWO_MSEC = 2000;

    String AVERAGE = "average";
    String AVERAGE_RATING = "averageRating";
    String PATIENT_TYPE = "patientType";
    String IP_PATIENT = "1";
    String OP_PATIENT = "2";

    int PASSWORD_LENGTH = 4;

    String CONTENT_TYPE = "Content-Type";
    String JSON_CONTENT = "application/json";
    String FEEDBACK = "feedback";
    String UHID_NUMBER = "op";
    String UHID = "uhid";
    String ROOM_NUMBRER = "roomNum";
    String CONTACT_NUMBER = "phone";
    String EMAIL_ID = "email";
    String ADMINSSION_DATE = "admission";
    String DISCHARGE_DATE = "discharge";
    String ACCEPT = "Accept";
    String AUTHORISATION = "authorization";
    String AUTHORISATION_VALUE = "Basic YWRtaW46YWRtaW4=";
    String FEEDBACK_POST_METHOD = "POST";
    String FEEDBACK_POST_USER = "user";
    String FEEDBACK_POST_ANSWERS = "answers";
    String CHAR_SET = "UTF-8";
    String JSON_CATEGORY_NAME = "catg";
    String JSON_QUESTIONS = "questions";
    String JSON_QUESTIONS_ID = "id";
    String JSON_QUESTIONS_TEXT = "text";
    String JSON_MALAYALAM_QUESTIONS = "mlang";
    String JSON_ANSWERS = "response";
    String JSON_ANSWER_ID = "id";
    String JSON_ANSWER_TXT = "text";
    String JSON_ANSWER_RATING = "rating";
    int MYEAR = 2017;
    int MDAY = 1;

    String NEED_DESC = "needDesc";
    String ANSWER_DESC = "answersDesc";
    String ID_ARRAY = "idarray";

    // Database Version
    int DATABASE_VERSION = 1;

    // Database Name
    String DATABASE_NAME = "kindersurvey";

    // Contacts table name
    String TABLE_QUESTIONS = "questions";
    String TABLE_RESPONSE = "response";
    String TABLE_QUESTION_RESPONSE = "question_response";

    //Table indices
    String ID = "id";
    String ENG_QUES_TEXT = "eng_que_text";
    String MAL_QUES_TEXT = "mal_que_text";
    String NEED_DESCR = "need_descr";
    String QUES_ID = "que_id";
    String RES_ID = "res_id";
    String CREATE_DATE = "create_date";
    String UPDATE_DATE = "update_date";
    String RES_DESCR = "res_descr";
    String RES_TEXT = "text";
    String RES_RATING = "rating";

    String LESS_THAN_15 = "0-15";
    String BETWEEN_15_AND_30 = "15-30";
    String BETWEEN_30_AND_1HR = "30-1 hr";
    String ABOVE_1HR = "1 hr & Above";
    String NOT_RELEVANT = "NA";
}
