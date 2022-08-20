package com.Segev.QuizTime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import static com.Segev.QuizTime.SplashActivity.catList;

public class CategoryActivity extends AppCompatActivity {

    private GridView catGrid;
    private Button resendCode;
    private TextView verifyMsg;
    private FirebaseAuth fAuth;
    protected String userId;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // adding toolbar and support action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resendCode = findViewById(R.id.resendCode);
        verifyMsg = findViewById(R.id.verifyMsg);
        fAuth=FirebaseAuth.getInstance();
        catGrid = findViewById(R.id.catGridView);

        // set an adapter for the categories grid
        catGridAdapter adapter = new catGridAdapter(catList);
        catGrid.setAdapter(adapter);

        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        if (!user.isEmailVerified()){
            // bothering to the user
            verifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);
            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    // send email verification again
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CategoryActivity.this, "Verification Email Has been Sent.", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag", "onFailure: Email not sent " + e.getMessage());
                        }
                    });
                }
            });
        } else {
            verifyMsg.setVisibility(View.GONE);
            resendCode.setVisibility(View.GONE);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0,1,0,"Register Screen");
        menu.add(0,2,0,"Login Screen");
        menu.add(0,3,0,"Random Category");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==1)
        {
            Intent intent=new Intent(CategoryActivity.this, MainActivity.class);
            // put in the data that the moving was from the categories activity
            intent.putExtra("fromCategoryActivity", true);
            startActivity(intent);
        }
        if(id==2)
        {
            Intent intent=new Intent(CategoryActivity.this,LoginActivity.class);
            // put in the data that the moving was from the categories activity
            intent.putExtra("fromCategoryActivity", true);
            startActivity(intent);
        }
        if(id==3)
        {
            // create an intent
            Intent intent = new Intent(CategoryActivity.this, SetsActivity.class);
            // Randomize a number
            int position = (int) (Math.random() * 6);
            // put extra data in the intent of the category name and his ID
            intent.putExtra("CATEGORY", catList.get(position));
            intent.putExtra("CATEGORY_ID", position + 1);
            // move to the next activity
            startActivity(intent);
        }
        if(id == android.R.id.home)
        {
            Intent intent=new Intent(CategoryActivity.this, LoginActivity.class);
            // put in the data that the moving was from the categories activity
            intent.putExtra("fromCategoryActivity", true);
            // move to the previous activity
            startActivity(intent);
            CategoryActivity.this.finish();

        }

        return super.onOptionsItemSelected(item);
    }

}