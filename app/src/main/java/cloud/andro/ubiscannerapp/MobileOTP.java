package cloud.andro.ubiscannerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class MobileOTP extends AppCompatActivity {

    Button LoginButton;

    Handler handler;
    Boolean isSystemUiShown;
    int SYSTEM_UI_HIDE_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_otp);

        handler = new Handler();

        View decorView1 = getWindow().getDecorView();
        decorView1.setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            handler.postDelayed(checkSystemUiRunnable, SYSTEM_UI_HIDE_DELAY);
                            isSystemUiShown = true;
                        } else {
                            isSystemUiShown = false;
                        }
                    }
                });

        LoginButton = (Button) findViewById(R.id.imageView7);


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MobileOTP.this, AddCustomerNumber.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        //              for status bar
        //              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        //              | View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    private final Runnable checkSystemUiRunnable = new Runnable() {
        @Override
        public void run() {
            checkHideSystemUI();
        }
    };

    private void checkHideSystemUI() {
        // Check if system UI is shown and hide it by post a delayed handler
        if (isSystemUiShown) {
            hideSystemUI();
            handler.postDelayed(checkSystemUiRunnable, SYSTEM_UI_HIDE_DELAY);
        }
    }
}
