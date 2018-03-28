package creator.kindersurvey.feedback;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import creator.kindersurvey.R;
import creator.kindersurvey.Survey.SurveyScreen;
import creator.kindersurvey.util.AppConstants;
import pl.droidsonroids.gif.GifImageView;


public class FeedbackActivity extends Activity {

    GifImageView excellentGifImageView  = null;
    GifImageView veryGoodGifImageView   = null;
    GifImageView goodGifImageView       = null;
    GifImageView fairGifImageView       = null;
    GifImageView poorGifImageView = null;
    RatingBar feedbackRatingBar = null;
    TextView feedbackTextView = null;
    ArcProgress percentageArcProgress = null;
    Button closeButton = null;
    LinearLayout linearLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_feedback);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        excellentGifImageView = findViewById(R.id.excellentGifImageView);
        linearLayout = findViewById(R.id.linearLayout);
        veryGoodGifImageView = findViewById(R.id.veryGoodGifImageView);
        goodGifImageView = findViewById(R.id.goodGifImageView);
        fairGifImageView = findViewById(R.id.fairGifImageView);
        poorGifImageView = findViewById(R.id.poorGifImageView);
        feedbackRatingBar = findViewById(R.id.feedbackRatingBar);
        feedbackTextView = findViewById(R.id.averageFeedbackTextView);
        percentageArcProgress = findViewById(R.id.arc_progress);
        closeButton = findViewById(R.id.feedbackCloseButton);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        feedbackRatingBar.setEnabled(false);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK, null);
                finish();
            }
        });

        String average = getIntent().getExtras().getString(AppConstants.AVERAGE, "No value");
        int averageRating = getIntent().getExtras().getInt(AppConstants.AVERAGE_RATING, 0);

        feedbackTextView.setText(average);
        percentageArcProgress.setProgress(averageRating);
        percentageArcProgress.setRight(50);

        LayerDrawable stars = (LayerDrawable) feedbackRatingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.golden), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.golden), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.golden), PorterDuff.Mode.SRC_ATOP);

        if (average.equals(AppConstants.EXCELLENT)) {
            excellentGifImageView.setVisibility(View.VISIBLE);
            feedbackRatingBar.setRating(AppConstants.EXCELLENT_EMOJI_RATING_NUMBER);
        } else if (average.equals(AppConstants.VERY_GOOD)) {
            veryGoodGifImageView.setVisibility(View.VISIBLE);
            feedbackRatingBar.setRating(AppConstants.VERY_GOOD_EMOJI_RATING_NUMBER);
        } else if (average.equals(AppConstants.GOOD)) {
            goodGifImageView.setVisibility(View.VISIBLE);
            feedbackRatingBar.setRating(AppConstants.GOOD_EMOJI_RATING_NUMBER);
        } else if (average.equals(AppConstants.FAIR)) {
            fairGifImageView.setVisibility(View.VISIBLE);
            feedbackRatingBar.setRating(AppConstants.FAIR_EMOJI_RATING_NUMBER);
        } else if (average.equals(AppConstants.POOR)) {
            poorGifImageView.setVisibility(View.VISIBLE);
            feedbackRatingBar.setRating(AppConstants.POOR_EMOJI_RATING_NUMBER);
        }
    }

    @Override
    public void onBackPressed() {

    }
}
