package com.daniil.myapplication;

import static java.lang.System.exit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button m_startButton;
    Button m_exitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_startButton = findViewById(R.id.startGameBut);
        m_exitButton = findViewById(R.id.exitBut);
        m_exitButton.setOnClickListener(view -> {
            finish();}
        );
        m_startButton.setOnClickListener(view -> {
            startActivity(new Intent(this,BallApp.class));
        });
    }
}