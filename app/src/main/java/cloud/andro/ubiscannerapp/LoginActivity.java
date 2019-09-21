package cloud.andro.ubiscannerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText SolID, EmployeeID, MobileNumber, Password;
    String enteredSolID, enteredEmployeeID, enteredEmployeeMobileNo, enteredPassword, Temppasswordchangestatus;
    Button login;
    JSONObject jsonObject;
    private static final int REQUEST_READ_PHONE_STATE = 0;
    int success;
    String IMEI;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PROFILE = "data";
    private static final String url_emailcheck = "http://192.168.100.165:90/PhpFiles/UBI/employeecheck.php";

    Handler handler;
    Boolean isSystemUiShown;
    int SYSTEM_UI_HIDE_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SolID = (EditText) findViewById(R.id.branch);
        EmployeeID = (EditText) findViewById(R.id.employeeid);
        MobileNumber = (EditText) findViewById(R.id.mobileno);
        Password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.imageView7);

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


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enteredSolID = SolID.getText().toString();
                enteredEmployeeID = EmployeeID.getText().toString();
                enteredEmployeeMobileNo = MobileNumber.getText().toString();
                enteredPassword = Password.getText().toString();

                //-----validations---------

                if (TextUtils.isEmpty(enteredSolID)) {
                    //email.setError("Invalid Email");
                    Toast.makeText(LoginActivity.this, "Please Enter Branch ID", Toast.LENGTH_LONG).show();

                }

                if (TextUtils.isEmpty(enteredEmployeeID)) {
                    //email.setError("Invalid Email");
                    Toast.makeText(LoginActivity.this, "Please Enter Employee ID", Toast.LENGTH_LONG).show();

                }

                if (TextUtils.isEmpty(enteredEmployeeMobileNo)) {
                    //email.setError("Invalid Email");
                    Toast.makeText(LoginActivity.this, "Please Enter Employee Email", Toast.LENGTH_LONG).show();

                }

                /*HashMap<String, String> loginData = new HashMap<>();
                loginData.put("email", enteredUsername);
                loginData.put("password", enteredPassword);
                // request authentication with remote server4
                AsyncDataClass asyncRequestObject = new AsyncDataClass();
                asyncRequestObject.execute(serverUrl, enteredUsername, enteredPassword);*/
                new CheckEmail().execute();

            }
        });

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
        }

        TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {

            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (mTelephony.getPhoneCount() == 2) {
                    IMEI = mTelephony.getImei(0);
                }else{
                    IMEI = mTelephony.getImei();
                }
            }else{
                if (mTelephony.getPhoneCount() == 2) {
                    IMEI = mTelephony.getDeviceId(0);
                } else {
                    IMEI = mTelephony.getDeviceId();
                }
            }
        } else {
            IMEI = mTelephony.getDeviceId();
        }

        Toast.makeText(LoginActivity.this, "IMEI Number is: "+IMEI, Toast.LENGTH_LONG).show();


        //getDeviceIMEI();

    }

   /* public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            deviceUniqueIdentifier = tm.getDeviceId();
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }
*/
    class CheckEmail extends AsyncTask<String, String, String> {


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
            httpParams.put("sol_id", enteredSolID);
            httpParams.put("employee_id", enteredEmployeeID);
            httpParams.put("mobile_no", enteredEmployeeMobileNo);
            httpParams.put("password", enteredPassword);

            jsonObject = httpJsonParser.makeHttpRequest(
                    url_emailcheck, "POST", httpParams);
            try {
                success = jsonObject.getInt(TAG_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            runOnUiThread(new Runnable() {
                public void run() {
                    JSONObject user;
                    if (success == 1) {

                        try {
                            user = jsonObject.getJSONObject(TAG_PROFILE);
                            Temppasswordchangestatus = user.getString("ispasswordchange");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //ActiveJobs = user.getString(TAG_ACTIVEJOBS);
                        //Display success message

                        SharedPreferences pref = getSharedPreferences("loginData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("mobile_number", enteredEmployeeMobileNo);
                        editor.putString("employee_id", enteredEmployeeID);
                        editor.putString("password_change_status", Temppasswordchangestatus);

                        editor.commit();

                        Toast.makeText(LoginActivity.this,"Success",Toast.LENGTH_SHORT).show();

                        if("1".equals(Temppasswordchangestatus)){
                            Intent i = new Intent(LoginActivity.this, MobileOTP.class);
                            startActivity(i);
                        }else{
                            Intent i = new Intent(LoginActivity.this, ChangePasswordScreen.class);
                            startActivity(i);
                        }



                    } else {

                    }
                }
            });
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
