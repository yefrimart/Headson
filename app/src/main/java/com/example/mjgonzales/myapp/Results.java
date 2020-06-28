package com.example.mjgonzales.myapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Results extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        TextView correctWords = findViewById(R.id.correct_word_list);
        TextView incorrectWords = findViewById(R.id.incorrect_word_list);

		ArrayList <String> correctWordList = getIntent().getStringArrayListExtra(Game.CORRECT_WORD_SET);
		ArrayList <String> incorrectWordList = getIntent().getStringArrayListExtra(Game.INCORRECT_WORD_SET);

		String text = "";
		for (String s : correctWordList) {
			text += s + "\n";
		}
		correctWords.setText(text);

		text = "";
		for (String s : incorrectWordList) {
			text += s + "\n";
		}
		incorrectWords.setText(text);


        int totalCount = getIntent().getIntExtra(Game.TOTAL_WORDS,0);
        int acceptedCount = getIntent().getIntExtra(Game.ACCEPTED_WORDS,0);

        TextView acceptedText = findViewById(R.id.acceptedCount);
		acceptedText.setText("" + acceptedCount);

		TextView failedText = findViewById(R.id.failedCount);
		failedText.setText("" + (totalCount-acceptedCount));

		TextView totalText = findViewById(R.id.totalCount);
		totalText.setText("" + totalCount);

		ImageButton playAgainButton = findViewById(R.id.playAgainButton);
		ImageButton homeButton = findViewById(R.id.homeButton);

		playAgainButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentLoadNewActivity = new Intent(Results.this, LoadingGame.class);
				intentLoadNewActivity.putExtra(Game.CATEGORY_ARRAY_ID, getIntent().getIntExtra(Game.CATEGORY_ARRAY_ID, -1));
				startActivity(intentLoadNewActivity);
				finish();
			}
		});


		homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

    }
}
