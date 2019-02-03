package co.w.mynewscast.ui.queue;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.w.mynewscast.R;
import co.w.mynewscast.model.Article;
import co.w.mynewscast.model.ArticleFirebaseModel;
import co.w.mynewscast.ui.base.BaseActivity;
import co.w.mynewscast.ui.main.ArticleAdapter;
import co.w.mynewscast.utils.QueryHelper;
import co.w.mynewscast.utils.RecyclerViewUtils;
import co.w.mynewscast.utils.TaskDelegate;

public class QueueActivity extends BaseActivity implements TaskDelegate {

    private FirebaseUser user;

    private DatabaseReference mDatabase;
    private DatabaseReference mArticleQueueReference;
    private ChildEventListener mArticleQueueListener;
    private ArticleAdapter mArticleAdapter;
    private RecyclerView queueArticleRecyclerView;

    private List<ArticleFirebaseModel> firebaseArticleList = new ArrayList<>();
    private List<Article> queueArticleList = new ArrayList<>();

    private static final String TAG = "QueueActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.queue_activity);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mArticleQueueReference = FirebaseDatabase.getInstance().getReference("queue");
        user = FirebaseAuth.getInstance().getCurrentUser();


        mArticleAdapter = new ArticleAdapter(this, queueArticleList);
        queueArticleRecyclerView = findViewById(R.id.queue_list_view);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        queueArticleRecyclerView.setLayoutManager(mLayoutManager);

        queueArticleRecyclerView.addItemDecoration(new RecyclerViewUtils.GridSpacingItemDecoration(1, RecyclerViewUtils.dpToPx(1, QueueActivity.this), true));
        queueArticleRecyclerView.setItemAnimator(new DefaultItemAnimator());

        queueArticleRecyclerView.setAdapter(mArticleAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // A new message has been added
                // onChildAdded() will be called for each node at the first time
                ArticleFirebaseModel article = dataSnapshot.getValue(ArticleFirebaseModel.class);
                firebaseArticleList.add(article);

//                Log.e(TAG, "onChildAdded:" + article.id);

                ArticleFirebaseModel latest = firebaseArticleList.get(firebaseArticleList.size() - 1);
                QueryHelper.getArticle(latest.lang, latest.id, QueueActivity.this);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.e(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A message has changed
                ArticleFirebaseModel article = dataSnapshot.getValue(ArticleFirebaseModel.class);
                Toast.makeText(QueueActivity.this, "onChildChanged: " + article.id, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A message has been removed
                ArticleFirebaseModel article = dataSnapshot.getValue(ArticleFirebaseModel.class);
                Toast.makeText(QueueActivity.this, "onChildRemoved: " + article.id, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.e(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A message has changed position
                ArticleFirebaseModel article = dataSnapshot.getValue(ArticleFirebaseModel.class);
                Toast.makeText(QueueActivity.this, "onChildMoved: " + article.id, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "postArticles:onCancelled", databaseError.toException());
                Toast.makeText(QueueActivity.this, "Failed to load Message.", Toast.LENGTH_SHORT).show();
            }
        };

        mArticleQueueReference.addChildEventListener(childEventListener);

        // copy for removing at onStop()
        mArticleQueueListener = childEventListener;
    }

    @Override
    public void taskCompletionResult(String result) {
        if (result != null) {

//            result = result.substring(1, result.length()-1);

            try {
                JSONObject articleJson = new JSONObject(result);
                queueArticleList.add(new Article(articleJson));

                Log.e("TEST", String.valueOf(articleJson));

                mArticleAdapter.notifyItemInserted(queueArticleList.size() - 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        if (mArticleQueueListener != null) {
//            mArticleQueueReference.removeEventListener(mArticleQueueListener);
//        }
//
//        for (Article article: firebaseArticleList) {
//            Log.e(TAG, "listItem: " + article.Id);
//        }
//    }
}
