package creator.kindersurvey.Commons;

import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import creator.kindersurvey.util.AppConstants;

/**
 * Created by CreatorJeslin on 13/11/17.
 */

public class CommonMethods {

    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    public boolean ValidateEmail(final String hex) {

        return Pattern.compile(EMAIL_PATTERN).matcher(hex).matches();


    }

    public Boolean isValidPhoneNumber(String patientPhone) {
        boolean flag = false;
        if (Patterns.PHONE.matcher(patientPhone).matches() && patientPhone.length() == 10) {
            flag = true;
        }
        return flag;
    }

    public Boolean isEmptyField(String value) {
        return (value.equals("")) || (value.replace(" ", "").equals(""));

    }

    public Boolean validatePassword(String password) {
        return (password.equals("")) || (password.replace(" ", "").equals("")) || (password.length() < AppConstants.PASSWORD_LENGTH);

    }

    public Boolean validateUsername(String username) {
        return (username.equals("")) || (username.replace(" ", "").equals(""));
    }
}
