package com.ibm.hospitaltranhelper;

import android.app.Activity;
import android.content.Intent;


import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.hospitaltranhelper.chatrecord.ChatRecordActivity;
import com.ibm.hospitaltranhelper.utils.NetUtils;
import com.ibm.hospitaltranhelper.utils.TranslationHelper;
import com.ibm.hospitaltranhelper.utils.WaveFileReader;
import com.ibm.hospitaltranhelper.utils.WaveFileWriter;

import java.io.File;
import java.io.IOException;


public class ChatActivity extends Activity {
    private RelativeLayout layout;
    private ImageButton imgBtnBack;
    private ImageButton imgBtnSpeak;
    private ImageButton imgBtnChartRecord;
    private TextView txtTarget;
    private TextView txtSource;
    private RelativeLayout progressLayout;
    private String sttStr = "";
    private String tttStr = "";

    private Handler handler;

    private MediaPlayer mMediaPlayer;

    private boolean playState = false; // 录音的播放状态

    private String getWatsonWav = Environment.getExternalStorageDirectory()
            + "/watsonWav.wav";
    private String getNewWatsonWav = Environment.getExternalStorageDirectory()
            + "/watsonNewWav.wav";
    private File watsonWav;
    private File watsonNewWav;
    private File watsonwavtowav;


    private String wavpath;
    private String where;
    private String sourcelang;
    private String targetlang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在Android2.2以后必须添加以下代码
        // 本应用采用的Android4.0
        // 设置线程的策略
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        // 设置虚拟机的策略
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog().penaltyDeath().build());

        if (!NetUtils.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "手机没有连上网！，请联网后再重试！",
                    Toast.LENGTH_LONG).show();
        }
        setContentView(R.layout.activity_chat);
        Intent it = getIntent();
        wavpath = it.getStringExtra("wavpath");
        sourcelang = it.getStringExtra("sourceLang");
        targetlang = it.getStringExtra("targetLang");
        where = it.getStringExtra("where");


        initView();
        initData();
        setClickListener();
    }


    private void initView() {
        layout = (RelativeLayout) findViewById(R.id.layout);
        imgBtnBack = (ImageButton) findViewById(R.id.imgbtn_back);
        imgBtnSpeak = (ImageButton) findViewById(R.id.imgbtn_speak);
        imgBtnChartRecord = (ImageButton) findViewById(R.id.imgbtn_chatrecord);
        txtSource = (TextView) findViewById(R.id.txt_source);
        txtTarget = (TextView) findViewById(R.id.txt_target);
        progressLayout = (RelativeLayout) findViewById(R.id.progress);

        if(where == "right"){
           // layout.setBackgroundColor(Color.parseColor("#8a71bf") );
            layout.setBackgroundColor(getResources().getColor(R.color.purple));

        }else{
            layout.setBackgroundColor(Color.parseColor("#21c7e8"));
        }

      //  Toast.makeText(getApplicationContext(), "where---:" + where + "  sourcelang---:" + sourcelang + "    targetlang-----:" + targetlang, Toast.LENGTH_LONG).show();

    }

    private void initData() {


        new Thread(new Runnable() {
            @Override
            public void run() {
          /*      Message m = new Message();
                m.what = 0;
                handler.sendMessage(m);*/

                Message m1 = new Message();
                m1.what = 1;
                sttStr = TranslationHelper.speechToText(wavpath, sourcelang);

                tttStr = TranslationHelper.translate(sttStr, sourcelang, targetlang);

                handler.sendMessage(m1);

                Message m = new Message();
                m.what = 2;
                watsonWav = TranslationHelper.textToSpeech(tttStr, getWatsonWav, targetlang);
                watsonNewWav = new File(getNewWatsonWav);
                try {
                    watsonwavtowav = watsonWavToWav(getWatsonWav, getNewWatsonWav);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                handler.sendMessage(m);

            }
        }).start();

        handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == 1) {

                    txtSource.setText(sttStr);
                    txtTarget.setText(tttStr);
                    progressLayout.setVisibility(View.GONE);
                    txtSource.setVisibility(View.VISIBLE);
                    txtTarget.setVisibility(View.VISIBLE);
                    imgBtnBack.setVisibility(View.VISIBLE);
                    imgBtnSpeak.setVisibility(View.VISIBLE);

                }else  if(msg.what == 2){
                    playWav();
                    imgBtnChartRecord.setVisibility(View.VISIBLE);
                }


            }
        };


    }

    private void playWav(){
        if (!playState) {
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(getNewWatsonWav);
                mMediaPlayer.prepare();

                playState = true;
                mMediaPlayer.start();

                // 设置播放结束时监听
                mMediaPlayer
                        .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if (playState) {
                                    playState = false;
                                }
                            }
                        });
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                playState = false;
            } else {
                playState = false;
            }
        }
    }
    private void setClickListener() {
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(it);
            }
        });
        imgBtnChartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ChatActivity.this, ChatRecordActivity.class);
                startActivity(it);
            }
        });
        //点击重新播放录音
        imgBtnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              playWav();
            }
        });
    }

    public File watsonWavToWav(String wavfilename, String outputfilename) throws IOException {

        File wavfile = new File(wavfilename);
        long wavfile_length = 0;
        wavfile_length = wavfile.length();

        long data_length = (wavfile_length - 78);

        WaveFileReader reader = new WaveFileReader(wavfilename, true, data_length);

        System.out.println(reader.getByteRate());
        System.out.println(reader.getDataLen());
        WaveFileWriter writer = new WaveFileWriter();

        File output_file = new File(outputfilename);
        int[] data = new int[reader.getNumChannels() * reader.getDataLen()];
        for (int i = 0; i < reader.getNumChannels(); i++)
            for (int j = 0; j < reader.getDataLen(); j++) {

                data[i * reader.getDataLen() + j] = reader.getData()[i][j];
            }
        writer.write(data, (int) reader.getSampleRate(), reader.getByteRate(), output_file);
        return output_file;
    }





}
