package android.test.tinkoff;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by dmitriy on 5/13/17.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsEntry> mNews;
    private Context mContext;

    public NewsAdapter(List<NewsEntry> news, Context context){
        mNews = news;
        mContext = context;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.news_entry, parent, false);
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final NewsViewHolder holder, int position) {
        final NewsEntry current = mNews.get(position);
        String newsText = current.getText();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.newsTextView.setText(Html.fromHtml(newsText, Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.newsTextView.setText(Html.fromHtml(newsText));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String url = "https://api.tinkoff.ru/v1/news_content?id=";
                StringRequest contentRequest = new StringRequest(Request.Method.GET, url + current.getId(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonParser parser = new JsonParser();
                        JsonObject payload = parser.parse(response).getAsJsonObject().get("payload").getAsJsonObject();
                        String content = payload.get("content").getAsString();
                        Intent intent = new Intent(mContext,NewsContent.class);
                        intent.putExtra("content",content);
                        mContext.startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext,R.string.error,Toast.LENGTH_SHORT).show();
                    }
                });
                RequestQueueSingleton.getInstance(mContext).addToRequestQueue(contentRequest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }

    public void clear(){
        mNews.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<NewsEntry> news){
        mNews.addAll(news);
        notifyDataSetChanged();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{

        private TextView newsTextView;

        public NewsViewHolder(View itemView) {
            super(itemView);
            newsTextView = (TextView) itemView.findViewById(R.id.news_text);
        }
    }
}
