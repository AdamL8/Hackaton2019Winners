package co.w.mynewscast.ui.mediaplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import co.w.mynewscast.R;
import co.w.mynewscast.ui.base.BaseActivity;

public class MediaPlayerActivity extends BaseActivity implements MediaPlayerMvpView, View.OnClickListener {

    private ImageButton forwardButton, pauseButton, playButton, rewindButton;
    private ImageView mediaImage;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    public TextView songName, duration;

    private double timeElapsed = 0;
    private double finalTime = 0;

    private Handler durationHandler = new Handler();;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;

    public static int oneTimeOnly = 0;
    String mediaURL = "http://40.76.47.167/api/content/summary/audio/fr/1150647";

    @Inject
    MediaPlayerPresenter meMediaPlayerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityComponent().inject(this);
        setContentView(R.layout.media_player);

        meMediaPlayerPresenter.attachView(this);

        forwardButton = (ImageButton) findViewById(R.id.media_ff);
        pauseButton = (ImageButton) findViewById(R.id.media_pause);
        playButton = (ImageButton) findViewById(R.id.media_play);
        rewindButton = (ImageButton) findViewById(R.id.media_rew);
        mediaImage = (ImageView)findViewById(R.id.mp3Image);

        songName = (TextView) findViewById(R.id.songName);
        duration = (TextView) findViewById(R.id.songDuration);
        seekbar = (SeekBar) findViewById(R.id.seekBar);

        forwardButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        rewindButton.setOnClickListener(this);

        // mediaPlayer = MediaPlayer.create(this, R.raw.song);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(mediaURL);
            mediaPlayer.prepare();
            finalTime = mediaPlayer.getDuration();
            seekbar.setMax((int) finalTime);
            seekbar.setClickable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            timeElapsed = mediaPlayer.getCurrentPosition();
            //set seekbar progress
            seekbar.setProgress((int) timeElapsed);
            //set time remaing
            double timeRemaining = finalTime - timeElapsed;
            duration.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));

            //repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 100);
        }
    };


    @Override
    public void play() {
        mediaPlayer.start();
        timeElapsed = mediaPlayer.getCurrentPosition();
        seekbar.setProgress((int) timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public void rewind() {
        if ((timeElapsed - forwardTime) >= 0) {
            timeElapsed = timeElapsed - forwardTime;

            //seek to the exact second of the track
            mediaPlayer.seekTo((int) timeElapsed);
        }
    }

    @Override
    public void forward() {
        //check if we can go forward at forwardTime seconds before song endes
        if ((timeElapsed + forwardTime) <= finalTime) {
            timeElapsed = timeElapsed + forwardTime;

            //seek to the exact second of the track
            mediaPlayer.seekTo((int) timeElapsed);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        meMediaPlayerPresenter.detachView();
    }

    @Override
    public void onClick(View v) {
        Integer id = v.getId();

        if (id == R.id.media_ff)
        {
            forward();
        } else if (id == R.id.media_pause)
        {
            pause();
        } else if (id == R.id.media_play)
        {
            play();
        } else if (id== R.id.media_rew)
        {
            rewind();
        }
    }
}
