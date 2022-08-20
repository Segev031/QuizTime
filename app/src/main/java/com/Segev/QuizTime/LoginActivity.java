package com.Segev.QuizTime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmail, mPassword;
    private Button loginButton, buttonBye;
    private TextView forgotPasswordLink, title, createBtn;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth;
    static boolean active = false;
    private boolean isFromScoreActivity, isFromCategoryActivity;
    CoolBroadcastReceiver coolBroadcastReceiver = new CoolBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // restart the service if the moving was from the categories activity or from the scores activity
        isFromCategoryActivity = getIntent().getBooleanExtra("fromCategoryActivity", false);
        isFromScoreActivity = getIntent().getBooleanExtra("fromScoreActivity", false);
        if (isFromScoreActivity || isFromCategoryActivity) {
            active=false;
        }

        // create an animated gradient background
        ConstraintLayout constraintLayout = findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        createBtn = findViewById(R.id.createButton);
        progressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        forgotPasswordLink = findViewById(R.id.forgotPassword);
        title = findViewById(R.id.main_title);
        buttonBye = findViewById(R.id.buttonBye);
        loginButton.setOnClickListener(this);
        createBtn.setOnClickListener(this);
        forgotPasswordLink.setOnClickListener(this);
        buttonBye.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // register the broadcast receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(coolBroadcastReceiver, filter);
        if (!active) {
            // activate the music service
            Intent i=new Intent(LoginActivity.this, MusicService.class);
            startService(i);
            active=true;
        }
        loginButton.setEnabled(true);
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

        if (v == loginButton) {
            // canceling the double click
            loginButton.setEnabled(false);
            // get the user inputs as strings
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            // integrity check
            if (TextUtils.isEmpty(email)) {
                loginButton.setEnabled(true);
                mEmail.setError("E-Mail is required.");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                loginButton.setEnabled(true);
                mPassword.setError("Password is required.");
                return;
            }
            if (password.length() < 6) {
                loginButton.setEnabled(true);
                mPassword.setError("Password must have 6 characters or more.");
                return;
            }
            if (!checkString(password)) {
                loginButton.setEnabled(true);
                mPassword.setError("Password must have at least 1 number and 1 letter");
                return;
            }

            // show progress bar
            progressBar.setVisibility(View.VISIBLE);
            // animation of the title
            YoYo.with(Techniques.Tada).duration(2000).repeat(1).playOn(title);

            //authenticate the user
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // concealing of the progress bar
                        progressBar.setVisibility(View.GONE);
                        // stop the service
                        Intent i=new Intent(LoginActivity.this, MusicService.class);
                        stopService(i);
                        Toast.makeText(LoginActivity.this, "Logged in Successfully.", Toast.LENGTH_SHORT).show();
                        // move to the next activity
                        Intent intent = new Intent(LoginActivity.this, CategoryActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        // enabling the pressing of the button again and hiding the progress bar
                        loginButton.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
        if (v == createBtn) {
            // create an account by moving to the register / main activity
            Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent1);
        }

        if (v == forgotPasswordLink) {
            final EditText resetMail = new EditText(v.getContext());
            // create a dialog for password resetting
            final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle("Reset Password ?");
            passwordResetDialog.setMessage("Enter Your Email To Received Reset Link. ");
            passwordResetDialog.setView(resetMail);
            passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // get the user input as string
                    String mail = resetMail.getText().toString();
                    // send a password reset email
                    fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(LoginActivity.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Error! Reset Link is Not Sent. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //close the dialog
                }
            });

            AlertDialog dialog = passwordResetDialog.create();
            dialog.show();
            dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

        }
        if (v == buttonBye) {
            // check if the user actually want to exit by alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            // set the message of the alert dialog
            builder.setMessage("Are you sure you want to exit?").
                    setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // stop music service
                            Intent i=new Intent(LoginActivity.this, MusicService.class);
                            stopService(i);
                            // finish all the open activities
                            finishAffinity();
                            // set the general form for the message
                            String message = "Hope you come back soon!";
                            NotificationCompat.Builder builder1 = new NotificationCompat.Builder(LoginActivity.this)
                                    .setSmallIcon(R.drawable.ic_message)
                                    .setContentTitle("Quiz Time!")
                                    .setContentText(message)
                                    .setAutoCancel(true);

                            // send a notification to the user
                            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            PendingIntent pendingIntent = PendingIntent.getActivity(LoginActivity.this, 0, intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            builder1.setContentIntent(pendingIntent);
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                builder1.setChannelId("com.Segev.QuizTime");
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel channel = new NotificationChannel(
                                        "com.Segev.QuizTime",
                                        "NewQuiz",
                                        NotificationManager.IMPORTANCE_DEFAULT
                                );
                                if (notificationManager != null) {
                                    notificationManager.createNotificationChannel(channel);
                                }
                            }

                            notificationManager.notify(0, builder1.build());
                            // for the ;returning by click on the notification
                            active = false;
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
