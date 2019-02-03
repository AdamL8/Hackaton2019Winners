package co.w.mynewscast.ui.videoplayer;


import javax.inject.Inject;

import co.w.mynewscast.ui.base.BasePresenter;

public class VideoPlayerPresenter extends BasePresenter<VideoPlayerMvpView> {

    @Inject
    public VideoPlayerPresenter() {
    }

    @Override
    public void attachView(VideoPlayerMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }
}