package com.ibm.hospitaltranhelper;

import android.app.Activity;
import android.content.Intent;


import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.Timer;
import java.util.TimerTask;


public class ChatActivity extends Activity {
    private RelativeLayout layout;
    private ImageButton imgBtnBack;
    private ImageButton imgBtnSpeak;
    private ImageButton imgBtnChartRecord;
    private TextView txtTarget;
    private TextView txtSource;
    private RelativeLayout progressLayout;

    private ImageView  imgViewProgres;
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

    private long RUN_MAX_TIME = 15000;//设置运行时间不超过8s

    private Thread runThread;

    private String wavpath;
    private String where;
    private String sourcelang;
    private String targetlang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        imgViewProgres= (ImageView)findViewById(R.id.imgviewprogress);

        if (where == "right") {
            // layout.setBackgroundColor(Color.parseColor("#8a71bf") );
            layout.setBackgroundColor(getResources().getColor(R.color.purple));

        } else {
            layout.setBackgroundColor(Color.parseColor("#21c7e8"));
        }

    }
   private  AnimationDrawable animDraw;
    private void initData() {
        Resources res = getResources();
        animDraw = (AnimationDrawable) res.getDrawable(R.drawable.progressanimation);
        imgViewProgres.setImageDrawable(animDraw);
        animDraw.start();

        if (NetUtils.isConnected(getApplicationContext())) {
            RunThread();//启动调用watson的线程

            handler = new Handler() {
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what){
                        case 1:
                            txtSource.setText(sttStr);
                            txtTarget.setText(tttStr);
                            progressLayout.setVisibility(View.GONE);

                            animDraw.stop();
                            imgViewProgres.setVisibility(View.GONE);
                            txtSource.setVisibility(View.VISIBLE);
                            txtTarget.setVisibility(View.VISIBLE);
                            imgBtnBack.setVisibility(View.VISIBLE);
                            imgBtnSpeak.setVisibility(View.VISIBLE);
                            imgBtnChartRecord.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            playWav();
                            break;
//                        case 3:
//                            goHome();
//                            break;
                    }
                    
                }
            };

         //   handler.sendEmptyMessageDelayed(3,RUN_MAX_TIME);

        } else {
            Toast.makeText(getApplicationContext(), "Your phone without Internet!",
                    Toast.LENGTH_LONG).show();
            finish();
        }

    }

    private void playWav() {
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



    //运行线程
    void RunThread() {
        runThread = new Thread(runRunable);
        runThread.start();
    }

    private Runnable runRunable = new Runnable() {
        @Override
        public void run() {


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
    };


    private void goHome(){
        Toast.makeText(getApplicationContext(),"运行时间超过"+RUN_MAX_TIME/1000+"亲,你的网络有问题呀!请待会再试验!",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ChatActivity.this,MainActivity.class);
        startActivity(intent);
    // System.exit(0);
        this.finish();
    }


}
