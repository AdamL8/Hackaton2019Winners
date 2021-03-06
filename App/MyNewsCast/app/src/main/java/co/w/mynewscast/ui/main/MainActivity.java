package co.w.mynewscast.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import co.w.mynewscast.MyNewsCastApplication;
import co.w.mynewscast.R;
import co.w.mynewscast.model.Article;
import co.w.mynewscast.ui.base.BaseActivity;
import co.w.mynewscast.ui.experience.ExperienceActivity;
import co.w.mynewscast.ui.queue.QueueActivity;
import co.w.mynewscast.ui.settings.SettingsActivity;
import co.w.mynewscast.utils.DialogFactory;
import co.w.mynewscast.utils.PreferenceUtils;
import co.w.mynewscast.utils.QueryHelper;
import co.w.mynewscast.utils.RecyclerViewUtils;
import co.w.mynewscast.utils.TaskDelegate;

public class MainActivity extends BaseActivity implements MainMvpView,
        NavigationView.OnNavigationItemSelectedListener, TaskDelegate, View.OnClickListener {

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "co.w.mynewscast.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";
    private static final int RC_CONTENT_EXPERIENCE = 1;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @Inject
    MainPresenter mMainPresenter;

    private ArticleAdapter mArticleAdapter;
    private RecyclerView articleRecyclerView;
    private List<Article> articleList = new ArrayList<>();

    //@BindView(R.id.cardView) CardView mCardView;

    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_TRIGGER_SYNC_FLAG, triggerDataSyncOnCreate);
        return intent;
    }

    private void loadArticles()
    {
        QueryHelper.getArticles(PreferenceUtils.getSelectedLanguageId(), this);
    }

    @Override
    public void taskCompletionResult(String result) {

        if (result != null) {

            try {
                JSONArray jsonArray = new JSONArray(result);
                int curSize = mArticleAdapter.getItemCount();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject articleJson = jsonArray.getJSONObject(i);
                    articleList.add(new Article(articleJson));
                }

                mArticleAdapter.notifyItemRangeChanged(curSize, jsonArray.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Remove dots menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.action_settings);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyNewsCastApplication.getInstance().initAppLanguage(this);

        activityComponent().inject(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.news_articles);
        setSupportActionBar(toolbar);


        // Button listeners
        findViewById(R.id.newspaper).setOnClickListener(this);
        findViewById(R.id.what_is_popular).setOnClickListener(this);
        findViewById(R.id.categories).setOnClickListener(this);

        mArticleAdapter = new ArticleAdapter(this, articleList);
        articleRecyclerView = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        articleRecyclerView.setLayoutManager(mLayoutManager);

        articleRecyclerView.addItemDecoration(new RecyclerViewUtils.GridSpacingItemDecoration(2, RecyclerViewUtils.dpToPx(10, MainActivity.this), true));
        articleRecyclerView.setItemAnimator(new DefaultItemAnimator());

        articleRecyclerView.setAdapter(mArticleAdapter);

        loadArticles();

        mMainPresenter.attachView(this);

//        //get firebase auth instance
//        auth = FirebaseAuth.getInstance();
//
//        //get current user
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        authListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//
//                if (user == null) {
//                    // user auth state is changed - user is null
//                    // launch login activity
//                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
//                }
//            }
//        };
//
//        /* Check if the user is authenticated with Firebase already. If this is the case we can set the authenticated
//         * user and hide hide any login buttons */
//        auth.addAuthStateListener(authListener);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.newspaper) {
            contentExperience();
        } else if (i == R.id.what_is_popular) {
            contentExperience();
        } else if (i == R.id.categories) {
            contentExperience();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mMainPresenter.detachView();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_signout)
        {
            auth.signOut();
        } else if (id == R.id.nav_queue)
        {
            startActivity(new Intent(this, QueueActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // [START contentExperience]
    public void contentExperience() {
        startActivityForResult(new Intent(this, ExperienceActivity.class),RC_CONTENT_EXPERIENCE);
    }
    // [END contentExperience]

    /***** MVP View methods implementation *****/

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(this, getString(R.string.dialog_error_loading_main))
                .show();
    }
}
