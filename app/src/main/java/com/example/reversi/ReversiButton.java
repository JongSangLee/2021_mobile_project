package com.example.reversi;

import android.content.Context;
import java.lang.reflect.Array;
import java.util.Arrays;
import androidx.appcompat.widget.AppCompatButton;


public class ReversiButton extends AppCompatButton{
    public static int p1Cnt = 0;
    public static int p2Cnt = 0;

    Context ctx;
    int posX, posY;
    int player = -1;
    boolean isSettable = false;
    boolean[] direction = {false, false, false, false, false, false, false, false};

    public ReversiButton(Context context, int x, int y){
        super(context);
        ctx = context;
        posX = x;
        posY = y;
    }

    public void setStone(int _player){
        //이 칸에 돌이 있는 것으로 표시한다
        player = _player;
        //이 칸을 돌을 둘 수 없는 곳으로 표시한다
        isSettable = false;
        //이 칸을 선택 불가로 만든다
        this.setClickable(false);

        //플레이어에 맞는 돌을 배치한다
        if(_player==1){
            //player1의 돌을 놓는다
            this.setBackgroundResource(R.drawable.black);
            p1Cnt++;
        }
        else{
            //player2의 돌을 놓는다
            this.setBackgroundResource(R.drawable.white);
            p2Cnt++;
        }
    }

    public void flipStone(){
        if(player==-1){
            //불가능
        }
        else if(player==1){
            //player1의 돌이므로 현재 검은색
            //흰색으로 바꿔준다
            this.setBackgroundResource(R.drawable.white);
            //player를 바꿔준다
            player = 2;
            p1Cnt--;
            p2Cnt++;
        }
        else{
            //player2의 돌 이므로 현재 검은색
            //검은색으로 바꿔준다
            this.setBackgroundResource(R.drawable.black);
            //player를 바꿔준다
            player = 1;
            p1Cnt++;
            p2Cnt--;
        }
    }
    public void setSettable(boolean flag){
        isSettable = flag;
    }
}