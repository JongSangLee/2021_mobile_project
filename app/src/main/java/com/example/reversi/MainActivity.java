package com.example.reversi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//게임 플레이 버튼 클릭시 액티비티 전환
        Button play_game_btn = (Button) findViewById(R.id.play_game_btn);
        play_game_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                startActivity(intent);
            }
        });
        }
}