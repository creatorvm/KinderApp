package creator.kindersurvey.Survey;

/**
 * Created by CreatorJeslin on 14/11/17.
 */

public class SurveyQuestions {
    private int id;
    private String MalayalamQuestion;
    private String EnglishQuestion;
    private int answerid;
    private boolean responseid;
    private String answer_value;

    public SurveyQuestions() {
    }

    public SurveyQuestions(int id, String malayalamQuestion, String englishQuestion) {
        this.id = id;
        this.MalayalamQuestion = malayalamQuestion;
        this.EnglishQuestion = englishQuestion;
    }

    public String getMalayalamQuestion() {
        return MalayalamQuestion;
    }

    public SurveyQuestions(int id, String malayalamQuestion, String englishQuestion, int answerid, boolean responseid) {
        this.id = id;
        MalayalamQuestion = malayalamQuestion;
        EnglishQuestion = englishQuestion;
        this.answerid = answerid;
        this.responseid = responseid;
    }

    public void setMalayalamQuestion(String malayalamQuestion) {
        MalayalamQuestion = malayalamQuestion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnglishQuestion() {
        return EnglishQuestion;
    }

    public void setEnglishQuestion(String englishQuestion) {
        EnglishQuestion = englishQuestion;
    }

    public int getAnswerid() {
        return answerid;
    }

    public void setAnswerid(int answerid) {
        this.answerid = answerid;
    }

    public String getAnswer_value() {
        return answer_value;
    }

    public void setAnswer_value(String answer_value) {
        this.answer_value = answer_value;
    }

    public boolean getResponseid() {
        return responseid;
    }

    public void setResponseid(boolean responseid) {
        this.responseid = responseid;
    }
}
