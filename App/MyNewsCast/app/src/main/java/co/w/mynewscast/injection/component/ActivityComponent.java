package co.w.mynewscast.injection.component;

import co.w.mynewscast.injection.PerActivity;
import co.w.mynewscast.injection.module.ActivityModule;
import co.w.mynewscast.ui.experience.ExperienceActivity;
import co.w.mynewscast.ui.main.MainActivity;
import co.w.mynewscast.ui.mediaplayer.MediaPlayerActivity;
import co.w.mynewscast.ui.signin.SignInActivity;
import dagger.Subcomponent;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

    void inject(SignInActivity signInActivity);

    void inject(ExperienceActivity experienceActivity);

    void inject(MediaPlayerActivity mediaPlayerActivity);
}
