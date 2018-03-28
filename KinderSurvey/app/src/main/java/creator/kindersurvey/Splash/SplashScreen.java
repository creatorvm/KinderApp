package creator.kindersurvey.Splash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import creator.kindersurvey.Login.Login;
import creator.kindersurvey.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread loading = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    Intent main = new Intent(SplashScreen.this,Login.class);
                    startActivity(main);
                    finish();


                }

                catch (Exception e) {
                    e.printStackTrace();
                }

                finally {
                    finish();
                }
            }
        };

        loading.start();

    }
}
