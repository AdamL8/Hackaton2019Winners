package co.w.mynewscast;

import android.app.Application;
import android.content.Context;

import co.w.mynewscast.injection.component.ApplicationComponent;
import co.w.mynewscast.injection.component.DaggerApplicationComponent;
import co.w.mynewscast.injection.module.ApplicationModule;
import co.w.mynewscast.utils.LocaleUtils;
import co.w.mynewscast.utils.PreferenceUtils;
import timber.log.Timber;

public class MyNewsCastApplication extends Application {

    ApplicationComponent mApplicationComponent;

    private static MyNewsCastApplication applicationInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationInstance = this;
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

    public void initAppLanguage(Context context){
        LocaleUtils.initialize(context, PreferenceUtils.getSelectedLanguageId() );
    }

    public static synchronized MyNewsCastApplication getInstance() {
        return applicationInstance;
    }
}
