package creator.kindersurvey.Login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import creator.kindersurvey.Commons.CommonMethods;
import creator.kindersurvey.PatientStatus.PatientStatusActivity;
import creator.kindersurvey.R;
import creator.kindersurvey.util.AppConstants;

public class Login extends AppCompatActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */

    SharedPreferences sharedPreferences = null;


    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "admin:admin"
    };

    private Login.UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Creating private sharedpreference
        sharedPreferences = getSharedPreferences(AppConstants.PREF, MODE_PRIVATE);
        if (!sharedPreferences.getString(AppConstants.USERNAME, "").equals("") && !sharedPreferences.getString(AppConstants.PASSWORD, "").equals("")) {
            Intent intent = new Intent(Login.this, PatientStatusActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_login);

            // Set up the login form.
            mUsernameView = (EditText) findViewById(R.id.username);
            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
            mSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        CommonMethods commonMethods = new CommonMethods();

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (commonMethods.validatePassword(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
        }

        if (commonMethods.validateUsername(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
        }

        if ((!commonMethods.validatePassword(password)) && (!commonMethods.validateUsername(username))) {
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(AppConstants.TWO_MSEC);
            } catch (InterruptedException e) {
                return false;
            }
            Boolean CredentialsMatch = false;
            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");


                if (pieces[0].equals(mUsername)) {
                    if (pieces[1].equals(mPassword)) {
                        CredentialsMatch = true;
                    } else {
                        CredentialsMatch = false;
                    }
                } else {
                    CredentialsMatch = false;
                }
                return CredentialsMatch;
            }
            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //Storing credentials to sharedpreference
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(AppConstants.USERNAME, mUsername);
                editor.putString(AppConstants.PASSWORD, mPassword);
                editor.apply();

                Intent HomeIntent = new Intent(Login.this, PatientStatusActivity.class);
                startActivity(HomeIntent);
                finish();
            } else {
                Toast.makeText(Login.this, "Your Login credentials mismatch...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
