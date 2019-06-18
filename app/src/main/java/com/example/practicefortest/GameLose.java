package com.example.practicefortest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameLose extends AppCompatActivity {
    String score ;
   String lives ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lose);
        Button btn = findViewById(R.id.play);
        TextView txt = findViewById(R.id.textView2);
        Intent intent = getIntent();
         score = intent.getStringExtra("score");
         lives = intent.getStringExtra("lives");
        if(score != null)
        {
            txt.setText(score);
        }
        if(lives != null){
            txt.setText(lives);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                view.getContext().startActivity(intent);}
        });




    }
}
