package cloud.andro.ubiscannerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class SplashScreen extends AppCompatActivity {

    /*Duration of wait*/
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    Handler handler;
    Boolean isSystemUiShown;
    int SYSTEM_UI_HIDE_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the MainActivity. */
                Intent mainIntent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
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
