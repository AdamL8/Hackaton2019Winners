package ca.qc.hackathon2019.importtensorflowastf;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;
import ca.qc.hackathon2019.importtensorflowastf.injection.component.ApplicationComponent;
import ca.qc.hackathon2019.importtensorflowastf.injection.component.DaggerApplicationComponent;
import ca.qc.hackathon2019.importtensorflowastf.injection.module.ApplicationModule;

public class MyNewsCast extends Application  {

    ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Fabric.with(this, new Crashlytics());
        }
    }

    public static MyNewsCast get(Context context) {
        return (MyNewsCast) context.getApplicationContext();
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