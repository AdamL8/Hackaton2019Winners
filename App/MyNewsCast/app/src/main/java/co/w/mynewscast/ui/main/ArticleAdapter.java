package co.w.mynewscast.ui.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.List;

import co.w.mynewscast.R;
import co.w.mynewscast.model.Article;

public class ArticleAdapter extends ArrayAdapter<Article> {

    Context context;
    List<Article> objects;

    public ArticleAdapter(Context context, List<Article> objects)
    {
        super(context, 0, objects);

        this.context = context;
        this.objects = objects;
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        ArticleCell cell = new ArticleCell();

        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.article_item, parent, false);

        cell.articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
        cell.articleTitleTextView = (TextView) view.findViewById(R.id.articleTextView);

        Article article = getItem(position);

        if (article != null) {
            new DownloadImageTask(cell.articleImageView).execute(article.Image);
        }

        return view;
    }

    private class ArticleCell
    {
        // Article items;
        ImageView articleImageView;
        TextView articleTitleTextView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
