package com.ibm.hospitaltranhelper.utils;

import com.ibm.hospitaltranhelper.config.Constants;
import com.ibm.watson.developer_cloud.language_translation.v2.LanguageTranslation;
import com.ibm.watson.developer_cloud.language_translation.v2.model.Translation;
import com.ibm.watson.developer_cloud.language_translation.v2.model.TranslationResult;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechAlternative;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 江南 on 2015/7/22.
 */
public class TranslationHelper {

    /**
     * 把source语言种类翻译成traget语言种类
     * @param text 要翻译的文本语言
     * @param source 翻译的文本的语言
     * @param traget 想要翻译成的文本语言
     * @return
     */
    public static String translate(String text,String source,String traget){
        LanguageTranslation service = new LanguageTranslation();
       // service.setUsernameAndPassword("5d933d5e-50b5-4b05-a358-f6b78162f7a7", "VbLmZpQSNrdu");
        service.setUsernameAndPassword(Constants.TTT_USERNAME, Constants.TTT_PASSWORD);
        TranslationResult translationResult = service.translate(text, source, traget);
        List<Translation> translationList= translationResult.getTranslations();
        Translation translation=translationList.get(0);
        return translation.getTranslation();
    }

    /**
     * 将语音文件转化为文字(改函数将英文语言翻译英文)
     * @param audioUrl  要转换为文字的语音文件路径
     * @return
     */
    public static String speechToText(String audioUrl,String lang){

        SpeechToText service = new SpeechToText();
        //service.setUsernameAndPassword("cedf80b0-7834-4c6d-bf1f-2f97cbfc9b61", "bhRWf6nInRrm");
        service.setUsernameAndPassword(Constants.STT_USERNAME,Constants.STT_PASSWORD);
        //File audio = new File("F:\\AndroidCode\\text-to-speech-java\\howareyou.wav");
        File audio = new File(audioUrl);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("audio", audio);
        params.put("content_type", "audio/wav; rate=16000"); //flac or war file format
        if(lang == Constants.ENGLISH)
        {
            params.put("model", "en-US_BroadbandModel"); //english
        }else if (lang == Constants.SPANISH){
            params.put("model", "en-ES_BroadbandModel"); //english
        }else{
            params.put("model", "en-US_BroadbandModel"); //english
        }

        params.put("word_confidence", true);
        params.put("continuous", true);
        params.put("timestamps", true);
        params.put("inactivity_timeout", 1200);
        params.put("max_alternatives", 1);

        SpeechResults speechResults = service.recognize(params);
        List<Transcript>  transcriptList=speechResults.getResults();
        SpeechAlternative speechAlternative=transcriptList.get(0).getAlternatives().get(0);

        return speechAlternative.getTranscript();

    }

    /**
     * 将英文文本转换为英文语音,
     * @param text
     * @param fileName
     * @return  一个mav格式的的音频文件
     */
    public static File  textToSpeechEN(String text,String fileName){

        TextToSpeech service = new TextToSpeech();
        //service.setUsernameAndPassword("ecab4566-1a4c-42b9-9043-deb92de7b912", "zopvINzOffFE");
        service.setUsernameAndPassword(Constants.TTS_USERNAME,Constants.TTS_PASSWORD);
        InputStream in = service.synthesize(text, Voice.EN_MICHAEL, "audio/wav");
        File file = new File(fileName);
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while((bytesRead = in.read(buffer,0,8192))!= -1){
                os.write(buffer,0,bytesRead);
            }
            os.close();
            in.close();
        } catch (Exception  e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 将文本转换为西班牙语音,
     * @param text
     * @param fileName
     * @return  一个mav格式的的音频文件
     */
    public static File  textToSpeech(String text,String fileName,String lang){
        InputStream in;
        TextToSpeech service = new TextToSpeech();
        service.setUsernameAndPassword(Constants.TTS_USERNAME,Constants.TTS_PASSWORD);
        if(lang == Constants.ENGLISH)
        {
            in = service.synthesize(text, Voice.EN_MICHAEL, "audio/wav");
        }else if(lang == Constants.SPANISH){
          in = service.synthesize(text, Voice.ES_ENRIQUE, "audio/wav");
        }else{
            in = service.synthesize(text, Voice.EN_MICHAEL, "audio/wav");
        }

        File file = new File(fileName);
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while((bytesRead = in.read(buffer,0,8192))!= -1){
                os.write(buffer,0,bytesRead);
            }
            os.close();
            in.close();
        } catch (Exception  e) {
            e.printStackTrace();
        }
        return file;
    }


}
