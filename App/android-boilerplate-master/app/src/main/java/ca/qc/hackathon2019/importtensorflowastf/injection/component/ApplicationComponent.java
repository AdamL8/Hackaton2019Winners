package ca.qc.hackathon2019.importtensorflowastf.injection.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import ca.qc.hackathon2019.importtensorflowastf.data.DataManager;
import ca.qc.hackathon2019.importtensorflowastf.data.SyncService;
import ca.qc.hackathon2019.importtensorflowastf.data.local.DatabaseHelper;
import ca.qc.hackathon2019.importtensorflowastf.data.local.PreferencesHelper;
import ca.qc.hackathon2019.importtensorflowastf.data.remote.RibotsService;
import ca.qc.hackathon2019.importtensorflowastf.injection.ApplicationContext;
import ca.qc.hackathon2019.importtensorflowastf.injection.module.ApplicationModule;
import ca.qc.hackathon2019.importtensorflowastf.util.RxEventBus;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(SyncService syncService);

    @ApplicationContext Context context();
    Application application();
    RibotsService ribotsService();
    PreferencesHelper preferencesHelper();
    DatabaseHelper databaseHelper();
    DataManager dataManager();
    RxEventBus eventBus();

}
