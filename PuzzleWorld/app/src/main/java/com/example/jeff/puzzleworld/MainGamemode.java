package com.example.jeff.puzzleworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Jeff on 2017/1/23.
 */

public class MainGamemode extends Activity implements View.OnClickListener {
    private Button amature, medium, hard,back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.gamemode);
        amature = (Button) findViewById(R.id.amature);
        medium = (Button) findViewById(R.id.medium);
        hard = (Button) findViewById(R.id.hard);
        back = (Button) findViewById(R.id.back);

        //Select Difficulty
        amature.setOnClickListener(this);
        medium.setOnClickListener(this);
        hard.setOnClickListener(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) { //Jump to the corresponding difficulty of the game
        Intent intent = new Intent(this, MainGameplay.class);
        switch (v.getId()) {
            case R.id.amature:
                intent.putExtra("column", 3);

                break;
            case R.id.medium:
                intent.putExtra("column", 4);

                break;
            case R.id.hard:
                intent.putExtra("column", 5);

                break;
        }
        //Whether directly into the game 0 directly into, 1 set the difficulty of the game
        intent.putExtra("code", 1);
        this.startActivity(intent);
    }
}
