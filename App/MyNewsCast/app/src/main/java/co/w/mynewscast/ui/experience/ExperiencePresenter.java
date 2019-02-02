package co.w.mynewscast.ui.experience;

import javax.inject.Inject;

import co.w.mynewscast.injection.ConfigPersistent;
import co.w.mynewscast.ui.base.BasePresenter;

@ConfigPersistent
public class ExperiencePresenter extends BasePresenter<ExperienceMvpView> {

    @Inject
    public ExperiencePresenter() {
    }

    @Override
    public void attachView(ExperienceMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }
}
