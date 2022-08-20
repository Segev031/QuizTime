package com.Segev.QuizTime;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import pl.droidsonroids.gif.GifImageView;

public class ScoreActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView score;
    private Button done;
    private Button logout;
    private Button btnBye;
    private TextView text;
    private GifImageView gifImageView;
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        score = findViewById(R.id.sa_score);
        done = findViewById(R.id.sa_done);
        text = findViewById(R.id.textView3);
        gifImageView=findViewById(R.id.gifImageView);
        aSwitch=findViewById(R.id.switch1);
        logout=findViewById(R.id.button);
        btnBye=findViewById(R.id.btnBye);

        done.setOnClickListener(this);
        logout.setOnClickListener(this);
        btnBye.setOnClickListener(this);

        // show "Congratulations (username)!"
        SharedPreferences sp = getSharedPreferences("key",0);
        String tValue = sp.getString("username","");
        text.setText("Congratulations " + tValue + "!");
        // show the final score
        String score_str = getIntent().getStringExtra("SCORE");
        score.setText(score_str);


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked)
                    // hide GIF
                    gifImageView.setVisibility(View.INVISIBLE);
                else
                    // show GIF
                    gifImageView.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == done) {
            // go back to the categories activity and finish this activity
            Intent intent = new Intent(ScoreActivity.this, CategoryActivity.class);
            startActivity(intent);
            finish();
        }
        if (v == logout) {
            // sign out from the firebase authentication
            FirebaseAuth.getInstance().signOut();
            // go back to the login activity
            Intent intent = new Intent(ScoreActivity.this, LoginActivity.class);
            // put extra data that the moving was from the score activity
            intent.putExtra("fromScoreActivity", true);
            startActivity(intent);
            finish();
        }
        if (v == btnBye) {
            // check if the user actually want to exit by alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(ScoreActivity.this);
            // set the message of the alert dialog
            builder.setMessage("Are you sure you want to exit?").
                    setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // finish all the open activities
                            finishAffinity();
                            // set the general form for the message
                            String message = "Hope you come back soon!";
                            NotificationCompat.Builder builder1 = new NotificationCompat.Builder(ScoreActivity.this)
                                    .setSmallIcon(R.drawable.ic_message)
                                    .setContentTitle("Quiz Time!")
                                    .setContentText(message)
                                    .setAutoCancel(true);
                            // send a notification to the user
                            Intent intent = new Intent(ScoreActivity.this,SplashActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            PendingIntent pendingIntent = PendingIntent.getActivity(ScoreActivity.this,0,intent,
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

    }
}