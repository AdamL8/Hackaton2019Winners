package co.w.mynewscast.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Article {

    public String CategoryName;
    public String Description;
    public String Title;
    public String Url;
    public String Image;
    public Integer Id;
    public boolean IsSelected;

    public Article() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Article (JSONObject object)
    {
        if (object != null)
        {
            try
            {
                CategoryName = object.getString("categoryName");
                Description = object.getString("description");
                Title = object.getString("title");
                Url = object.getString("url");
                Id = object.getInt("id");
                Image = object.getString("image");
            }
            catch (JSONException e)
            {
                Log.w("ERRROR", e);
            }
        }
    }
}
