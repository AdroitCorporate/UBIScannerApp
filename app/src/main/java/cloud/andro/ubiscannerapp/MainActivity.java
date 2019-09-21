package cloud.andro.ubiscannerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser mUser;
    TextView Resend;
    public static String TAG = "100";
    String email, password = "helloworld";
    private boolean emailAddressChecker;

    ImageView Proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Proceed = (ImageView) findViewById(R.id.imageView8);
        Resend = (TextView) findViewById(R.id.textView2);

        SharedPreferences pref = getSharedPreferences("loginData", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        email = pref.getString("email", null);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
      /*  mAuth.createUserWithEmailAndPassword(email,password);
        mUser.sendEmailVerification();*/

      Resend.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              SendEmailVerificationMessage();
          }
      });

      Proceed.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

             finish();
              overridePendingTransition( 0, 0);
              startActivity(getIntent());
              overridePendingTransition( 0, 0);

              if(mAuth.getCurrentUser().isEmailVerified()){

//                  Toast.makeText(getApplicationContext(), "Email Is Verified", Toast.LENGTH_LONG).show();
                  Intent i = new Intent(MainActivity.this, AddCustomerNumber.class);
                  startActivity(i);

              }else{
                  //Toast.makeText(getApplicationContext(), "Please Verify Email", Toast.LENGTH_LONG).show();

              }



          }
      });


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(getApplicationContext(), "User Registration successful!", Toast.LENGTH_LONG).show();
                            //progressBar.setVisibility(View.GONE);
                            SendEmailVerificationMessage();
                            /*Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(intent);*/
                        }
                        else {
                            //Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                            //progressBar.setVisibility(View.GONE);
                        }
                    }
                });
        //VerifyEmailAddress();



        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //SendEmailVerificationMessage();
                            //Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                            //progressBar.setVisibility(View.GONE);

                            if(mAuth.getCurrentUser().isEmailVerified()){

                               // Toast.makeText(getApplicationContext(), "Email Is Verified", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(MainActivity.this, AddCustomerNumber.class);
                                startActivity(i);

                            }else{
                                Toast.makeText(getApplicationContext(), "Please Verify Email", Toast.LENGTH_LONG).show();

                            }

                            /*Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);*/
                        }
                        else {
                            //Toast.makeText(getApplicationContext(), "Login failed! Please try again later", Toast.LENGTH_LONG).show();
                            //progressBar.setVisibility(View.GONE);
                        }
                    }
                });


        /*mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    SignInMethodQueryResult result = task.getResult();
                    List<String> signInMethods = result.getSignInMethods();
                    if (signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                        // User can sign in with email/password
                    } else if (signInMethods.contains(EmailAuthProvider.EMAIL_LINK_SIGN_IN_METHOD)) {
                        // User can sign in with email/link
                    }
                } else {
                    Log.e(TAG, "Error getting sign in methods for user", task.getException());
                }
            }
        });*/
    }

    public void SendEmailVerificationMessage(){

        if(mUser != null){
            mUser.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Verification Mail Sent", Toast.LENGTH_LONG).show();
                    }else{
                        String error = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void VerifyEmailAddress(){
        emailAddressChecker = mUser.isEmailVerified();

        if(emailAddressChecker){
            //Toast.makeText(getApplicationContext(), "Verification Done", Toast.LENGTH_LONG).show();
        }else{
            //Toast.makeText(getApplicationContext(), "Please verify your account", Toast.LENGTH_LONG).show();
            mAuth.signOut();
        }
    }

}

