package co.w.mynewscast.ui.mediaplayer;

import javax.inject.Inject;

import co.w.mynewscast.ui.base.BasePresenter;

public class MediaPlayerPresenter extends BasePresenter<MediaPlayerMvpView> {

    @Inject
    public MediaPlayerPresenter() {
    }

    @Override
    public void attachView(MediaPlayerMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }
}
