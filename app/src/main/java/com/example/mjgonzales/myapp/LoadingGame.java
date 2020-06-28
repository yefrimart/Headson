package com.example.mjgonzales.myapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LoadingGame extends AppCompatActivity {

    private Handler x = new Handler();
    private Thread countDownThread = null;
	private StoppableRunneable process = null;
    private int progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_game);
        final TextView countdownText = findViewById(R.id.countdownText);
		final MediaPlayer startSound = MediaPlayer.create(this, R.raw.start_sound);
        countDownThread = new Thread(process = new StoppableRunneable() {
            @Override
            public void run() {
                for (int i = 5; i > 0; --i) {
                    progress = i;
                    x.post(new Runnable() {
                        @Override
                        public void run() {
                            countdownText.setText(String.valueOf(progress));
                        }
                    });
                    if(hasStopped())return;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                x.post(new Runnable() {
                    @Override
                    public void run() {
                    	if(hasStopped()) return;
                    	startSound.start();
						Intent intent = new Intent(LoadingGame.this, Game.class);
						intent.putExtra(Game.CATEGORY_ARRAY_ID, getIntent().getIntExtra(Game.CATEGORY_ARRAY_ID, -1));
						startActivity(intent);
						finish();
                    }
                });
                if (!hasStopped()) finish();
            }
        });
        countDownThread.start();
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		process.stop();
		finish();
	}
}

abstract class StoppableRunneable implements Runnable
{
	private boolean stopped = false;
	public void stop() {
		stopped = true;
	}
	public boolean hasStopped() {
		return stopped;
	}
}

