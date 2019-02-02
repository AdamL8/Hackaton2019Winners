package co.w.mynewscast.ui;

import android.app.Application;
import android.content.Context;

import co.w.mynewscast.BuildConfig;
import co.w.mynewscast.injection.component.ApplicationComponent;
import co.w.mynewscast.injection.module.ApplicationModule;
import timber.log.Timber;

public class MyNewsCastApplication extends Application {
    ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static MyNewsCastApplication get(Context context) {
        return (MyNewsCastApplication) context.getApplicationContext();
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }
}