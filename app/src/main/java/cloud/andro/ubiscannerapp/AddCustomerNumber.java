package cloud.andro.ubiscannerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddCustomerNumber extends AppCompatActivity {

    EditText CustomerID;
    ImageView ValidationArrow;
    String TempCustomerID, TempCustomerName = null, TempAccountNumber, TempCustPOI, TempCustPOA;
    private static final String url_verification = "http://192.168.100.165:90/PhpFiles/UBI/verifycustomerid.php";
    private static final String url_adverts = "http://192.168.100.165:90/PhpFiles/UBI/advertisementtext.php";
    String text="";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PROFILE = "data";
    Button Verify;
    TextView ScrollableText1;
    Handler handler;
    Boolean isSystemUiShown;
    int SYSTEM_UI_HIDE_DELAY = 2000;
    Dialog dialog;
    TextView Guidline, Logout, CustomerName, CustomerIDTxt, AccountNumberTxt;
    ConstraintLayout dailogConstraintLayout, CustomerDetailConstraintLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account_number);

        Guidline = (TextView) findViewById(R.id.textView9);
        Logout = (TextView) findViewById(R.id.textView16);
        CustomerName = (TextView) findViewById(R.id.textView11);
        CustomerIDTxt = (TextView) findViewById(R.id.textView13);
        AccountNumberTxt = (TextView) findViewById(R.id.textView15);
        CustomerDetailConstraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);
        ValidationArrow = (ImageView) findViewById(R.id.imageView24);


        ValidationArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TempCustomerID = CustomerID.getText().toString();
                new VerifyCustomerID().execute();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Do the file write
        } else {
            // Request permission from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            Toast.makeText(getApplicationContext(),"Permission Granted",Toast.LENGTH_SHORT).show();

        }

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getSharedPreferences("loginData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();

                Intent i = new Intent(AddCustomerNumber.this, LoginActivity.class);
                startActivity(i);

                finish();
            }
        });

        View view = getLayoutInflater().inflate(R.layout.guidlines, null);

        dailogConstraintLayout = view.findViewById(R.id.ctlayout);

        dialog = new Dialog(this, R.style.Theme_AppCompat_Light_NoActionBar_FullScreen);

        //dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(view);

        //dailogConstraintLayout = (ConstraintLayout) findViewById(R.id.ctlayout);

        Guidline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        dailogConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        CustomerID = (EditText) findViewById(R.id.accountno);
        ScrollableText1 = (TextView) findViewById(R.id.scrollableText1);
        Verify = (Button) findViewById(R.id.imageView7);

        ScrollableText1.setSelected(true);

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



        Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TempAccountNumber != null){

                    Intent i = new Intent(AddCustomerNumber.this, ScanPhoto.class);
                    startActivity(i);
    /*
                    SharedPreferences pref = getSharedPreferences("loginData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("tempcustomerid", Tem );
                    editor.commit();*/

                }else{

                }

            }
        });

        new Adverts().execute();

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

    private class VerifyCustomerID extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display progress bar

        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            httpParams.put("custid", TempCustomerID);
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    url_verification , "GET", httpParams);
            try {
                int success = jsonObject.getInt(TAG_SUCCESS);
                JSONObject user;
                if (success == 1) {
                    //Parse the JSON response
                    user = jsonObject.getJSONObject(TAG_PROFILE);

                    TempCustomerID = user.getString("custid");
                    TempCustomerName = user.getString("custname");
                    TempAccountNumber = user.getString("accno");
                    TempCustPOI = user.getString("proofofidentity");
                    TempCustPOA = user.getString("proofofaddress");


                }else{

                    /*Handler mHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message message) {
                            // This is where you do your work in the UI thread.
                            // Your worker tells you in the message what to do.
                            Toast.makeText(AddCustomerNumber.this, "Account Not Found", Toast.LENGTH_LONG).show();
                        }
                    };
*/
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddCustomerNumber.this, "Customer Not Found", Toast.LENGTH_LONG).show();
                        }
                    });

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

            SharedPreferences pref = getSharedPreferences("loginData", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("tempaccountnumber", TempCustomerID );
            editor.putString("tempcustomerid", TempCustomerID );
            editor.putString("tempcustname", TempCustomerName );
            editor.putString("tempcustac", TempAccountNumber );
            editor.putString("Temppoi", TempCustPOI );
            editor.putString("Temppoa", TempCustPOA );
            editor.commit();


            CustomerDetailConstraintLayout.setVisibility(View.VISIBLE);
            CustomerIDTxt.setText(TempCustomerID);
            CustomerName.setText(TempCustomerName);
            AccountNumberTxt.setText(TempAccountNumber);



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
