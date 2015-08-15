package com.ibm.hospitaltranhelper;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.hospitaltranhelper.config.Constants;
import com.ibm.hospitaltranhelper.utils.AudioFileFunc;
import com.ibm.hospitaltranhelper.utils.AudioRecordFunc;
import com.ibm.hospitaltranhelper.utils.ErrorCode;
import com.ibm.hospitaltranhelper.utils.NetUtils;


public class MainActivity extends Activity {

    private String leftSelectLang = Constants.ENGLISH;
    private String rightSelectLang = Constants.SPANISH;

    private ImageButton imgBtnDoctor;
    private ImageButton imgBtnPatient;
    private ImageButton imgBtnDoctorLang;
    private ImageButton imgBtnPatientLang;


    private ImageView mRecordLight_1;
    private ImageView mRecordLight_2;
    private ImageView mRecordLight_3;

    private ImageView mRecordLight_11;
    private ImageView mRecordLight_12;
    private ImageView mRecordLight_13;


    private Animation mRecordLight_1_Animation;
    private Animation mRecordLight_2_Animation;
    private Animation mRecordLight_3_Animation;
    private Animation mRecordLight_11_Animation;
    private Animation mRecordLight_12_Animation;
    private Animation mRecordLight_13_Animation;


    private static final int RECORD_ING = 1; // 正在录音
    private static final int RECORD_ED = 2; // 完成录音


    private static final float MIN_TIME = 1;// 最短录音时间
    private float recordTime = 0.0f; // 录音时长


    private int mRecord_State = 0; // 录音的状态

    private AudioRecordFunc mRecordMav;

    private Thread mRecordThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (!NetUtils.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Mobile phone without Internet!",
                    Toast.LENGTH_LONG).show();
        }
        initView();
        setClickListener();

    }

    private void initView() {
        imgBtnDoctor = (ImageButton) findViewById(R.id.imgbtn_doctor);
        imgBtnPatient = (ImageButton) findViewById(R.id.imgbtn_patient);
        imgBtnDoctorLang = (ImageButton) findViewById(R.id.imgbtn_doctorlang);
        imgBtnPatientLang = (ImageButton) findViewById(R.id.imgbtn_patientlang);


        mRecordLight_1 = (ImageView) findViewById(R.id.voice_recordinglight_1);
        mRecordLight_2 = (ImageView) findViewById(R.id.voice_recordinglight_2);
        mRecordLight_3 = (ImageView) findViewById(R.id.voice_recordinglight_3);

        mRecordLight_11 = (ImageView) findViewById(R.id.voice_recordinglight_11);
        mRecordLight_12 = (ImageView) findViewById(R.id.voice_recordinglight_12);
        mRecordLight_13 = (ImageView) findViewById(R.id.voice_recordinglight_13);


    }

    private void setClickListener() {

        //点击医生项目的框选择语言
        imgBtnDoctorLang.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SelectLangDialog selectLangDialog;
                selectLangDialog = new SelectLangDialog(MainActivity.this, R.style.dialog, new SelectLangDialog.OnSelectLangDialogListener() {

                    @Override
                    public void back(String lang) {
                        switch (lang) {
                            case "en":
                                imgBtnDoctorLang.setBackgroundResource(R.mipmap.banner_english);
                                leftSelectLang = Constants.ENGLISH;
                               // Toast.makeText(getApplicationContext(), leftSelectLang, Toast.LENGTH_LONG).show();
                                break;
                            case "es":
                                imgBtnDoctorLang.setBackgroundResource(R.mipmap.banner_spanish);
                                leftSelectLang = Constants.SPANISH;
                               // Toast.makeText(getApplicationContext(), lang, Toast.LENGTH_LONG).show();
                                break;
                            case "otherlang":
                                Toast.makeText(getApplicationContext(), "It cannot Specch to Text", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(getApplicationContext(), "It cannot Specch to Text", Toast.LENGTH_LONG).show();
                                break;

                        }

                    }
                });
                Window window = selectLangDialog.getWindow();
                window.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                WindowManager.LayoutParams params = window.getAttributes();

                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int screenWidth = dm.widthPixels;

                params.x = screenWidth / 6;
                params.y = -30;
                params.width = 400;
                window.setAttributes(params);
                selectLangDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
                selectLangDialog.show();
            }
        });
        //点击病人项目的框选择语言
        imgBtnPatientLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectLangDialog selectLangDialog;
                selectLangDialog = new SelectLangDialog(MainActivity.this, R.style.dialog, new SelectLangDialog.OnSelectLangDialogListener() {

                    @Override
                    public void back(String lang) {
                        switch (lang) {
                            case "en":
                                imgBtnPatientLang.setBackgroundResource(R.mipmap.banner_english);
                                rightSelectLang = Constants.ENGLISH;
                              //  Toast.makeText(getApplicationContext(), lang, Toast.LENGTH_LONG).show();
                                break;
                            case "es":
                                imgBtnPatientLang.setBackgroundResource(R.mipmap.banner_spanish);
                                rightSelectLang = Constants.SPANISH;
                             //   Toast.makeText(getApplicationContext(), rightSelectLang, Toast.LENGTH_LONG).show();
                                break;
                            case "otherlang":
                                Toast.makeText(getApplicationContext(), "It cannot Specch to Text", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(getApplicationContext(), "It cannot Specch to Text", Toast.LENGTH_LONG).show();
                                break;

                        }

                    }
                });
                Window window = selectLangDialog.getWindow();
                window.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                WindowManager.LayoutParams params = window.getAttributes();

                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int screenWidth = dm.widthPixels;

                params.x = screenWidth / 6;
                params.y = -30;
                params.width = 600;
                window.setAttributes(params);
                selectLangDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
                selectLangDialog.show();
            }
        });

        imgBtnDoctor.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    //开始录音
                    case MotionEvent.ACTION_DOWN:
                        if (mRecord_State != RECORD_ING) {
                            imgBtnDoctor.setBackgroundResource(R.mipmap.recording);
                            //开始动画效果
                            startRecordLightAnimation();
                            //修改录音状态
                            mRecord_State = RECORD_ING;
                            //录音开始了
                            startRecordMav();
                            recordTimethread();
                        }
                        break;
                    //停止录音
                    case MotionEvent.ACTION_UP:
                        // 停止动画效果
                        stopRecordLightAnimation();
                        // 修改录音状态
                        mRecord_State = RECORD_ED;
                        stopRecordMav();


                        if (recordTime < MIN_TIME) {
                            Toast.makeText(getApplicationContext(), "Please speak more time!"  , Toast.LENGTH_LONG).show();

                            recordTime = 0;
                        } else {
                            Intent it = new Intent();
                            it.setClass(MainActivity.this, ChatActivity.class);

                            it.putExtra("wavpath", AudioFileFunc.getWavFilePath());
                            it.putExtra("sourceLang", leftSelectLang);
                            it.putExtra("targetLang", rightSelectLang);
                            it.putExtra("where", "left");
                            startActivity(it);
                        }
                        imgBtnDoctor.setBackgroundResource(R.mipmap.doctor);

                        break;
                }
                return false;
            }
        });

        //病人选择开始录音了
        imgBtnPatient.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    //开始录音
                    case MotionEvent.ACTION_DOWN:
                        if (mRecord_State != RECORD_ING) {
                            imgBtnPatient.setBackgroundResource(R.mipmap.recording);
                            //开始动画效果
                            startRecordLightAnimationRight();
                            //修改录音状态
                            mRecord_State = RECORD_ING;
                            //录音开始了

                            startRecordMav();

                            recordTimethread();
                        }
                        break;
                    //停止录音
                    case MotionEvent.ACTION_UP:
                        // 停止动画效果
                        stopRecordLightAnimationRight();
                        // 修改录音状态
                        mRecord_State = RECORD_ED;
                        stopRecordMav();

                        if (recordTime < MIN_TIME) {
                            Toast.makeText(getApplicationContext(), "Please speak more time!" , Toast.LENGTH_LONG).show();

                            recordTime = 0;
                        } else {
                            Intent it = new Intent();
                            it.setClass(MainActivity.this, ChatActivity.class);
                            it.putExtra("wavpath", AudioFileFunc.getWavFilePath());
                            it.putExtra("sourceLang", rightSelectLang);
                            it.putExtra("targetLang", leftSelectLang);
                            it.putExtra("where", "right");
                            startActivity(it);
                        }
                        imgBtnPatient.setBackgroundResource(R.mipmap.patient);
                        break;
                }
                return false;
            }
        });


    }


    /**
     * 开始录音
     */
    private void startRecordMav() {
        int mResult = -1;
        mRecordMav = AudioRecordFunc.getInstance();
        mResult = mRecordMav.startRecordAndFile();
        if (mResult == ErrorCode.SUCCESS) {
            Log.i("record", "Record sucess!");
        } else {
            Log.i("record", "Record failure!");
        }

    }

    /**
     * 停止录音
     */
    private void stopRecordMav() {
        mRecordMav.stopRecordAndFile();
    }

    /**
     * 用来控制动画效果
     */
    Handler mRecordLightHandler = new Handler() {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (mRecord_State == RECORD_ING) {
                        mRecordLight_1.setVisibility(View.VISIBLE);
                        mRecordLight_1_Animation = AnimationUtils.loadAnimation(
                                MainActivity.this, R.anim.voice_anim);
                        mRecordLight_1.setAnimation(mRecordLight_1_Animation);
                        mRecordLight_1_Animation.startNow();
                    }
                    break;
                case 1:
                    if (mRecord_State == RECORD_ING) {
                        mRecordLight_2.setVisibility(View.VISIBLE);
                        mRecordLight_2_Animation = AnimationUtils.loadAnimation(
                                MainActivity.this, R.anim.voice_anim);
                        mRecordLight_2.setAnimation(mRecordLight_2_Animation);
                        mRecordLight_2_Animation.startNow();
                    }
                    break;
                case 2:
                    if (mRecord_State == RECORD_ING) {
                        mRecordLight_3.setVisibility(View.VISIBLE);
                        mRecordLight_3_Animation = AnimationUtils.loadAnimation(
                                MainActivity.this, R.anim.voice_anim);
                        mRecordLight_3.setAnimation(mRecordLight_3_Animation);
                        mRecordLight_3_Animation.startNow();
                    }
                    break;
                case 3:
                    if (mRecordLight_1_Animation != null) {
                        mRecordLight_1.clearAnimation();
                        mRecordLight_1_Animation.cancel();
                        mRecordLight_1.setVisibility(View.GONE);

                    }
                    if (mRecordLight_2_Animation != null) {
                        mRecordLight_2.clearAnimation();
                        mRecordLight_2_Animation.cancel();
                        mRecordLight_2.setVisibility(View.GONE);
                    }
                    if (mRecordLight_3_Animation != null) {
                        mRecordLight_3.clearAnimation();
                        mRecordLight_3_Animation.cancel();
                        mRecordLight_3.setVisibility(View.GONE);
                    }

                    break;

                case 10:
                    if (mRecord_State == RECORD_ING) {
                        mRecordLight_11.setVisibility(View.VISIBLE);
                        mRecordLight_11_Animation = AnimationUtils.loadAnimation(
                                MainActivity.this, R.anim.voice_anim);
                        mRecordLight_11.setAnimation(mRecordLight_11_Animation);
                        mRecordLight_11_Animation.startNow();
                    }
                    break;
                case 11:
                    if (mRecord_State == RECORD_ING) {
                        mRecordLight_12.setVisibility(View.VISIBLE);
                        mRecordLight_12_Animation = AnimationUtils.loadAnimation(
                                MainActivity.this, R.anim.voice_anim);
                        mRecordLight_12.setAnimation(mRecordLight_12_Animation);
                        mRecordLight_12_Animation.startNow();
                    }
                    break;
                case 12:
                    if (mRecord_State == RECORD_ING) {
                        mRecordLight_13.setVisibility(View.VISIBLE);
                        mRecordLight_13_Animation = AnimationUtils.loadAnimation(
                                MainActivity.this, R.anim.voice_anim);
                        mRecordLight_13.setAnimation(mRecordLight_13_Animation);
                        mRecordLight_13_Animation.startNow();
                    }
                    break;
                case 13:
                    if (mRecordLight_11_Animation != null) {
                        mRecordLight_11.clearAnimation();
                        mRecordLight_11_Animation.cancel();
                        mRecordLight_11.setVisibility(View.GONE);

                    }
                    if (mRecordLight_12_Animation != null) {
                        mRecordLight_12.clearAnimation();
                        mRecordLight_12_Animation.cancel();
                        mRecordLight_12.setVisibility(View.GONE);
                    }
                    if (mRecordLight_13_Animation != null) {
                        mRecordLight_13.clearAnimation();
                        mRecordLight_13_Animation.cancel();
                        mRecordLight_13.setVisibility(View.GONE);
                    }

                    break;
            }
        }
    };

    /**
     * 开始动画效果
     */
    private void startRecordLightAnimation() {
        mRecordLightHandler.sendEmptyMessageDelayed(0, 0);
        mRecordLightHandler.sendEmptyMessageDelayed(1, 1000);
        mRecordLightHandler.sendEmptyMessageDelayed(2, 2000);
    }

    private void startRecordLightAnimationRight() {
        mRecordLightHandler.sendEmptyMessageDelayed(10, 0);
        mRecordLightHandler.sendEmptyMessageDelayed(11, 1000);
        mRecordLightHandler.sendEmptyMessageDelayed(12, 2000);
    }


    /**
     * 停止动画效果
     */
    private void stopRecordLightAnimation() {
        mRecordLightHandler.sendEmptyMessage(3);
    }

    private void stopRecordLightAnimationRight() {
        mRecordLightHandler.sendEmptyMessage(13);
    }



    // 录音计时线程
    void recordTimethread() {
        mRecordThread = new Thread(recordThread);
        mRecordThread.start();
    }

    private Runnable recordThread = new Runnable() {
        @Override
        public void run() {
            recordTime = 0.0f;
            while (mRecord_State == RECORD_ING) {

                try {
                    Thread.sleep(150);
                    recordTime += 0.15;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    };

}
