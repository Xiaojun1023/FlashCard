package com.example.flashcard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity
{
    public ListView flashcardListView;
    String[] flashcardTitles =
            {"Flashcard 1: What is a mobile application?",
                    "Flashcard 2: Role of UI/UX in mobile app development",
                    "Flashcard 3: What is an API in mobile app development?",
                    "Flashcard 4: What is a RelativeLayout?",
                    "Flashcard 5: How to use ListView?",
                    "Flashcard 6: ",
                    "Flashcard 7: ",
                    "Flashcard 8: ",
                    "Flashcard 9: ",
                    "Flashcard 10: ",
                    "Flashcard 11: ",
                    "Flashcard 12: ",
                    "Flashcard 13: ",
                    "Flashcard 14: ",
                    "Flashcard 15: ",};

    String selectedColor = "Default";

    SharedPreferences MySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flashcardListView = findViewById(R.id.flashcardListView);

        MySharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        selectedColor = MySharedPreferences.getString("BackgroundColor", "Default");
        applyBackgroundColor(selectedColor);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, flashcardTitles);
        flashcardListView.setAdapter(adapter);

        flashcardListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                flashcardsNumber(position + 1);
            }
        });

        Button changeColorButton = findViewById(R.id.button);
        changeColorButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showColorSelection();
            }
        });

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showAddFlashcardDialog();
            }
        });
        loadAdditionalFlashcards();
    }

    private void showAddFlashcardDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Flashcard");

        final EditText inputQuestion = new EditText(this);
        inputQuestion.setHint("Enter question");
        final EditText inputAnswer = new EditText(this);
        inputAnswer.setHint("Enter answer");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputQuestion);
        layout.addView(inputAnswer);
        builder.setView(layout);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String question = inputQuestion.getText().toString();
                String answer = inputAnswer.getText().toString();

                if (!question.isEmpty() && !answer.isEmpty())
                {
                    saveFlashcard(question, answer);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Both fields are required!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void saveFlashcard(String question, String answer)
    {
        SharedPreferences.Editor editor = MySharedPreferences.edit();
        int availableSlot = findAvailableSlot();
        if (availableSlot == -1)
        {
            Toast.makeText(this, "No available slots for new flashcards!", Toast.LENGTH_SHORT).show();
            return;
        }

        editor.putString("Flashcard" + availableSlot + "_Question", question);
        editor.putString("Flashcard" + availableSlot + "_Answer", answer);
        editor.apply();

        flashcardTitles[availableSlot - 1] = "Flashcard " + availableSlot + ": " + question;
        ((ArrayAdapter) flashcardListView.getAdapter()).notifyDataSetChanged();

        Toast.makeText(this, "Flashcard added to slot " + availableSlot, Toast.LENGTH_SHORT).show();
    }

    private int findAvailableSlot()
    {
        for (int i = 6; i <= 15; i++)
        {
            if (MySharedPreferences.getString("Flashcard" + i + "_Question", "").isEmpty())
            {
                return i;
            }
        }
        return -1;
    }

    private void loadAdditionalFlashcards()
    {
        for (int i = 6; i <= 15; i++)
        {
            String question = MySharedPreferences.getString("Flashcard" + i + "_Question", "");
            if (!question.isEmpty())
            {
                flashcardTitles[i - 1] = "Flashcard " + i + ": " + question;
            }
        }
        ((ArrayAdapter) flashcardListView.getAdapter()).notifyDataSetChanged();
    }

    private void showEditOrViewDialog(int number, String question, String answer)
    {
        new AlertDialog.Builder(this)
                .setTitle("Flashcard Options")
                .setMessage("Do you want to view or edit this flashcard?")
                .setPositiveButton("View", (dialog, which) ->
                {
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    intent.putExtra("FlashcardNumber", number);
                    intent.putExtra("Question", question);
                    intent.putExtra("Answer", answer);
                    startActivity(intent);
                })
                .setNegativeButton("Edit", (dialog, which) ->
                {
                    showEditFlashcardDialog(number, question, answer);
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void showEditFlashcardDialog(int number, String oldQuestion, String oldAnswer)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Flashcard");

        final EditText inputQuestion = new EditText(this);
        inputQuestion.setHint("Enter question");
        inputQuestion.setText(oldQuestion);

        final EditText inputAnswer = new EditText(this);
        inputAnswer.setHint("Enter answer");
        inputAnswer.setText(oldAnswer);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputQuestion);
        layout.addView(inputAnswer);
        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) ->
        {
            String newQuestion = inputQuestion.getText().toString();
            String newAnswer = inputAnswer.getText().toString();

            updateFlashcard(number, newQuestion, newAnswer);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateFlashcard(int number, String question, String answer)
    {
        SharedPreferences.Editor editor = MySharedPreferences.edit();
        editor.putString("Flashcard" + number + "_Question", question);
        editor.putString("Flashcard" + number + "_Answer", answer);
        editor.apply();

        flashcardTitles[number - 1] = "Flashcard " + number + ": " + question;
        ((ArrayAdapter) flashcardListView.getAdapter()).notifyDataSetChanged();

        Toast.makeText(this, "Flashcard updated!", Toast.LENGTH_SHORT).show();
    }

    public void showColorSelection()
    {
        final String[] colors = {"Red", "Blue", "Yellow", "Gray", "Default"};

        new android.app.AlertDialog.Builder(this)
                .setTitle("Select Background Color")
                .setItems(colors, (dialog, which) ->
                {
                    selectedColor = colors[which];
                    Toast.makeText(this, "Selected Color: " + selectedColor, Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = MySharedPreferences.edit();
                    editor.putString("BackgroundColor", selectedColor);
                    editor.apply();

                    applyBackgroundColor(selectedColor);
                })
                .show();
    }

    private void applyBackgroundColor(String color)
    {
        View rootLayout = findViewById(R.id.main);

        switch (color)
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
            case "Default":
                rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.default_background));
                break;
        }
    }

    public void flashcardsNumber(int number)
    {
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        intent.putExtra("FlashcardNumber", number);

        if (number >= 1 && number <= 5)
        {
            if (number == 1)
            {
                intent.putExtra("Question","What is a mobile application?");
                intent.putExtra("Answer","A mobile application, or app, is software designed to run on mobile devices such as smartphones and tablets, providing users with specific functionalities or services.");
            }
            else if (number == 2)
            {
                intent.putExtra("Question","What is the role of UI/UX design in mobile app development?");
                intent.putExtra("Answer","UI (User Interface) and UX (User Experience) design are crucial in mobile app development as they focus on the app's look, feel, and overall user experience. Good UI/UX design ensures that the app is intuitive, user-friendly, and provides a satisfying experience to the user.");
            }
            else if (number == 3)
            {
                intent.putExtra("Question","What is an API in the context of mobile app development?");
                intent.putExtra("Answer","An API (Application Programming Interface) is a set of protocols and tools that allows different software components to communicate with each other. In mobile app development, APIs are used to enable features such as data exchange with a server, accessing device hardware, or integrating third-party services.");
            }
            else if (number == 4)
            {
                intent.putExtra("Question","What is a RelativeLayout?");
                intent.putExtra("Answer","RelativeLayout is a view group in Android that displays child views in relative positions. In a RelativeLayout, each view can be positioned relative to its parent layout or other child views, allowing for flexible and dynamic UI designs.");
            }
            else
            {
                intent.putExtra("Question","How to Use ListView?");
                intent.putExtra("Answer","ListView is a view in Android that displays a vertically scrollable list of items. Each item in the list is defined by an Adapter that provides the content and layout for each row.");
            }
            startActivity(intent);
        }
        else if (number >= 6 && number <= 15)
        {
            String question = MySharedPreferences.getString("Flashcard" + number + "_Question", "");
            String answer = MySharedPreferences.getString("Flashcard" + number + "_Answer", "");

            showEditOrViewDialog(number, question, answer);
        }
    }
}