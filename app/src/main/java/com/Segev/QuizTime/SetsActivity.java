package com.Segev.QuizTime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SetsActivity extends AppCompatActivity {

    private GridView sets_grid;
    private FirebaseFirestore firestore;
    public static int category_id;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        // adding toolbar and support action bar
        Toolbar toolbar = findViewById(R.id.set_toolbar);
        setSupportActionBar(toolbar);
        // get the name and the ID of the clicked category
        String title = getIntent().getStringExtra("CATEGORY");
        category_id = getIntent().getIntExtra("CATEGORY_ID",1);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // show loading dialog
        loadingDialog = new Dialog(SetsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        firestore = FirebaseFirestore.getInstance();
        loadSets();

        sets_grid = findViewById(R.id.sets_gridview);


    }

    public void loadSets()
    {

        firestore.collection("QUIZ").document("CAT" + String.valueOf(category_id))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    // load sets
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        long sets = (long) doc.get("SETS");
                        // set an adapter for the sets grid
                        SetsAdapter adapter = new SetsAdapter((int) sets);
                        sets_grid.setAdapter(adapter);
                        loadingDialog.cancel();

                    } else {
                        Toast.makeText(SetsActivity.this, "No CAT Document Exists!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    Toast.makeText(SetsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                loadingDialog.cancel();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            // finish the sets activity and move to the previous activity
            SetsActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}