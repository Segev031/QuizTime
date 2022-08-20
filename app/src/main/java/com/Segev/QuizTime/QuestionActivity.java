package com.Segev.QuizTime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.Segev.QuizTime.SetsActivity.category_id;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView question, qCount, timer;
    private Button option1, option2, option3, option4;
    private List<Question> questionList;
    private int questNum;
    private CountDownTimer countDown;
    private int score;
    private FirebaseFirestore firestore;
    private int setNo;
    private Dialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        question = findViewById(R.id.question);
        qCount = findViewById(R.id.quest_num);
        timer = findViewById(R.id.countdown);

        option1= findViewById(R.id.option1);
        option2= findViewById(R.id.option2);
        option3= findViewById(R.id.option3);
        option4= findViewById(R.id.option4);

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);

        // show loading dialog
        loadingDialog = new Dialog(QuestionActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        firestore = FirebaseFirestore.getInstance();
        // get the number of the clicked set
        setNo = getIntent().getIntExtra("SETNO",1);

        getQuestionsList();
        score=0;
    }

    private void getQuestionsList()
    {
        questionList = new ArrayList<>();
        firestore.collection("QUIZ").document("CAT" + String.valueOf(category_id))
                .collection("SET" + String.valueOf(setNo))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    QuerySnapshot questions = task.getResult();

                    for (QueryDocumentSnapshot doc : questions) {
                        // load all the questions to the questions list
                        questionList.add(new Question(doc.getString("QUESTION"),
                                doc.getString("A"), doc.getString("B"), doc.getString("C"), doc.getString("D"),
                                Integer.valueOf(doc.getString("ANSWER"))
                        ));
                    }

                    setQuestion();
                }
                else {
                    Toast.makeText(QuestionActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                // cancel the loading dialog
                loadingDialog.cancel();
            }
        });

    }

    private void setQuestion() {
        // set text of the timer to 10 seconds
        timer.setText(String.valueOf(10));

        // set text for the question and for the options
        question.setText(questionList.get(0).getQuestion());
        option1.setText(questionList.get(0).getOption1());
        option2.setText(questionList.get(0).getOption2());
        option3.setText(questionList.get(0).getOption3());
        option4.setText(questionList.get(0).getOption4());

        // set the question count number to 1/(number of questions)
        qCount.setText(String.valueOf(1)+"/"+String.valueOf(questionList.size()));

        // start the timer
        startTimer();

        questNum = 0;
    }

    private void startTimer() {
        // create a countdown timer for 10 seconds
         countDown = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                // On every tick the value of the seconds is changing
                timer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {

                changeQuestion();
            }
        };

         // start the counting
        countDown.start();
    }



    @Override
    public void onClick(View v) {

        int selectedOption = 0;
        switch (v.getId())
        {
            // get the selected option and disable clicks until the question changing
            case R.id.option1:
                selectedOption = 1;
                option2.setEnabled(false);
                option3.setEnabled(false);
                option4.setEnabled(false);
                break;
            case R.id.option2:
                selectedOption = 2;
                option1.setEnabled(false);
                option3.setEnabled(false);
                option4.setEnabled(false);
                break;
            case R.id.option3:
                selectedOption = 3;
                option1.setEnabled(false);
                option2.setEnabled(false);
                option4.setEnabled(false);
                break;
            case R.id.option4:
                selectedOption = 4;
                option1.setEnabled(false);
                option2.setEnabled(false);
                option3.setEnabled(false);
                break;
            default:
        }

        checkAnswer(selectedOption, v);
        countDown.cancel();

    }



    private void checkAnswer(int selectedOption, View view)
    {

        if(selectedOption == questionList.get(questNum).getCorrectAns())
        {
            // right Answer
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            // add one to the score
            score++;

        }
        else {
            // wrong Answer
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.RED));

            // show the right answer
            switch (questionList.get(questNum).getCorrectAns())
            {
                case 1:
                    option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 2:
                    option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 3:
                    option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 4:
                    option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
            }


        }
        // wait 800 milliseconds and then change the question
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeQuestion();
            }
        }, 800);
    }

    private void changeQuestion()
    {
        if(questNum != questionList.size() - 1)
        {
            questNum++;
            // replaces everything with animation
            playAnim(question, 0,0);
            playAnim(option1, 0,1);
            playAnim(option2, 0,2);
            playAnim(option3, 0,3);
            playAnim(option4, 0,4);
            // set all the options to be clickable again
            option1.setEnabled(true);
            option2.setEnabled(true);
            option3.setEnabled(true);
            option4.setEnabled(true);

            // set the question count number to (question number)/(number of questions)
            qCount.setText(String.valueOf(questNum+1)+"/"+String.valueOf(questionList.size()));
            // set the text of the timer to 10 seconds again
            timer.setText(String.valueOf(10));
            // start the timer again
            startTimer();

        }

        else
        {
            // move to the next activity
            Intent intent = new Intent(QuestionActivity.this, ScoreActivity.class);
            // put extra data in the intent of the final score
            intent.putExtra("SCORE", String.valueOf(score) + "/" + String.valueOf(questionList.size()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void playAnim(final View view, final int value, final int viewNum)
    {

        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).
                setStartDelay(100).setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        // Nothing
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(value == 0)
                        {
                            // change all the views with the zero value
                            switch (viewNum)
                            {
                                case 0:
                                    ((TextView)view).setText(questionList.get(questNum).getQuestion());
                                    break;
                                case 1:
                                    ((Button)view).setText(questionList.get(questNum).getOption1());
                                    break;
                                case 2:
                                    ((Button)view).setText(questionList.get(questNum).getOption2());
                                    break;
                                case 3:
                                    ((Button)view).setText(questionList.get(questNum).getOption3());
                                    break;
                                case 4:
                                    ((Button)view).setText(questionList.get(questNum).getOption4());
                                    break;
                            }


                             if(viewNum != 0)
                                 ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0575CD")));

                             // and then change their values to one
                            playAnim(view, 1, viewNum);
                        }

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        // Nada
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                       // Absolutely Nada
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(QuestionActivity.this, GameMusicService.class);
        startService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = new Intent(QuestionActivity.this, GameMusicService.class);
        stopService(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // cancel the countdown timer
        countDown.cancel();
    }
}