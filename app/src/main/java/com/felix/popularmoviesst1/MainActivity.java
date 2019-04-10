package com.felix.popularmoviesst1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.felix.popularmoviesst1.adapter.MovieAdapter;
import com.felix.popularmoviesst1.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.felix.popularmoviesst1.utilities.Constant.API_KEY;
import static com.felix.popularmoviesst1.utilities.Constant.MOVIE_DB_BASE_URL;

import static com.felix.popularmoviesst1.utilities.Constant.POPULARITY_DESC;
import static com.felix.popularmoviesst1.utilities.Constant.SORTED_BY;
import static com.felix.popularmoviesst1.utilities.Constant.VOTE_AVERAGE_DESC;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnItemClickListener {

    public static final String MOVIE = "movie";

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private List<Movie> mMovieList;
    private RequestQueue mRequestQueue;
    private String url = MOVIE_DB_BASE_URL + API_KEY + SORTED_BY ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, 500);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        mMovieList = new ArrayList<>();

        mRequestQueue = Volley.newRequestQueue(this);
        parseJSON(url + POPULARITY_DESC);
    }

    private void parseJSON(String url) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject result = jsonArray.getJSONObject(i);

                                double voteAverage = result.getDouble("vote_average");
                                String title = result.getString("title");
                                String overView = result.getString("overview");
                                String releaseDate = result.getString("release_date");

                                String posterPath = result.getString("poster_path");
                                String imageUrl = "https://image.tmdb.org/t/p/w185" + posterPath;

                                mMovieList.add(new Movie(voteAverage, title, overView, releaseDate, imageUrl));
                            }

                            mMovieAdapter = new MovieAdapter(MainActivity.this, mMovieList);
                            mRecyclerView.setAdapter(mMovieAdapter);
                            mMovieAdapter.notifyDataSetChanged();
                            mMovieAdapter.setOnItemClickListener(MainActivity.this);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sort_popular:
                mMovieList.clear();
                parseJSON(url + POPULARITY_DESC);
                mMovieAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_rating:
                mMovieList.clear();
                parseJSON(url + VOTE_AVERAGE_DESC);
                mMovieAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        Movie movie = mMovieList.get(position);

        intent.putExtra(MOVIE, movie);

        startActivity(intent);
    }
}
