package co.w.mynewscast.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import co.w.mynewscast.MyNewsCastApplication;
import co.w.mynewscast.R;
import co.w.mynewscast.ui.base.BaseActivity;
import co.w.mynewscast.ui.main.MainActivity;

import static co.w.mynewscast.utils.LocaleManager.LANGUAGE_ENGLISH;
import static co.w.mynewscast.utils.LocaleManager.LANGUAGE_FRENCH;

public class SettingsActivity  extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.en).setOnClickListener(v -> setNewLocale(LANGUAGE_ENGLISH, false));
        findViewById(R.id.en).setOnLongClickListener(v -> setNewLocale(LANGUAGE_ENGLISH, true));
        findViewById(R.id.fr).setOnClickListener(v -> setNewLocale(LANGUAGE_FRENCH, false));
        findViewById(R.id.fr).setOnLongClickListener(v -> setNewLocale(LANGUAGE_FRENCH, true));
    }

    private boolean setNewLocale(String language, boolean restartProcess) {
        MyNewsCastApplication.localeManager.setNewLocale(this, language);

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

        if (restartProcess) {
            System.exit(0);
        } else {
            Toast.makeText(this, "Activity restarted", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
