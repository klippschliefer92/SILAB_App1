package com.example.silab;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    String server = "192.168.2.105";
    int port = 25143;
    Socket s;
    DataOutputStream dos;
    OutputStream os;
    DataInputStream dis;
    InputStream is;
    boolean activate = true;
    boolean laneChangeLeft = false;
    boolean laneChangeRight = false;
    double gasPedal = 0.0;
    double vTarget = 0;

    double speed;


    Button leftButton;
    Button rightButton;
    Button start;
    Button stop;

    SeekBar speedSeekbar;


    //i

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button) findViewById(R.id.startButton);
        leftButton = (Button) findViewById(R.id.leftButton);
        rightButton = (Button) findViewById(R.id.rightButton);
        stop = (Button) findViewById(R.id.stopButton);
        speedSeekbar = (SeekBar) findViewById(R.id.seekBar);



        TCPThread tcpthread = new TCPThread();
        tcpthread.start();




        speedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vTarget = (progress/10) + 0.0333;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




    }


    @Override
    public void onResume(){
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }







    public void links(View view){
        laneChangeLeft = true;
        leftButton.setEnabled(false);
        rightButton.setEnabled(false);
        leftButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                laneChangeLeft = false;
                leftButton.setEnabled(true);
                rightButton.setEnabled(true);
            }
        }, 7000);
    }




    public void rechts(View view){
        laneChangeRight = true;
        rightButton.setEnabled(false);
        leftButton.setEnabled(false);
        rightButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                laneChangeRight = false;
                rightButton.setEnabled(true);
                leftButton.setEnabled(true);
            }
        }, 7000);
    }




    public void los(View view){
        vTarget = 8.33;
        gasPedal = 1.0;
        speedSeekbar.setProgress(83);
    }





    public void stop(View view){
        speedSeekbar.setProgress(83);
        vTarget = 0.0001;
    }



    public class TCPThread extends Thread{

        public void run(){

            try {
                s = new Socket(server, port);
                os = s.getOutputStream();
                dos = new DataOutputStream(os);
                is = s.getInputStream();
                dis = new DataInputStream(is);

                while (s.isConnected()){
                    speed = dis.readDouble();

                    dos.writeBoolean(activate);
                    dos.writeBoolean(laneChangeLeft);
                    dos.writeBoolean(laneChangeRight);
                    dos.writeDouble(gasPedal);
                    dos.writeDouble(vTarget);

                }



            }
             catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    };

}
