package co.w.mynewscast.injection.component;

import co.w.mynewscast.injection.PerActivity;
import co.w.mynewscast.injection.module.ActivityModule;
import co.w.mynewscast.ui.main.MainActivity;
import dagger.Subcomponent;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

}
