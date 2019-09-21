package cloud.andro.ubiscannerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangePasswordScreen extends AppCompatActivity {

    TextView ScrollableText1, ScrollableText2;
    ArrayList<HashMap<String, String>> arrayList;
    private static final String url_adverts = "http://192.168.100.165:90/PhpFiles/UBI/advertisementtext.php";
    private static final String url_edit = "http://192.168.100.165:90/PhpFiles/UBI/changepassword.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PROFILE = "data";
    int success;
    private static final String TAG_EMPLOYEEID = "employee_id";
    private static final String TAG_PASSWORD = "password";
    Handler handler;
    HashMap<String, String> array = new HashMap<>();
    Boolean isSystemUiShown;
    String text="";
    int SYSTEM_UI_HIDE_DELAY = 2000;
    boolean temp;
    String TempEmployeeID, TempPassword;

    Button SubmitButton;

    EditText NewPassword, RetypePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_screen);

        SharedPreferences pref = getSharedPreferences("loginData", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        TempEmployeeID = pref.getString("employee_id", null);

        NewPassword = (EditText) findViewById(R.id.et_newpassword);
        RetypePassword = (EditText) findViewById(R.id.et_retypepassword);

        SubmitButton = (Button) findViewById(R.id.imageView7);

        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //validate();


                if(validate()){
                    Toast.makeText(ChangePasswordScreen.this,"Password matching",Toast.LENGTH_SHORT).show();
                    new ChangePassword().execute();
                     startActivity(new Intent(ChangePasswordScreen.this, MobileOTP.class));
                }else{
                    Toast.makeText(ChangePasswordScreen.this,"Password Not matching",Toast.LENGTH_SHORT).show();

                }
            }
        });

        handler = new Handler();
        arrayList = new ArrayList<>(8);

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

        //jobsData=new ArrayList<>();
        new Adverts().execute();

        ScrollableText1 = findViewById(R.id.scrollableText1);
        //ScrollableText2 = findViewById(R.id.scrollableText2);

        ScrollableText1.setSelected(true);
        //ScrollableText2.setSelected(true);
    }

    public boolean validate() {
        temp=true;
        String pass=NewPassword.getText().toString();
        String cpass=RetypePassword.getText().toString();

        if(!pass.equals(cpass)){
            Toast.makeText(ChangePasswordScreen.this,"Password Not matching",Toast.LENGTH_SHORT).show();
            temp=false;
        }else{
            TempPassword = pass;
        }
        return temp;
    }

    private class Adverts extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display progress bar

        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    url_adverts , "POST", null);
            try {

                int success = jsonObject.getInt(TAG_SUCCESS);
                JSONObject user;


                if (success == 1) {

                    JSONArray adverts = jsonObject.getJSONArray(TAG_PROFILE);

                    for (int i = 0; i < adverts.length(); i++) {

                        JSONObject c = adverts.getJSONObject(i);

                        String id = c.getString("advertisement_text");

                        text += id +", ";

                    }


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {

            runOnUiThread(new Runnable() {
                public void run() {
                    //Populate the Edit Texts once the network activity is finished executing
                }
            });

            ScrollableText1.setText("");

            ScrollableText1.setText(text);
            ScrollableText1.setSelected(true);


        }


    }

    class ChangePassword extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display progress bar
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            //Populating request parameters

            httpParams.put(TAG_EMPLOYEEID, TempEmployeeID);
            httpParams.put(TAG_PASSWORD, TempPassword);

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    url_edit, "GET", httpParams);
            try {
                success = jsonObject.getInt(TAG_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
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
