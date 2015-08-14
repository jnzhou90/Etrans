package com.ibm.hospitaltranhelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageButton;

import com.ibm.hospitaltranhelper.config.Constants;

/**
 * Created by zhoujn on 2015/7/31.
 */
public class SelectLangDialog extends AlertDialog  implements View.OnClickListener {

    private  String lang;
    private  OnSelectLangDialogListener selectLangDialogListener;


    private ImageButton imgBtnEnglish;
    private ImageButton imgBtnSpanish;
    private ImageButton imgBtnPortuguese ;
    private ImageButton imgBtnFrench;
    private ImageButton imgBtnChinese ;
    private ImageButton imgBtnArabic;


    public interface OnSelectLangDialogListener{
        public void back(String lang);
    }

    public SelectLangDialog(Context context,int theme,OnSelectLangDialogListener selectLangDialogListener){
        super(context,theme);
        this.selectLangDialogListener = selectLangDialogListener;


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sel_lang);

        imgBtnEnglish = (ImageButton) findViewById(R.id.imgbtn_english);
        imgBtnSpanish = (ImageButton) findViewById(R.id.imgbtn_spanish);
        imgBtnPortuguese = (ImageButton) findViewById(R.id.imgbtn_portuguese);
        imgBtnFrench = (ImageButton) findViewById(R.id.imgbtn_french);
        imgBtnArabic = (ImageButton) findViewById(R.id.imgbtn_arabic);
        imgBtnChinese = (ImageButton) findViewById(R.id.imgbtn_chinese);

        imgBtnEnglish.setOnClickListener(this);
        imgBtnSpanish.setOnClickListener(this);
        imgBtnPortuguese.setOnClickListener(this);
        imgBtnFrench.setOnClickListener(this);
        imgBtnArabic.setOnClickListener(this);
        imgBtnChinese.setOnClickListener(this);



    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.imgbtn_english:
                selectLangDialogListener.back(Constants.ENGLISH);
                SelectLangDialog.this.dismiss();
                break;
            case R.id.imgbtn_spanish:
                selectLangDialogListener.back(Constants.SPANISH);
                SelectLangDialog.this.dismiss();
                break;
            default:
                selectLangDialogListener.back("otherlang");
                SelectLangDialog.this.dismiss();
                break;
        }
    }
}

