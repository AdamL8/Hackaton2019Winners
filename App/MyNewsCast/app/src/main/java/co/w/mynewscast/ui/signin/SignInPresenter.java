package co.w.mynewscast.ui.signin;

import javax.inject.Inject;

import co.w.mynewscast.injection.ConfigPersistent;
import co.w.mynewscast.ui.base.BasePresenter;

@ConfigPersistent
public class SignInPresenter extends BasePresenter<SignInMvpView> {

    @Inject
    public SignInPresenter() {
    }

    @Override
    public void attachView(SignInMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

}
