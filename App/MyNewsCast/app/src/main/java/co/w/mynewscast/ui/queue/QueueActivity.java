package co.w.mynewscast.ui.queue;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import co.w.mynewscast.R;
import co.w.mynewscast.model.Article;
import co.w.mynewscast.ui.base.BaseActivity;

public class QueueActivity extends BaseActivity {

    private FirebaseUser user;

    private DatabaseReference mDatabase;
    private DatabaseReference mArticleQueueReference;
    private ChildEventListener mArticleQueueListener;

    private ArrayList<Article> articleList;

    private static final String TAG = "QueueActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.queue_activity);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mArticleQueueReference = FirebaseDatabase.getInstance().getReference("queue");
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // A new message has been added
                // onChildAdded() will be called for each node at the first time
                Article article = dataSnapshot.getValue(Article.class);
                articleList.add(article);

                Log.e(TAG, "onChildAdded:" + article.Id);

                Article latest = articleList.get(articleList.size() - 1);

//                tvAuthor.setText(latest.author);
//                tvTime.setText(latest.time);
//                tvBody.setText(latest.body);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.e(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A message has changed
                Article article = dataSnapshot.getValue(Article.class);
                Toast.makeText(QueueActivity.this, "onChildChanged: " + article.Id, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A message has been removed
                Article article = dataSnapshot.getValue(Article.class);
                Toast.makeText(QueueActivity.this, "onChildRemoved: " + article.Id, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.e(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A message has changed position
                Article article = dataSnapshot.getValue(Article.class);
                Toast.makeText(QueueActivity.this, "onChildMoved: " + article.Id, Toast.LENGTH_SHORT).show();
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

//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        if (mArticleQueueListener != null) {
//            mArticleQueueReference.removeEventListener(mArticleQueueListener);
//        }
//
//        for (Article article: articleList) {
//            Log.e(TAG, "listItem: " + article.Id);
//        }
//    }
}
