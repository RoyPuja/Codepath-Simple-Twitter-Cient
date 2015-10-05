package com.codepath.simpletweets;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.simpletweets.Dialogs.PostTweetDialog;
import com.codepath.simpletweets.helper.EndlessScrollListener;
import com.codepath.simpletweets.models.Tweets;
import com.codepath.simpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity implements PostTweetDialog.PostTweetDialogListener {

    private TwitterClient client;
    private ArrayList<Tweets> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // add tweeter icon to actionbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        lvTweets=(ListView)findViewById(R.id.lvTweets);
        tweets=new ArrayList<>();
        aTweets=new TweetsArrayAdapter(this,tweets);
        lvTweets.setAdapter(aTweets);
        user=new User();

        client=TwitterApplication.getRestClient();//singleton client.
        getUserProfile();

         populateTimeline(0);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline(page);
            }
        });

    }

    private void getUserProfile() {
        client.getUserProfile(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                Log.d("DEBUG", "user: " + jsonObject.toString());
                user = new User(jsonObject);
            }
        });
    }

    public void onPost(Tweets tweet) {

        if (tweet != null) {
            // pass tweet to tweet list fragment
            // tweetListFragment.insertTweet(tweet);
            aTweets.insert(tweet, 0);
            populateTimeline(0);
            aTweets.notifyDataSetChanged();

        }
    }
    //Send API request to get the timeline JSON and fill the listview by creating tweet objects from JSON
    private void populateTimeline(int finalpage) {
        client.getHomeTimeline(finalpage,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                // Toast.makeText(TimelineActivity.this, json.toString(), Toast.LENGTH_LONG).show();
                //Log.d("DEBUG", json.toString());

                aTweets.addAll(Tweets.fromJSONArray(json));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
        //successful requst


        //failure


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_tweet) {
            FragmentManager fm = getSupportFragmentManager();
            PostTweetDialog postTweetDialog = PostTweetDialog.newInstance(user, null);
            postTweetDialog.show(fm, "fragment_post_tweet");
            return true;

        }

        return super.onOptionsItemSelected(item);
    }


}


