package tw.brad.ry_playmusic2;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private Button playPause;
    private SeekBar seekBar;
    private MyReceiver myReceiver;
    private MyService myService;
    private boolean isBound;
    private boolean isPlaying;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.LocalBinder binder = (MyService.LocalBinder)iBinder;
            myService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playPause = findViewById(R.id.play_or_pause);
        isPlaying = false;
        playPause.setText("播放");

        seekBar = findViewById(R.id.prgress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b){
                    myService.setPosition(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter("fromService");
        registerReceiver(myReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound){
            unbindService(mConnection);
            isBound = false;
        }
        unregisterReceiver(myReceiver);
    }

    public void playOrPause(View view) {
        isPlaying = !isPlaying;
        if (!isPlaying){
            playPause.setText("播放");
            myService.pauseMusic();
        }else{
            playPause.setText("暫停");
            myService.playMusic();

        }
    }


    public void stopPlay(View view) {
        isPlaying = false;
        playPause.setText("播放");

        myService.stopMusic();
        myService.setPosition(0);
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wherenow = intent.getIntExtra("wherenow", -1);
            if (wherenow >= 0){
                seekBar.setProgress(wherenow);
            }

            int max = intent.getIntExtra("max", -1);
            if (max >= 0){
                seekBar.setMax(max);
            }
        }
    }
}
