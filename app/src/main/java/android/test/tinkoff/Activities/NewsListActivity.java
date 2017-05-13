package android.test.tinkoff.Activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.test.tinkoff.Adapters.NewsAdapter;
import android.test.tinkoff.Helpers.NewsEntry;
import android.test.tinkoff.R;
import android.test.tinkoff.Helpers.RequestQueueSingleton;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class NewsListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeContainer;
    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;
    private TextView mEmptyResultsTextView;

    String url = "https://api.tinkoff.ru/v1/news";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeContainer.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.news_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,llm.getOrientation()));
        mEmptyResultsTextView = (TextView) findViewById(R.id.emptyView);

        RequestQueue queue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        JsonObjectRequest newsRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<NewsEntry> news = null;
                try {
                    news = parseJsonResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                populateList(news);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyResultsTextView.setVisibility(View.VISIBLE);
            }
        });

        RequestQueueSingleton.getInstance(this).addToRequestQueue(newsRequest);
    }

    public void populateList(List<NewsEntry> news){
        mAdapter = new NewsAdapter(news,NewsListActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void updateList(List<NewsEntry> news){
        mAdapter.clear();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.addAll(news);
    }

    @Override
    public void onRefresh() {
        JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<NewsEntry> news = null;
                try {
                    news = parseJsonResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateList(news);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyResultsTextView.setVisibility(View.VISIBLE);
            }
        });
        RequestQueueSingleton.getInstance(this).addToRequestQueue(updateRequest);
        mSwipeContainer.setRefreshing(false);
    }

    public List<NewsEntry> parseJsonResponse(JSONObject response) throws JSONException {
        JSONArray newsArray = response.getJSONArray("payload");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<NewsEntry>>() {}.getType();
        List<NewsEntry> news = gson.fromJson(newsArray.toString(), listType);
        Collections.sort(news, new NewsEntry.DateComparator());
        return news;
    }
}
