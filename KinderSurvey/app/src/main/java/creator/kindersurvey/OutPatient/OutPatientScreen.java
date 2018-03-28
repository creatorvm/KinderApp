package creator.kindersurvey.OutPatient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import creator.kindersurvey.Commons.CommonMethods;
import creator.kindersurvey.R;
import creator.kindersurvey.Survey.SurveyScreen;
import creator.kindersurvey.util.AppConstants;


public class OutPatientScreen extends AppCompatActivity {

    EditText PatientNameET, UhidET, ContactNumberET, AddressET;
    AwesomeValidation awesomeValidation;
    SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_patient_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Button GoButton = (Button) findViewById(R.id.outpatient_startsurvey_Button);
        GoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getvaluesFromForm();
                navigateToSurvey();
            }
        });

        PatientNameET = (EditText) findViewById(R.id.outpatient_patientname_ET);
        UhidET = (EditText) findViewById(R.id.outpatient_uhid_ET);
        ContactNumberET = (EditText) findViewById(R.id.outpatient_patientcontact_ET);
        AddressET = (EditText) findViewById(R.id.outpatient_patientaddress_ET);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        sharedPreferences = getSharedPreferences(AppConstants.PREF, MODE_PRIVATE);
    }

    void getvaluesFromForm() {

        awesomeValidation.addValidation(this, R.id.outpatient_patientname_ET, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.error_invalid_patientname);
//        awesomeValidation.addValidation(this, R.id.outpatient_patientcontact_ET, "^[2-9]{2}[0-9]{8}$", R.string.error_invalid_phonenumber);
    }

    void navigateToSurvey() {
        CommonMethods commonMethods = new CommonMethods();
        String PatientName = PatientNameET.getText().toString();
        String UHIDNum = UhidET.getText().toString();
        String ContactNumber = ContactNumberET.getText().toString();
        String Address = AddressET.getText().toString();
        if (awesomeValidation.validate()) {
            if (UHIDNum.equals("")) {
                UhidET.setError("This field is mandatory");
            } else {
                if (ContactNumber.equals("") && Address.equals("")) {
                    showConfirmDialogue(PatientName, UHIDNum, ContactNumber, Address);
                } else if (ContactNumber.equals("") && (!Address.equals(""))) {
                    if (commonMethods.ValidateEmail(Address)) {
                        showConfirmDialogue(PatientName, UHIDNum, ContactNumber, Address);
                    } else {
                        AddressET.setError("Check this field");
                    }
                } else if ((!ContactNumber.equals("")) && Address.equals("")) {
                    if (commonMethods.isValidPhoneNumber(ContactNumber)) {
                        showConfirmDialogue(PatientName, UHIDNum, ContactNumber, Address);
                    } else {
                        ContactNumberET.setError("Check this field");
                    }
                } else if ((!ContactNumber.equals("")) && (!Address.equals(""))) {
                    if (commonMethods.isValidPhoneNumber(ContactNumber)) {
                        if (commonMethods.ValidateEmail(Address)) {
                            setUpSurveyScreen(PatientName, UHIDNum, ContactNumber, Address);
                        } else {
                            AddressET.setError("Check this field");
                        }
                    } else {
                        ContactNumberET.setError("Check this field");
                    }
                }
            }
        }
    }

    void setUpSurveyScreen(String patientName, String uhidNumber, String contactNumber, String email) {
        Intent intent = new Intent(OutPatientScreen.this, SurveyScreen.class);
        intent.putExtra(AppConstants.PATIENT_TYPE, AppConstants.OP_PATIENT);
        intent.putExtra(AppConstants.PATIENT_NAME, patientName);
        intent.putExtra(AppConstants.UHID, uhidNumber);
        intent.putExtra(AppConstants.CONTACT_NUMBER, contactNumber);
        intent.putExtra(AppConstants.EMAIL_ID, email);
        startActivity(intent);
        finish();
    }

    void showConfirmDialogue(final String patientName, final String uhidNumber, final String contactNumber, final String email) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OutPatientScreen.this);

        // Setting Dialog Title
        alertDialog.setTitle("Confirm...");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure to leave these fields??");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.logo);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                setUpSurveyScreen(patientName, uhidNumber, contactNumber, email);
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
