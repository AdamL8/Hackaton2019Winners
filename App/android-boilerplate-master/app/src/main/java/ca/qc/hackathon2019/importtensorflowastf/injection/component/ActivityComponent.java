package ca.qc.hackathon2019.importtensorflowastf.injection.component;

import dagger.Subcomponent;
import ca.qc.hackathon2019.importtensorflowastf.injection.PerActivity;
import ca.qc.hackathon2019.importtensorflowastf.injection.module.ActivityModule;
import ca.qc.hackathon2019.importtensorflowastf.ui.main.MainActivity;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

}
