package lars.benedetto.com.wikimeup;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.getMaxSpeechInputLength;

class Voice implements TextToSpeech.OnInitListener {
    private Context context;
    private TextToSpeech tts;
    private String text;

    Voice(Context context) {
        this.context = context;
    }

    void speak(String text) {
        this.text = text;
        tts = new TextToSpeech(context.getApplicationContext(), this);
    }

    void stop() {
        tts.stop();
        tts.shutdown();

    }

    @Override
    public void onInit(int i) {
        tts.setLanguage(Locale.ENGLISH);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            String[] tokens = tokenize(text, getMaxSpeechInputLength());
            tts.speak(tokens[0], TextToSpeech.QUEUE_FLUSH, null, null);
            for (int j = 1; j < tokens.length; j++) {
                Log.i("Voice", tokens[i]);
                tts.speak(tokens[i], TextToSpeech.QUEUE_ADD, null, null);
            }
        } else {
            String[] tokens = tokenize(text, 3000);
            tts.speak(tokens[0], TextToSpeech.QUEUE_FLUSH, null);
            for (int j = 1; j < tokens.length; j++) {
                Log.i("Voice", tokens[i]);
                tts.speak(tokens[i], TextToSpeech.QUEUE_ADD, null);
            }
        }
    }

    private String[] tokenize(String text, int maxLen) {
        String[] words = text.split(" ");
        ArrayList<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        int totalLen = 0;
        int pos = 0;
        String word;
        while (totalLen < maxLen && pos < words.length) {
            word = words[pos];
            if ((totalLen + word.length() + 1) < maxLen) {
                token.append(words[pos]).append(" ");
                totalLen += words[pos].length() + 1;
            } else {
                tokens.add(token.toString());
                token = new StringBuilder();
                totalLen = 0;
            }
            pos++;
        }
        return tokens.toArray(new String[tokens.size()]);
    }
}
