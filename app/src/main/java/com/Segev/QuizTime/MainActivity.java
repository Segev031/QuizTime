package com.Segev.QuizTime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.Segev.QuizTime.LoginActivity.active;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView title, loginBtn;
    private Button startButton, buttonBye;
    private EditText mUsername,mPhone, mPassword, mEmail;
    private FirebaseAuth fAuth;
    private ProgressBar progressBar;
    private FirebaseFirestore fstore;
    String userID;
    public static final String TAG = "TAG";
    CoolBroadcastReceiver coolBroadcastReceiver = new CoolBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // create an animated gradient background
        ConstraintLayout constraintLayout = findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        progressBar = findViewById(R.id.progressBar);
        title = findViewById(R.id.main_title);
        startButton = findViewById(R.id.start_button);
        buttonBye = findViewById(R.id.buttonBye);
        mUsername = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.phone);
        mEmail = findViewById(R.id.email);
        loginBtn = findViewById(R.id.loginBtn);
        fstore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        startButton.setOnClickListener(this);
        buttonBye.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // register the broadcast receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(coolBroadcastReceiver,filter);
        if (getIntent().getBooleanExtra("fromCategoryActivity", false)) {
            // activate the music service
            Intent i=new Intent(MainActivity.this, MusicService.class);
            startService(i);
            active=true;
        }
        startButton.setEnabled(true);
    }


    @Override
        protected void onStop() {
            super.onStop();
        // finish all the open activities
            finishAffinity();
            // unregister the broadcast receiver
            unregisterReceiver(coolBroadcastReceiver);
        }


    @Override
        public void onClick(View v) {

            if (v == startButton) {
                // canceling the double click
                startButton.setEnabled(false);
                // get the user inputs as strings
                final String email = mEmail.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();
                final String username = mUsername.getText().toString().trim();
                final String phone = mPhone.getText().toString().trim();

                // integrity check
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (email.isEmpty()) {
                    startButton.setEnabled(true);
                    mEmail.setError("E-mail is required.");
                    return;
                } else {
                    if (email.matches(emailPattern)) {
                        //correct email address
                    } else {
                        startButton.setEnabled(true);
                        mEmail.setError("Invalid email address");
                        return;
                    }
                }
                if (TextUtils.isEmpty(password)) {
                    startButton.setEnabled(true);
                    mPassword.setError("Password is required.");
                    return;
                }
                if ((password.length() < 6)) {
                    startButton.setEnabled(true);
                    mPassword.setError("Password must have 6 characters");
                    return;
                }
                if (!checkString(password)) {
                    startButton.setEnabled(true);
                    mPassword.setError("Password must have at least 1 number and 1 letter");
                    return;
                }
                if (!phone.startsWith("05")) {
                    startButton.setEnabled(true);
                    mPhone.setError("Phone number have to start with '05'");
                    return;
                }
                if (phone.length() != 10) {
                    startButton.setEnabled(true);
                    mPhone.setError("Phone number must have 10 characters");
                    return;
                }

                // show progress bar
                progressBar.setVisibility(View.VISIBLE);

                //  animate the title
                YoYo.with(Techniques.Tada).duration(2000).repeat(1).playOn(title);
                // wait 1650 milliseconds
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //register the user in the firebase authentication
                        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // stop the service
                                    Intent i=new Intent(MainActivity.this, MusicService.class);
                                    stopService(i);
                                    //send a verification email for the current user
                                    FirebaseUser fuser = fAuth.getCurrentUser();
                                    fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(MainActivity.this, "Verification Email Has Been Sent. ", Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailture: Email not sent. " + e.getMessage());
                                        }
                                    });

                                    // save the username of the user on the device
                                    SharedPreferences sp = getSharedPreferences("key", 0);
                                    SharedPreferences.Editor sedt = sp.edit();
                                    sedt.putString("username", username);
                                    sedt.commit();

                                    Toast.makeText(MainActivity.this, "User created", Toast.LENGTH_SHORT).show();
                                    userID = fAuth.getCurrentUser().getUid();
                                    // create a collection for each user
                                    DocumentReference documentReference = fstore.collection("users").document(userID);

                                    Map<String, Object> user = new HashMap<>();
                                    user.put("username", username);
                                    user.put("email", email);
                                    user.put("phone", phone);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: user profile created for: " + userID);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailture: " + e.toString());

                                        }
                                    });

                                    // move to the next activity
                                    startActivity(new Intent(MainActivity.this, CategoryActivity.class));
                                } else {
                                    Toast.makeText(MainActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    startButton.setEnabled(true);
                                }
                            }
                        });


                    }
                }, 1650);
            }
            if (v == buttonBye) {
                // check if the user actually want to exit by alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                // set the message of the alert dialog
                builder.setMessage("Are you sure you want to exit?").
                        setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // stop music service
                                Intent i=new Intent(MainActivity.this, MusicService.class);
                                stopService(i);
                                // finish all the open activities
                                finishAffinity();
                                // set the general form for the message
                                String message = "Hope you come back soon!";
                                NotificationCompat.Builder builder1 = new NotificationCompat.Builder(com.Segev.QuizTime.MainActivity.this)
                                        .setSmallIcon(R.drawable.ic_message)
                                        .setContentTitle("Quiz Time!")
                                        .setContentText(message)
                                        .setAutoCancel(true);
                                // send a notification to the user
                                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,0,intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);
                                builder1.setContentIntent(pendingIntent);
                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                    builder1.setChannelId("com.Segev.QuizTime");
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                    NotificationChannel channel = new NotificationChannel(
                                            "com.Segev.QuizTime",
                                            "NewQuiz",
                                            NotificationManager.IMPORTANCE_DEFAULT
                                    );
                                    if(notificationManager != null){
                                        notificationManager.createNotificationChannel(channel);
                                    }
                                }
                                notificationManager.notify(0,builder1.build());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog
                                dialog.cancel();

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

            }
            if (v == loginBtn) {
                // login to exist account by moving to the login activity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }


    // get the password as string
    // check if there is at least one letter and one digit
    private boolean checkString(String password) {
        char ch;
        password = password.toLowerCase();
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        for(int i=0;i < password.length();i++) {
            ch = password.charAt(i);
            if( Character.isDigit(ch)) {
                numberFlag = true;
            }
            else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
            if(numberFlag && lowerCaseFlag)
                return true;
        }
        return false;
    }
}