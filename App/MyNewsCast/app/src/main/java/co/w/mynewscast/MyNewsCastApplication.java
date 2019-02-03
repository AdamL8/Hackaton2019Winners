package co.w.mynewscast;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import co.w.mynewscast.injection.component.ApplicationComponent;
import co.w.mynewscast.injection.component.DaggerApplicationComponent;
import co.w.mynewscast.injection.module.ApplicationModule;
import co.w.mynewscast.utils.LocaleManager;
import timber.log.Timber;

public class MyNewsCastApplication extends Application {

    ApplicationComponent mApplicationComponent;

    public static LocaleManager localeManager;

    private final String TAG = "MyNewsCastApplication";

    @Override
    protected void attachBaseContext(Context base) {
        localeManager = new LocaleManager(base);
        super.attachBaseContext(localeManager.setLocale(base));
        Log.w(TAG, "attachBaseContext");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localeManager.setLocale(this);
        Log.w(TAG, "onConfigurationChanged: " + newConfig.locale.getLanguage());
    }
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
