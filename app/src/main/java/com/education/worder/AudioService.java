package com.education.worder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.education.worder.data.Word;
import com.education.worder.data.Words;
import com.education.worder.fragments.MainFragment;
import com.education.worder.fragments.Settings;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

public class AudioService extends Service implements Words.OnWords, TextToSpeech.OnInitListener{

    private static final String TAG = AudioService.class.getSimpleName();
    private static final String CHANNEL_ID = "channel_01";
    private static final String CHANNEL_NAME = "Channel name";
    private static final String CHANNEL_DESCRIPTION = "Decsription: who is?";
    private static final int NOTIFICATION_ID = 777;

    public static final String EX_COMMAND_START = TAG+".start";
    public static final String EX_COMMAND_LOAD = TAG+".load_data";
    public static final String EX_COMMAND_RETURN = TAG+".return";

    public interface OnAudioService{
        void onLoaded(Words data);
    }

    private NotificationManager manager;
    private Words words;
    private OnAudioService onAudioService;
    private boolean isForeground = false;
    private boolean ttsInited = false;

    private LocalBinder binder = new LocalBinder();
    private TextToSpeech tts;

    public AudioService() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            manager.createNotificationChannel(mChannel);

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int id){
        super.onStartCommand(intent, flags, id);
        try {
            if(intent.getAction().equals(EX_COMMAND_START)){
                startInForeground();
                loadData(Settings.load());
            }
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Bind");
        Intent startIntent = new Intent(this, AudioService.class);
        startIntent.setAction(EX_COMMAND_START);
        if(Build.VERSION.SDK_INT >= 26){
            startForegroundService(startIntent);
        }else {
            startService(startIntent);
        }
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        Log.i(TAG, "Unbind:");
        if(isForeground()) {
            stopInForeground();
        }
        return true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "Destroy:");
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        stopInForeground();
        stopSelf();
    }

    public boolean isForeground() {
        return isForeground;
    }

    private void startTTS(){
        Log.i(TAG, "Start TTS");
        tts = new TextToSpeech(this, this);
    }
    private void startInForeground() {
        Log.i(TAG, "Start in foreground");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(EX_COMMAND_RETURN);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,0);

        if(Build.VERSION.SDK_INT>=26) {
            Notification.Builder builder = new Notification.Builder(this,CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_settings)
                    .setContentTitle("TEST")
                    .setContentText("HELLO")
                    .setTicker("TICKER")
                    .setContentIntent(pendingIntent);
            Notification notification=builder.build();

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            manager.createNotificationChannel(channel);
            startForeground(NOTIFICATION_ID, notification);
        }else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_settings)
                    .setContentTitle("TEST")
                    .setContentText("HELLO")
                    .setTicker("TICKER")
                    .setContentIntent(pendingIntent);
            Notification notification=builder.build();
            startForeground(NOTIFICATION_ID, notification);
        }
        isForeground = true;
        startTTS();
    }
    private void stopInForeground(){
        Log.i(TAG, "Stop in foreground");
        stopForeground(true);
        isForeground = false;
    }
    public void setOnAudioServiceListener(OnAudioService listener){
        this.onAudioService = listener;
    }

    public void loadData(Settings settings){
        Handler handler = new Handler(getMainLooper());
        words = new Words(this, handler);
        words.setOnWordsListener(this);
        words.loadWors(settings.getDictionaryID());
    }

    public void addWord(Word word){
        words.addWord(Settings.load().getDictionaryID(), word);
    }
    public void deleteWord(Word word){
        words.deleteWord(word);
    }

    public void updateWord(Word word){
        words.updateWord(word);
    }

    @Override
    public void onLoaded(Words words) {
        this.words = words;
        if(onAudioService != null) {
            onAudioService.onLoaded(this.words);
        }
    }

    @Override
    public void onInserted() {
        loadData(Settings.load());
    }

    @Override
    public void onDeleted() {
        loadData(Settings.load());
    }

    @Override
    public void onUpdated() {
        loadData(Settings.load());
    }

    @Override
    public void onInit(int status) {
        Log.i(TAG, "Init TTS: "+(status == TextToSpeech.SUCCESS));
        if(status == TextToSpeech.SUCCESS) {
            try {
                Set<Locale> languages = tts.getAvailableLanguages();
                for (Locale l : languages) {
                    Log.i(TAG, l.getDisplayLanguage());
                }
            }catch (NullPointerException ex){
                ex.printStackTrace();
            }
            ttsInited = true;
        }
    }

    public void startSpeak(){
        Log.i(TAG, "Start speak, TTS inted:"+ttsInited);
        if(ttsInited) {
            ArrayList<Word> selected = words.getSelected();
            for (Word w : selected) {
                tts.setLanguage(Locale.US);
                tts.speak(w.getWord(), TextToSpeech.QUEUE_ADD, null, w.getWord());
                tts.setLanguage(Locale.getDefault());
                tts.speak(w.getTranslate(), TextToSpeech.QUEUE_ADD, null, w.getTranslate());
            }
        }
    }

    public Set<Locale> getLanguages(){
        return tts.getAvailableLanguages();
    }

    public class LocalBinder extends Binder{
        public AudioService getService(){
            return AudioService.this;
        }
    }
}
