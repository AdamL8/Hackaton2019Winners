package co.w.mynewscast.ui.experience;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import co.w.mynewscast.R;
import co.w.mynewscast.model.ActicleListSerializable;
import co.w.mynewscast.model.Article;
import co.w.mynewscast.ui.base.BaseActivity;
import co.w.mynewscast.ui.mediaplayer.MediaPlayerActivity;
import co.w.mynewscast.ui.queue.QueueActivity;
import co.w.mynewscast.ui.videoplayer.VideoPlayerActivity;
import co.w.mynewscast.utils.PreferenceUtils;
import co.w.mynewscast.utils.QueryHelper;
import co.w.mynewscast.utils.RecyclerViewUtils;
import co.w.mynewscast.utils.TaskDelegate;

public class ExperienceActivity extends BaseActivity implements ExperienceMvpView, TaskDelegate, View.OnClickListener {
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

        findViewById(R.id.podcastButton).setOnClickListener(this);
        findViewById(R.id.videoButton).setOnClickListener(this);

        experienceArticleAdapter = new ExperienceArticleAdapter(this, experienceArticleList);
        experienceArticleRecyclerView = findViewById(R.id.experience_list_view);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        experienceArticleRecyclerView.setLayoutManager(mLayoutManager);

        experienceArticleRecyclerView.addItemDecoration(new RecyclerViewUtils.GridSpacingItemDecoration(1, RecyclerViewUtils.dpToPx(1, ExperienceActivity.this), true));
        experienceArticleRecyclerView.setItemAnimator(new DefaultItemAnimator());

        experienceArticleRecyclerView.setAdapter(experienceArticleAdapter);

        experienceArticleRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, experienceArticleRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                for (int i = 0; i < experienceArticleRecyclerView.getChildCount(); i++) {
                    RecyclerView.ViewHolder holder = experienceArticleRecyclerView.findViewHolderForAdapterPosition(i);
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                }
                view.setBackgroundColor(Color.parseColor("#4577C6"));
            }

            @Override public void onLongItemClick(View view, int position) {
            }
        }));

        loadArticles();

        mExperiencePresenter.attachView(this);
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
                int curSize = experienceArticleAdapter.getItemCount();

                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject articleJson = jsonArray.getJSONObject(i);
                    experienceArticleList.add(new Article(articleJson));
                }


                experienceArticleAdapter.notifyItemRangeChanged(curSize, jsonArray.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mExperiencePresenter.detachView();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.podcastButton) {
            ActicleListSerializable listSerializable = new ActicleListSerializable(experienceArticleList);

            Intent intent = new Intent(this, MediaPlayerActivity.class);
            intent.putExtra("ArticleList", listSerializable);

            startActivity(intent);
        } else if (i == R.id.videoButton)
        {
            startActivity(new Intent(this, VideoPlayerActivity.class));
        }
    }
}
