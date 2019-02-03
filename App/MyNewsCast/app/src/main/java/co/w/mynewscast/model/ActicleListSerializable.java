package co.w.mynewscast.model;

import java.io.Serializable;
import java.util.List;

public class ActicleListSerializable implements Serializable {

    public List<Article> articles;

    public ActicleListSerializable(List<Article> articles)
    {
        this.articles = articles;
    }
}
