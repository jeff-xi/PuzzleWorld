package com.example.jeff.puzzleworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by Jeff on 2017/1/23.
 */

public class MainActivity extends Activity implements View.OnClickListener {
    private TextView play_game, how_toplay, game_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the Activity full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        play_game = (TextView) findViewById(R.id.play_game);
        how_toplay = (TextView) findViewById(R.id.how_toplay);
        game_mode = (TextView) findViewById(R.id.game_mode);

        play_game.setOnClickListener(this);
        how_toplay.setOnClickListener(this);
        game_mode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.play_game:
                intent = new Intent(this, MainGameplay.class);
                intent.putExtra("code", 0);
                intent.putExtra("column", 3);
                break;
            case R.id.how_toplay:
                intent = new Intent(this, MainHowtoplay.class);
                break;
            case R.id.game_mode:
                intent = new Intent(this, MainGamemode.class);
                break;
        }
        this.startActivity(intent);
    }
}
