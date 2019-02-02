package co.w.mynewscast.injection.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import co.w.mynewscast.injection.ApplicationContext;
import co.w.mynewscast.injection.module.ApplicationModule;
import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    @ApplicationContext
    Context context();
    Application application();
}
