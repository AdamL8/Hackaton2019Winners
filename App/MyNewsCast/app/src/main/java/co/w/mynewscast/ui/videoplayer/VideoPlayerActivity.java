package co.w.mynewscast.ui.videoplayer;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import javax.inject.Inject;

import co.w.mynewscast.R;
import co.w.mynewscast.ui.base.BaseActivity;

public class VideoPlayerActivity extends BaseActivity implements VideoPlayerMvpView {

    @Inject
    VideoPlayerPresenter mVideoPlayerPresenter;

    private VideoView videoView;
    private int position = 0;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityComponent().inject(this);
        setContentView(R.layout.video_player);

        mVideoPlayerPresenter.attachView(this);

        //set the media controller buttons
        if (mediaControls == null) {
            mediaControls = new MediaController(this);
        }

        //initialize the VideoView
        videoView = (VideoView) findViewById(R.id.video_view);

        // create a progress bar while the video file is loading
        progressDialog = new ProgressDialog(this);
        // set a title for the progress bar
        progressDialog.setTitle("JavaCodeGeeks Android Video View Example");
        // set a message for the progress bar
        progressDialog.setMessage("Loading...");
        //set the progress bar not cancelable on users' touch
        progressDialog.setCancelable(false);
        // show the progress bar
        progressDialog.show();

        try {
            //set the media controller in the VideoView
            videoView.setMediaController(mediaControls);

            //set the uri of the video to be played
            //videoView.setVideoURI("http://40.76.47.167/api/content/summary/audio/fr/1150647");
            videoView.setVideoPath("http://40.76.47.167/api/content/summary/audio/fr/1150647");

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.requestFocus();
        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {
                // close the progress bar and play the video
                progressDialog.dismiss();
                //if we have a position on savedInstanceState, the video playback should start from here
                videoView.seekTo(position);
                if (position == 0) {
                    videoView.start();
                } else {
                    //if we come from a resumed activity, video playback will be paused
                    videoView.pause();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //we use onSaveInstanceState in order to store the video playback position for orientation change
        savedInstanceState.putInt("Position", videoView.getCurrentPosition());
        videoView.pause();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //we use onRestoreInstanceState in order to play the video playback from the stored position
        position = savedInstanceState.getInt("Position");
        videoView.seekTo(position);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        mVideoPlayerPresenter.detachView();
    }
}
