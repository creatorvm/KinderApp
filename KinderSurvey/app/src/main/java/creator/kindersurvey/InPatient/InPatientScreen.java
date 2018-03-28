package creator.kindersurvey.InPatient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import creator.kindersurvey.Commons.CommonMethods;
import creator.kindersurvey.R;
import creator.kindersurvey.Survey.SurveyScreen;
import creator.kindersurvey.util.AppConstants;

public class InPatientScreen extends AppCompatActivity {

    EditText PatientNameET, RoomNoET, UhidET, ContactNumberET, EmailAddressET;
    DatePicker AdmissionDatePicker, DischargeDatePicker;

    AwesomeValidation awesomeValidation;
    SharedPreferences sharedPreferences = null;
    String PatientName = "";
    String admissionDate = "";
    String dischargeDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_patient_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PatientNameET = (EditText) findViewById(R.id.inpatient_patient_name_ET);
        RoomNoET = (EditText) findViewById(R.id.inpatient_room_no_ET);
        UhidET = (EditText) findViewById(R.id.inpatient_uhid_ET);
        ContactNumberET = (EditText) findViewById(R.id.inpatient_contact_number_ET);
        EmailAddressET = (EditText) findViewById(R.id.inpatient_email_address_ET);
        AdmissionDatePicker = (DatePicker) findViewById(R.id.inpatient_admission_DP);
        DischargeDatePicker = (DatePicker) findViewById(R.id.inpatient_discharge_DP);
        Button GoButton = (Button) findViewById(R.id.inpatient_start_survey_button);
        DischargeDatePicker.setMinDate(System.currentTimeMillis() - 1000);
        AdmissionDatePicker.updateDate(2016, 01, 01);
        AdmissionDatePicker.setMaxDate(System.currentTimeMillis() - 1000);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        sharedPreferences = getSharedPreferences(AppConstants.PREF, MODE_PRIVATE);
        GoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getvaluesFromForm();
                submitForm();
            }
        });

        //set minimum admision date to 2016
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        AdmissionDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                int mmonth = month + 1;
                if (year < AppConstants.MYEAR) {
                    datePicker.updateDate(AppConstants.MYEAR, mmonth, AppConstants.MDAY);
                }
                if (month < mmonth && year == AppConstants.MYEAR) {
                    datePicker.updateDate(AppConstants.MYEAR, mmonth, AppConstants.MDAY);
                }
                if (dayOfMonth < AppConstants.MDAY && year == AppConstants.MYEAR
                        && month == mmonth) {
                    datePicker.updateDate(AppConstants.MYEAR, mmonth, AppConstants.MDAY);
                }
                admissionDate = year + "-" + mmonth + "-" + dayOfMonth;
            }
        });

        DischargeDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                int mmonth = month + 1;
                if (year < AppConstants.MYEAR) {
                    datePicker.updateDate(AppConstants.MYEAR, mmonth, AppConstants.MDAY);
                }
                if (month < mmonth && year == AppConstants.MYEAR) {
                    datePicker.updateDate(AppConstants.MYEAR, mmonth, AppConstants.MDAY);
                }
                if (dayOfMonth < AppConstants.MDAY && year == AppConstants.MYEAR
                        && month == mmonth) {
                    datePicker.updateDate(AppConstants.MYEAR, mmonth, AppConstants.MDAY);
                }
                dischargeDate = year + "-" + mmonth + "-" + dayOfMonth;
            }
        });
    }


    void getvaluesFromForm() {
        awesomeValidation.addValidation(this, R.id.inpatient_patient_name_ET, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.error_invalid_patientname);
        awesomeValidation.addValidation(this, R.id.inpatient_room_no_ET, "^[1-9]\\d*$", R.string.error_invalid_number);
    }

    private void submitForm() {
        //first validate the form then move ahead
        //if this becomes true that means validation is successfull
        CommonMethods commonMethods = new CommonMethods();
        if (awesomeValidation.validate()) {
            PatientName = PatientNameET.getText().toString();
            String RoomNo = RoomNoET.getText().toString();
            String UHIDNum = UhidET.getText().toString();
            String ContactNumber = ContactNumberET.getText().toString();
            String EmailAddress = EmailAddressET.getText().toString();
            String date = new SimpleDateFormat(AppConstants.DATE_FORMAT).format(new Date());
            if (admissionDate.equals("")) {
                admissionDate = date;
            }

            if (dischargeDate.equals("")) {
                dischargeDate = date;
            }

            if (UHIDNum.equals("")) {
                UhidET.setError("This field is mandatory");
            } else {
                if (ContactNumber.equals("") && EmailAddress.equals("")) {
                    showConfirmDialogue(RoomNo, UHIDNum, ContactNumber, EmailAddress, PatientName, admissionDate, dischargeDate);
                } else if (ContactNumber.equals("") && (!EmailAddress.equals(""))) {
                    if (commonMethods.ValidateEmail(EmailAddress)) {
                        showConfirmDialogue(RoomNo, UHIDNum, ContactNumber, EmailAddress, PatientName, admissionDate, dischargeDate);
                    } else {
                        EmailAddressET.setError("Check this field");
                    }
                } else if ((!ContactNumber.equals("")) && EmailAddress.equals("")) {
                    if (commonMethods.isValidPhoneNumber(ContactNumber)) {
                        showConfirmDialogue(RoomNo, UHIDNum, ContactNumber, EmailAddress, PatientName, admissionDate, dischargeDate);
                    } else {
                        ContactNumberET.setError("Check this field");
                    }
                } else if ((!ContactNumber.equals("")) && (!EmailAddress.equals(""))) {
                    if (commonMethods.isValidPhoneNumber(ContactNumber)) {
                        if (commonMethods.ValidateEmail(EmailAddress)) {
                            setUpSurveyScreen(RoomNo, UHIDNum, ContactNumber, EmailAddress, PatientName, admissionDate, dischargeDate);
                        } else {
                            EmailAddressET.setError("Check this field");
                        }
                    } else {
                        ContactNumberET.setError("Check this field");
                    }
                }
            }
        }
    }

    void setUpSurveyScreen(String roomNumber, String uhidNumber, String contactNumber, String emailId, String patientName, String admissionDate, String dischargeDate) {
        Intent intent = new Intent(InPatientScreen.this, SurveyScreen.class);
        intent.putExtra(AppConstants.PATIENT_TYPE, AppConstants.IP_PATIENT);
        intent.putExtra(AppConstants.PATIENT_NAME, patientName);
        intent.putExtra(AppConstants.UHID, uhidNumber);
        intent.putExtra(AppConstants.ROOM_NUMBRER, roomNumber);
        intent.putExtra(AppConstants.CONTACT_NUMBER, contactNumber);
        intent.putExtra(AppConstants.EMAIL_ID, emailId);
        intent.putExtra(AppConstants.ADMINSSION_DATE, admissionDate);
        intent.putExtra(AppConstants.DISCHARGE_DATE, dischargeDate);
        startActivity(intent);
        finish();
    }

    void showConfirmDialogue(final String roomNumber, final String uhidNumber, final String contactNumber, final String emailId, final String patientName, final String admissionDate, final String dischargeDate) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(InPatientScreen.this);

        // Setting Dialog Title
        alertDialog.setTitle("Confirm...");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure to leave these fields??");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.logo);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // Write your code here to invoke YES event
//                sharedPreferences.edit().putString(AppConstants.PATIENT_NAME, PatientName).apply();
                setUpSurveyScreen(roomNumber, uhidNumber, contactNumber, emailId, patientName, admissionDate, dischargeDate);
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

}
