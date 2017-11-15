package ch.apquest.bb.appquest_04.TextToSpeech;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TextToSpeechController implements TextToSpeech.OnInitListener {

    //region Fields

    private TextToSpeech tts = null;
    private boolean ttsSuccessfulInit = false;
    private TextToSpeechListener ttsListener = null;

    //endregion

    //region Constructor

    public TextToSpeechController(Context context, TextToSpeechListener ttsListener) {
        this.ttsListener = ttsListener;

        this.tts = new TextToSpeech(context, this);
    }

    //endregion

    //region Events

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            ttsSuccessfulInit = true;

            // language by default
            tts.setLanguage(Locale.ENGLISH);
        }
        ttsListener.onTextToSpeechInit(status);
    }

    //endregion

    //region General Methods

    public void destroy() {
        if (tts != null) {
            tts.shutdown();
        }
    }

    public void speak(String text) {
        if (ttsSuccessfulInit) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, Long.toString(System.currentTimeMillis()));
        }
    }

    //endregion

}
