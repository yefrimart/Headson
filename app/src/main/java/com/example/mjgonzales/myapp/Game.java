package com.example.mjgonzales.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Game extends AppCompatActivity implements TiltListener {

	public static final String CATEGORY_ARRAY_ID = "categoryName";
	public static final String ACCEPTED_WORDS = "acceptedWords";
	public static final String TOTAL_WORDS = "totalWords";
	public static final String CORRECT_WORD_SET = "correctWordSet";
	public static final String INCORRECT_WORD_SET = "incorrectWordSet";

	private static final int TOTAL_SECONDS = 60;

	private int current = 0, remaining;
	private int correctCount = 0, totalCount = 0;

    private TextView remainingDisplay;
    private TextView phraseDisplay;
    private TextView categoryName;

    private ConstraintLayout backgroundGame;
    private Random random = new Random();

	private MediaPlayer correctSound = null, incorrectSound = null;

    private Phrase[] phrases = null;

    private Thread totalTimer = null;

    private TiltSensor sensor;

    private Handler mainThread = new Handler();

    private Set correctWords, incorrectWords;

	private StoppableRunneable process = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // initialize audio sound
		correctSound = MediaPlayer.create(this, R.raw.correct_sound);
		incorrectSound = MediaPlayer.create(this, R.raw.incorrect_sound);

		//initialize sets of words
		correctWords = new HashSet();
		incorrectWords = new HashSet();

        if (sensor == null) {
            sensor = new TiltSensor(this);
        }

        if (totalTimer == null) {
            totalTimer = new Thread(process = new StoppableRunneable() {
                @Override
                public void run() {
                    for (remaining = TOTAL_SECONDS; remaining >= 0; --remaining) {

                        mainThread.post(new Runnable() {
                            @Override
                            public void run() {
                                remainingDisplay.setText(String.valueOf(remaining));
                            }
                        });
                        if (hasStopped()) return;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mainThread.post(new Runnable() {
                        @Override
                        public void run() {
							Intent intent = new Intent(Game.this, Results.class);
							intent.putExtra(Game.CATEGORY_ARRAY_ID, getIntent().getIntExtra(Game.CATEGORY_ARRAY_ID, -1));
							intent.putExtra(Game.ACCEPTED_WORDS, correctCount);
                    		intent.putExtra(Game.TOTAL_WORDS, totalCount);
							intent.putStringArrayListExtra(Game.CORRECT_WORD_SET, new ArrayList<String>(correctWords));
							intent.putStringArrayListExtra(Game.INCORRECT_WORD_SET, new ArrayList<String>(incorrectWords));
							startActivity(intent);
							if (!hasStopped()) finish();
                        }


                    });
                }
            });
            totalTimer.start();
        }

        sensor.register();
        sensor.setListener(this);
        remainingDisplay = findViewById(R.id.remainingDisplay);
        phraseDisplay = findViewById(R.id.phraseDisplay);
        backgroundGame = findViewById(R.id.backgroundGame);
		categoryName = findViewById(R.id.category_name);

        int categoryId = getIntent().getIntExtra(Game.CATEGORY_ARRAY_ID, -1);
        if(categoryId != -1){
            String[] phrasesArray = getResources().getStringArray(categoryId);
            categoryName.setText(phrasesArray[0]);
            phrases = new Phrase[phrasesArray.length-1];
            for (int i = 1; i < phrasesArray.length; i++) {
                phrases[i-1] = new Phrase(phrasesArray[i]);
            }
        }
        current = phrases.length-1;
        setNewWord();
        phraseDisplay.setText(phrases[current].text);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensor.unregister();
    }

    private String setNewWord(){
    	String currentWord = phrases[current].text;
        current++;
        if(current == phrases.length) {
            current = 0;
            //RANDOM SHUFFLE
            for (int i = phrases.length - 1; i > 0; i--)
            {
                int index = random.nextInt(i + 1);
                // Simple swap
                Phrase a = phrases[index];
                phrases[index] = phrases[i];
                phrases[i] = a;
            }
        }
        phraseDisplay.setText(phrases[current].text);
        return currentWord;
    }

    @Override
    public void onTiltFinish(boolean front) {

    	String x = setNewWord();

        if(front) {
        	correctCount++;
        	correctWords.add(x);
		}
		else incorrectWords.add(x);

        backgroundGame.setBackgroundColor(Color.WHITE);

        totalCount++;
    }

    @Override
    public void onTiltStart(boolean front) {

        if (front) {
			correctSound.start();
            backgroundGame.setBackgroundColor(Color.GREEN);
        }
        else {
			incorrectSound.start();
            backgroundGame.setBackgroundColor(Color.RED);
        }
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		process.stop();
		finish();
	}
}

class Phrase {
    String text;

    public Phrase(String text) {
        this.text = text;
    }
}


class TiltSensor implements SensorEventListener {

    SensorManager sensorManager = null;
    Sensor rotationSensor = null;
    public TiltSensor(Activity activity){
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public void  register(){
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void unregister(){
        sensorManager.unregisterListener(this, rotationSensor);
    }

    private boolean tiltedFront = false;
    private boolean tiltedBack = false;
    private TiltListener listener;

    private double fromDeg(double rad){
        return rad / 180 * Math.PI;
    }

    public void setRoll(float rollAngle) {
        rollAngle = Math.abs(rollAngle);
        if (rollAngle > fromDeg(135)) {
			if (!tiltedFront) listener.onTiltStart(true);
			tiltedFront = true;
        }
        else if (rollAngle < fromDeg(60)) {
        	if (!tiltedBack) listener.onTiltStart(false);
            tiltedBack = true;
        }
        else if (fromDeg(80) <= rollAngle && rollAngle <= fromDeg(105)) {
            if(listener != null) {
                if (tiltedFront) listener.onTiltFinish(true);
                else if (tiltedBack) listener.onTiltFinish(false);
            }
            tiltedFront = false;
            tiltedBack = false;
        }
    }

    public void setListener(TiltListener listener){
        this.listener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] rotMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotMatrix, event.values);
        float[] orientation = new float[3];
        SensorManager.getOrientation(rotMatrix, orientation);
        setRoll(orientation[2]);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

interface TiltListener {
    void onTiltFinish(boolean front);
    void onTiltStart(boolean front);
}