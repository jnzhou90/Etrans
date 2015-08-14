package com.ibm.hospitaltranhelper.chatrecord;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.ibm.hospitaltranhelper.R;

import java.util.ArrayList;
import java.util.Calendar;


public class ChatRecordActivity extends Activity {

    private ListView talkview;
    private ArrayList<ChatMsgEntity> list = new ArrayList<ChatMsgEntity>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_record);
        talkview = (ListView) findViewById(R.id.list);
        String text1 = "Doctor, how will I be treated?";
        String text2 = "You need chemotherapy according to your  examination results.";
        String text3 = "How long will it last?";
        String text4 = "Usually three to four weeks, but it will depend on your  condition.";
        ChatMsgEntity newmessage1 = new ChatMsgEntity("Patient",getDate(),text1,R.layout.list_say_he_item);
        ChatMsgEntity newmessage2 = new ChatMsgEntity("Doctor",getDate(),text2,R.layout.list_say_me_item);
        ChatMsgEntity newmessage3 = new ChatMsgEntity("Patient",getDate(),text3,R.layout.list_say_he_item);
        ChatMsgEntity newmsesage4 = new ChatMsgEntity("Doctor",getDate(),text4,R.layout.list_say_me_item);
        list.add(newmessage1);
        list.add(newmessage2);
        list.add(newmessage3);
        list.add(newmsesage4);
        talkview.setAdapter(new ChatMsgViewAdapter(ChatRecordActivity.this, list));
    }

    private String getDate() {
        Calendar c = Calendar.getInstance();
        String date = String.valueOf(c.get(Calendar.YEAR)) + "-"
                + String.valueOf(c.get(Calendar.MONTH)) + "-" + String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        return date;
    }

}
