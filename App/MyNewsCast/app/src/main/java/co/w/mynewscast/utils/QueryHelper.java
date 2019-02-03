package co.w.mynewscast.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class QueryHelper {

    private static String BASE_API_URL = "http://40.76.47.167/api/content";

    public static void getArticle(String lang, String id, TaskDelegate delegate) {
        String queryUrl = String.format("%s/%s/%s", BASE_API_URL, lang, id);
        new JsonTask(delegate).execute(queryUrl);
    }

    public static void getArticles(String lang, TaskDelegate delegate) {
        String queryUrl = String.format("%s/%s", BASE_API_URL, lang);
        new JsonTask(delegate).execute(queryUrl);
    }

    private static class JsonTask extends AsyncTask<String, String, String> {

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