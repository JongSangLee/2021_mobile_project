package com.example.reversi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PlayActivity extends AppCompatActivity {

    TableLayout table;
    TextView explainLog;
    TextView turnLog;
    public int clickStat = 1;//1:player1 2:player2

    boolean endFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity);

        Button back_button = (Button) findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        //텍스트뷰 연결
        turnLog = (TextView)findViewById(R.id.turnLog);
        explainLog = (TextView)findViewById(R.id.explainLog);

        //테이블 생성
        table = (TableLayout) findViewById(R.id.tableLayout);
        ReversiButton[][] buttons = new ReversiButton[8][8];

        for(int i=0;i<8;i++){
            TableRow tableRow = new TableRow(this);
            for(int j=0;j<8;j++){
                buttons[i][j] = new ReversiButton(this, i, j);

                //디자인 관련 코드
                buttons[i][j].setBackgroundResource(R.drawable.board);

                //버튼에 OnClick 등록
                buttons[i][j].setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){
                        clickFunc(buttons, (ReversiButton)view);
                    }
                });
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1.0f);
                buttons[i][j].setLayoutParams(layoutParams);
                tableRow.addView(buttons[i][j]);
            }
            table.addView(tableRow);
        }

        //처음 돌 4개 배치
        buttons[3][4].setStone(1);
        buttons[4][3].setStone(1);
        buttons[3][3].setStone(2);
        buttons[4][4].setStone(2);

        checkSettablePosition(buttons,clickStat);
    }

    //클릭 했을 때 실행되는 함수
    public void clickFunc(ReversiButton[][]buttons, ReversiButton view){
        //돌을 놓을 수 있는 칸을 눌렀을 때
        if(view.isSettable){
            //돌을 놓는다
            view.setStone(clickStat);
            flipAllFlipableStone(buttons, view.posX, view.posY, clickStat);
            initPosition(buttons);

            if(checkEnd((ReversiButton) view)){
                /*게임 종료*/
            }
            else {
                //턴을 바꾸고 정보를 갱신한다
                if (clickStat == 1) { //player1:검은돌
                    turnLog.setText("White Turn");
                    explainLog.setText("player2의 차례입니다.");
                    clickStat = 2;
                    checkSettablePosition(buttons, clickStat);
                    if(isAlldisSettable(buttons)){
                        turnLog.setText("Black Turn");
                        explainLog.setText("둘 곳이 없어서 턴을 계속 진행합니다");
                        clickStat = 1;
                        checkSettablePosition(buttons, clickStat);
                        //나도 둘 곳이 없다면
                        if(isAlldisSettable(buttons)){
                            endFlag = true;
                            checkEnd((ReversiButton) view);
                        }


                    }
                } else{ //player2:흰 돌
                    turnLog.setText("Black Turn");
                    explainLog.setText("player1의 차례입니다.");
                    clickStat = 1;
                    checkSettablePosition(buttons, clickStat);
                    if(isAlldisSettable(buttons)){
                        turnLog.setText("White Turn");
                        explainLog.setText("둘 곳이 없어서 턴을 계속 진행합니다");
                        clickStat = 2;
                        checkSettablePosition(buttons, clickStat);
                        //나도 둘 곳이 없다면
                        if(isAlldisSettable(buttons)){
                            endFlag = true;
                            checkEnd((ReversiButton) view);
                        }
                    }
                }

            }
        }
        //돌을 놓을 수 없는 칸을 눌렀을 때
        else{
            explainLog.setText("잘못된 위치입니다.");
        }
    }

    public void initPosition(ReversiButton[][] buttons){
        for(int x=0;x<8;x++){
            for(int y=0;y<8;y++){
                //둘 수 있는 자리라면
                buttons[x][y].setSettable(false);
                if(buttons[x][y].player == -1) {
                    buttons[x][y].setBackgroundResource(R.drawable.board);
                    for(int j=0; j<8; j++)
                        buttons[x][y].direction[j]=false;
                }
            }
        }
    }

    public void checkSettablePosition(ReversiButton[][] buttons,int player){
        //특정 플레이어가 둘 수 있는 곳을 검사하는 함수
        for(int x=0;x<8;x++){
            for(int y=0;y<8;y++){
                //둘 수 있는 자리라면
                if(isSettable(buttons,buttons[x][y],player)) {
                    //해당 버튼의 isSettable을 true로 설정
                    buttons[x][y].setSettable(true);
                    //이 버튼의 이미지를 변경
                    buttons[x][y].setBackgroundResource(R.drawable.flipable);
                }
            }
        }
    }

    public boolean isSettable(ReversiButton[][] buttons, ReversiButton btn, int player){
        //이 자리가 다른 돌을 뒤집을 수 있는 자리인지 검사한다
        int x = btn.posX;
        int y = btn.posY;
        int num = 0;
        //빈 칸이 아니면 날림
        if(buttons[x][y].player != -1)
            return false;
        //1)주변 8자리에 다른 색 돌이 있는지 검사한다
        if(checkInBoundary(x-1,y+1) && findAnotherStone(buttons, x-1, y+1, player)){
            if(findFlippableStonesToOneDirection(buttons,buttons[x-1][y+1],-1,1, player)){
                buttons[x][y].direction[5]=true;
                num++;
            }
        }
        if(checkInBoundary(x,y+1) && findAnotherStone(buttons, x, y+1, player)){
            if(findFlippableStonesToOneDirection(buttons,buttons[x][y+1],0,1, player)){
                buttons[x][y].direction[4]=true;
                num++;
            }

        }
        if(checkInBoundary(x+1,y+1) && findAnotherStone(buttons, x+1, y+1, player)){
            if(findFlippableStonesToOneDirection(buttons,buttons[x+1][y+1],1,1, player)){
                buttons[x][y].direction[7]=true;
                num++;
            }

        }
        if(checkInBoundary(x-1,y) && findAnotherStone(buttons, x-1, y, player)){
            if(findFlippableStonesToOneDirection(buttons,buttons[x-1][y],-1,0, player)){
                buttons[x][y].direction[3]=true;
                num++;
            }

        }
        if(checkInBoundary(x+1,y) && findAnotherStone(buttons, x+1, y, player)) {
            if(findFlippableStonesToOneDirection(buttons,buttons[x+1][y],1,0, player)){
                buttons[x][y].direction[6]=true;
                num++;
            }

        }
        if(checkInBoundary(x-1,y-1) && findAnotherStone(buttons, x-1, y-1, player)){
            if(findFlippableStonesToOneDirection(buttons,buttons[x-1][y-1],-1,-1, player)){
                buttons[x][y].direction[0]=true;
                num++;
            }

        }
        if(checkInBoundary(x,y-1) && findAnotherStone(buttons, x, y-1, player)){
            if(findFlippableStonesToOneDirection(buttons,buttons[x][y-1],0,-1, player)){
                buttons[x][y].direction[1]=true;
                num++;
            }

        }
        if(checkInBoundary(x+1,y-1) && findAnotherStone(buttons, x+1, y-1, player)){
            if(findFlippableStonesToOneDirection(buttons,buttons[x+1][y-1],1,-1, player)){
                buttons[x][y].direction[2]=true;
                num++;
            }

        }
        if(num>0)
            return true;
        return false;
    }

    public boolean findAnotherStone(ReversiButton[][] buttons, int x, int y, int player){
        if(buttons[x][y].player != player && buttons[x][y].player != -1)
            return true;
        return false;
    }

    public boolean findFlippableStonesToOneDirection(ReversiButton[][] buttons, ReversiButton btn,int directionX, int directionY, int player){
        int nextX = btn.posX+directionX;
        int nextY = btn.posY+directionY;



        if(checkInBoundary(nextX, nextY)){
            //놓고 싶은 돌과 같은 색 돌을 찾아보자
            if(player == buttons[nextX][nextY].player)
                return true;
            //놓고 싶은 돌과 다른색 돌이 있으면 그 방향으로 진행한다


            if(player != buttons[nextX][nextY].player && buttons[nextX][nextY].player != -1){
                if(findFlippableStonesToOneDirection(buttons,buttons[nextX][nextY],directionX,directionY,player)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkInBoundary(int x, int y){
        if( 0 <= x && 7 >= x && 0 <= y && 7 >= y)
            return true;
        else
            return false;
    }

    public void flipAllFlipableStone(ReversiButton[][] buttons, int x, int y, int player) {
        for (int i = 0; i < 8; i++) {
            if (buttons[x][y].direction[i] == true) {
                flipDirection(buttons, x, y, player, i);
                buttons[x][y].direction[i] = false;
            }
        }
    }

    public void flipDirection(ReversiButton[][] buttons, int x, int y, int player, int direct){
        int a, b;
        switch (direct){
            case 0 : flip(buttons, x, y, player, -1, -1);
                break;
            case 1 : flip(buttons, x, y, player, 0, -1);
                break;
            case 2 : flip(buttons, x, y, player, 1, -1);
                break;
            case 3 : flip(buttons, x, y, player, -1, 0);
                break;
            case 4 : flip(buttons, x, y, player, 0, 1);
                break;
            case 5 : flip(buttons, x, y, player, -1, 1);
                break;
            case 6 : flip(buttons, x, y, player, 1, 0);
                break;
            case 7 : flip(buttons, x, y, player, 1, 1);
                break;
            default:
                break;
        }
    }

    public void flip(ReversiButton[][] buttons, int x, int y, int player, int directionX, int directionY){
        int nextX = x + directionX;
        int nextY = y + directionY;
        //
        while(buttons[nextX][nextY].player!=player && buttons[nextX][nextY].player>0){
            buttons[nextX][nextY].flipStone();
            nextX = nextX + directionX;
            nextY = nextY + directionY;
        }
    }

    public boolean checkEnd(ReversiButton btn){
        Button back_button = (Button) findViewById(R.id.back_button);
        String win;
        if(btn.p1Cnt > btn.p2Cnt) {
            win = "Player1이 승리하였습니다";
        }
        else if (btn.p1Cnt == btn.p2Cnt){
            win = "무승부";
        }
        else win = "Player2가 승리하였습니다";
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(PlayActivity.this)
                .setTitle("게임 종료")
                .setMessage(win)
                .setPositiveButton("메인 화면으로", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        AlertDialog msgDlg = msgBuilder.create();

        //모든 칸이 꽉 찼을 때
        if(btn.p1Cnt + btn.p2Cnt == 64){
            back_button.setVisibility(View.VISIBLE);
            msgDlg.show();
            return true;
        }
        //한쪽의 돌이 0이 되었을 때
        if(btn.p1Cnt==0 || btn.p2Cnt == 0){
            back_button.setVisibility(View.VISIBLE);
            msgDlg.show();
            return true;
        }

        //둘 다 둘 곳이 없다면
        if(endFlag){
            back_button.setVisibility(View.VISIBLE);
            msgDlg.show();
            return true;
        }
        return false;
    }

    public boolean isAlldisSettable(ReversiButton[][] buttons){
        for(int x=0;x<8;x++){
            for(int y=0;y<8;y++){
                if(buttons[x][y].isSettable==true)
                    return false;
            }
        }
        return true;
    }
}