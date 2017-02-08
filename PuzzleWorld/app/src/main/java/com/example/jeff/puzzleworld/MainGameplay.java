package com.example.jeff.puzzleworld;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import puzzleworld.utils.GamePintuLayout;

public class MainGameplay extends Activity {

    private GamePintuLayout mGamePintuLayout;
    private TextView mLevel;
    private TextView mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the Activity full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.gameplay);

        mTime = (TextView) findViewById(R.id.id_time);
        mLevel = (TextView) findViewById(R.id.id_level);
        //Back button and listen for events
        Button button = (Button) findViewById(R.id.back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mGamePintuLayout = (GamePintuLayout) findViewById(R.id.id_gamepintu);
        Intent intent = getIntent();
        mGamePintuLayout.mColumn = intent.getIntExtra("column", 3);
        mGamePintuLayout.code = intent.getIntExtra("code", 0);
        mGamePintuLayout.setTimeEnabled(true);

        mGamePintuLayout.setOnGamePintuListener(new GamePintuLayout.GamePintuListener() {
            @Override
            public void timechanged(int currentTime) {
                mTime.setText("" + currentTime);
            }

            @Override
            public void nextLevel(final int nextLevel) {
                new AlertDialog.Builder(MainGameplay.this)
                        .setTitle("Info").setMessage("You have won the game!")
                        .setPositiveButton("Next level", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mGamePintuLayout.nextLevel();
                                mLevel.setText("" + nextLevel);
                            }
                        }).show();
            }

            @Override
            public void gameover() {
                new AlertDialog.Builder(MainGameplay.this)
                        .setTitle("Info").setMessage("Sorry you lose the game!")
                        .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mGamePintuLayout.restart();
                            }
                        }).setNegativeButton("QUIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        mGamePintuLayout.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGamePintuLayout.resume();
    }


}
