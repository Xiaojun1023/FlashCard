package com.example.flashcard;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SecondActivity extends AppCompatActivity
{
    public boolean showQuestion = true;
    public TextView flashcardTextView;
    public Button flipButton;
    public int flipCount = 0;
    public TextView countTextView;
    private String flashcardKey;
    public Button resetButton;
    SharedPreferences MysharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        flashcardTextView = findViewById(R.id.flashcardTextView);
        flipButton = findViewById(R.id.button);
        countTextView = findViewById(R.id.flashcardCountTextView);

        resetButton = findViewById(R.id.resetButton);

        Intent intent = getIntent();
        String question = intent.getStringExtra("Question");
        String answer = intent.getStringExtra("Answer");
        int flashcardNumber = intent.getIntExtra("FlashcardNumber", 0);

        flashcardKey = "flipCount_" + flashcardNumber;

        flashcardTextView.setText(question);

        MysharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        flipCount = MysharedPreferences.getInt(flashcardKey, 0);
        countTextView.setText("Count: " + flipCount);

        String backgroundColor = MysharedPreferences.getString("BackgroundColor", "Default");

        View rootLayout = findViewById(R.id.main);
        switch (backgroundColor)
        {
            case "Red":
                rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.red_background));
                break;
            case "Blue":
                rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_background));
                break;
            case "Yellow":
                rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow_background));
                break;
            case "Gray":
                rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_background));
                break;
            case "White":
                rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.default_background));
                break;
        }

        flipButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                flipCard();
                if (showQuestion)
                {
                    flashcardTextView.setText(answer);
                }
                else
                {
                    flashcardTextView.setText(question);
                    flipCount++;
                    countTextView.setText("Count: " + flipCount);

                    SharedPreferences.Editor editor = MysharedPreferences.edit();
                    editor.putInt(flashcardKey, flipCount);
                    editor.apply();
                }
                showQuestion = !showQuestion;
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                flipCount = 0;
                countTextView.setText("Count: " + flipCount);

                SharedPreferences.Editor editor = MysharedPreferences.edit();
                editor.putInt(flashcardKey, flipCount);
                editor.apply();
            }
        });
    }

    private void flipCard()
    {
        AnimatorSet setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_right_out);
        AnimatorSet setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_left_in);

        setRightOut.setTarget(flashcardTextView);
        setLeftIn.setTarget(flashcardTextView);
        setRightOut.start();
        setLeftIn.start();
    }
}