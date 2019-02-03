package co.w.mynewscast.ui.mediaplayer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import co.w.mynewscast.R;
import co.w.mynewscast.model.ActicleListSerializable;
import co.w.mynewscast.model.Article;
import co.w.mynewscast.ui.base.BaseActivity;
import co.w.mynewscast.utils.PreferenceUtils;

public class MediaPlayerActivity extends BaseActivity implements MediaPlayerMvpView, View.OnClickListener, MediaPlayer.OnCompletionListener {

    private ImageButton forwardButton, pauseButton, playButton, rewindButton;
    private ImageView mediaImage;
    private MediaPlayer mediaPlayer;
    public TextView duration, podcastTitle;

    private double timeElapsed = 0;
    private double finalTime = 0;

    private Handler durationHandler = new Handler();;
    private int forwardTime = 5000;
    private SeekBar seekbar;
    Integer playListIndex = 0;
    List<Article> articles;

    String mediaURLBase = "http://40.76.47.167/api/content/summary/audio";
    //String mediaURLBase = "http://40.76.47.167/api/audio"; server not working with this

    String mediaURL = "http://40.76.47.167/api/content/summary/audio/fr/1150647"; // default

    // DEBUG FLAG
    boolean DEBUG = false;

    @Inject
    MediaPlayerPresenter mMediaPlayerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        ActicleListSerializable playList = (ActicleListSerializable) intent.getExtras().getSerializable("ArticleList");
        articles = playList.articles;

        activityComponent().inject(this);
        setContentView(R.layout.media_player);

        mMediaPlayerPresenter.attachView(this);

        forwardButton = (ImageButton) findViewById(R.id.media_ff);
        pauseButton = (ImageButton) findViewById(R.id.media_pause);
        playButton = (ImageButton) findViewById(R.id.media_play);
        rewindButton = (ImageButton) findViewById(R.id.media_rew);
        mediaImage = (ImageView)findViewById(R.id.podcast_image);
        podcastTitle = (TextView)findViewById(R.id.podcast_title);

        duration = (TextView) findViewById(R.id.songDuration);
        seekbar = (SeekBar) findViewById(R.id.seekBar);

        forwardButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        rewindButton.setOnClickListener(this);

        loadPodcast();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        ++playListIndex;
        loadPodcast();
        Log.w("COMPLETED", "COMPLETED");
        //finish(); // finish current activity
    }

    private void loadPodcast()
    {
        if (mediaPlayer != null)
            mediaPlayer.release();

        mediaPlayer = new MediaPlayer();
        if (!DEBUG) {
            Integer id = articles.get(playListIndex).Id;
            mediaURL = String.format("%s/%s/%s", mediaURLBase, PreferenceUtils.getSelectedLanguageId(), id);
        }

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_broken_image_grey_128dp);
        Glide.with(this).load(articles.get(playListIndex).Image).apply(requestOptions).into(mediaImage);

        podcastTitle.setText(articles.get(playListIndex).Title);

        try {
            Log.e("MEDIA PLAYER URL", mediaURL);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(this::onCompletion);

            mediaPlayer.setDataSource(this, Uri.parse(mediaURL));
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
        } else {
            timeElapsed = 0;
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
        } else {
            timeElapsed = finalTime;
            mediaPlayer.seekTo((int) timeElapsed);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mMediaPlayerPresenter.detachView();
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
