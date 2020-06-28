package com.example.mjgonzales.myapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {

    ImageButton animalsButton;
    ImageButton sportsButton;
    ImageButton expressionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animalsButton = findViewById(R.id.animalsButton);
        sportsButton = findViewById(R.id.sportsButton);
        expressionsButton = findViewById(R.id.expressionsButton);

        animalsButton.setOnClickListener(new CategoryBtnListerner(R.array.animalPhrases));
        sportsButton.setOnClickListener(new CategoryBtnListerner(R.array.sportPhrases));
        expressionsButton.setOnClickListener(new CategoryBtnListerner(R.array.expressionPhrases));

    }



    class CategoryBtnListerner implements View.OnClickListener {

        private int categoryId;

        CategoryBtnListerner(int categoryId) {
            this.categoryId = categoryId;
        }

        @Override
        public void onClick(View v) {
            Intent intentLoadNewActivity = new Intent(MainActivity.this, LoadingGame.class);
            intentLoadNewActivity.putExtra(Game.CATEGORY_ARRAY_ID, categoryId);
            startActivity(intentLoadNewActivity);
        }
    }

}
