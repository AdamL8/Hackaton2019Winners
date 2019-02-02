package co.w.mynewscast.ui.experience;

import android.os.Bundle;

import javax.inject.Inject;

import co.w.mynewscast.R;
import co.w.mynewscast.ui.base.BaseActivity;
import co.w.mynewscast.ui.main.MainMvpView;

public class ExperienceActivity extends BaseActivity implements ExperienceMvpView {
    @Inject
    ExperiencePresenter mExperiencePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        setContentView(R.layout.activity_google); // TODO: change content lauyout handle

        mExperiencePresenter.attachView(this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mExperiencePresenter.detachView();
    }
}
