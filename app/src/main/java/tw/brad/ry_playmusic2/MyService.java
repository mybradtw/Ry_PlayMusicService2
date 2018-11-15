package tw.brad.ry_playmusic2;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    private IBinder mBinder = new LocalBinder();
    private MediaPlayer mediaPlayer;
    private Timer timer;


    public class LocalBinder extends Binder {
        MyService getService(){return MyService.this;}
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = MediaPlayer.create(this, R.raw.test);

        Intent intent = new Intent("fromService");
        intent.putExtra("max", mediaPlayer.getDuration());
        sendBroadcast(intent);

        timer = new Timer();
        timer.schedule(new UpdateTask(), 0, 500);
    }

    private class UpdateTask extends TimerTask {
        @Override
        public void run() {
            if (mediaPlayer!= null && mediaPlayer.isPlaying()) {
                Intent intent = new Intent("fromService");
                intent.putExtra("wherenow", mediaPlayer.getCurrentPosition());
                sendBroadcast(intent);
            }
        }
    }


    public void playMusic(){
        if (mediaPlayer!= null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
    public void pauseMusic(){
        if (mediaPlayer!= null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }
    public void stopMusic(){
        if (mediaPlayer!= null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }
    public void setPosition(int pos){
        mediaPlayer.seekTo(pos);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }
}
