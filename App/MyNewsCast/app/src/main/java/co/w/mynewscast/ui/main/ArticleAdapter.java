package co.w.mynewscast.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import co.w.mynewscast.R;
import co.w.mynewscast.model.Article;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    Context context;
    List<Article> articles;

    public ArticleAdapter(Context context, List<Article> objects)
    {
        this.context = context;
        this.articles = objects;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_item, parent, false);

        return new ArticleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ArticleViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.title.setText(article.Title);

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_broken_image_grey_128dp);

        // loading album cover using Glide library
        Glide.with(context).load(article.Image).apply(requestOptions).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return this.articles.size();
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;

        public ArticleViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.articleTitle);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }
}
