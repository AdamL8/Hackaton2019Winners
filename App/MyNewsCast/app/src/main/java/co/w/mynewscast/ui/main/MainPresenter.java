package co.w.mynewscast.ui.main;

import javax.inject.Inject;

import co.w.mynewscast.injection.ConfigPersistent;
import co.w.mynewscast.ui.base.BasePresenter;

@ConfigPersistent
public class MainPresenter extends BasePresenter<MainMvpView> {

    @Inject
    public MainPresenter() {

    }

    @Override
    public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }
}
