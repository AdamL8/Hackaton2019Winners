package co.w.mynewscast.ui.experience;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import co.w.mynewscast.R;
import co.w.mynewscast.model.Article;

public class ExperienceArticleAdapter extends RecyclerView.Adapter<ExperienceArticleAdapter.ArticleViewHolder> implements View.OnClickListener {

    Context context;
    List<Article> articles;

    public ExperienceArticleAdapter(Context context, List<Article> objects)
    {
        this.context = context;
        this.articles = objects;
    }

    @Override public void onClick(View view) {
        // selected item
        //((TextView) view.findViewById(R.id.articleTitle)).setText("yoyoyo");



    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.experience_item, parent, false);
        itemView.setOnClickListener(this);
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
        public TextView description;

        public ArticleViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.listview_item_title);
            description = (TextView) view.findViewById(R.id.listview_item_short_description);
            thumbnail = (ImageView) view.findViewById(R.id.listview_image);
        }
    }
}
