package co.w.mynewscast.ui.experience;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import co.w.mynewscast.R;
import co.w.mynewscast.model.Article;
import co.w.mynewscast.ui.base.BaseActivity;
import co.w.mynewscast.ui.main.MainActivity;
import co.w.mynewscast.utils.TaskDelegate;

public class ExperienceActivity extends BaseActivity implements ExperienceMvpView, TaskDelegate {
    @Inject
    ExperiencePresenter mExperiencePresenter;

    private ExperienceArticleAdapter experienceArticleAdapter;
    private RecyclerView experienceArticleRecyclerView;
    private List<Article> experienceArticleList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.content_experience);

        experienceArticleAdapter = new ExperienceArticleAdapter(this, experienceArticleList);
        experienceArticleRecyclerView = findViewById(R.id.experience_list_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        experienceArticleRecyclerView.setLayoutManager(mLayoutManager);
        experienceArticleRecyclerView.setAdapter(experienceArticleAdapter);

        loadArticles();

        mExperiencePresenter.attachView(this);
    }

    private void loadArticles()
    {
        new JsonTask(this).execute("http://40.76.47.167/api/content/fr");
    }

    @Override
    public void taskCompletionResult(String result) {

        try {
            JSONArray jsonArray = new JSONArray(result);

            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject articleJson = jsonArray.getJSONObject(i);
                experienceArticleList.add(new Article(articleJson));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mExperiencePresenter.detachView();
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        private TaskDelegate delegate;

        public JsonTask(TaskDelegate delegate) {
            this.delegate = delegate;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            delegate.taskCompletionResult(result);
        }
    }
}
