package com.Segev.QuizTime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private TextView appName;
    public static List<String> catList = new ArrayList<>();
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        appName = findViewById(R.id.appName);

        // starting animation
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myanim);
        appName.setAnimation(anim);

        firestore = FirebaseFirestore.getInstance();

        new Thread() {
            @Override
            public void run() {
                loadData();
            }
        }.start();
    }
    private void loadData()
    {

        // get the information from the firebase firestore
        catList.clear();
        firestore.collection("QUIZ").document("Categories")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    // categories docs loading
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists())
                    {
                        // get the number of categories
                        long count = (long)doc.get("COUNT");
                        // insert the categories to the categories list
                        for(int i=1; i <= count; i++)
                        {
                            String catName = doc.getString("CAT" + String.valueOf(i));
                            catList.add(catName);
                        }

                        // move to the next activity
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);

                    }
                    else
                    {
                        Toast.makeText(SplashActivity.this, "No Category Document Exists!", Toast.LENGTH_LONG).show();
                    }
                    SplashActivity.this.finish();
                }
                else
                {
                    Toast.makeText(SplashActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}