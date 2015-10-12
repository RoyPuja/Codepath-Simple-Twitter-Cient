package com.codepath.simpletweets.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.simpletweets.Dialogs.PostTweetDialog;
import com.codepath.simpletweets.R;
import com.codepath.simpletweets.TwitterApplication;
import com.codepath.simpletweets.TwitterClient;
import com.codepath.simpletweets.adapters.TweetsArrayAdapter;
import com.codepath.simpletweets.adapters.TwitterPagerAdapter;
import com.codepath.simpletweets.fragments.TwitterListFragment;
import com.codepath.simpletweets.helper.EndlessScrollListener;
import com.codepath.simpletweets.models.Tweets;
import com.codepath.simpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity implements PostTweetDialog.PostTweetDialogListener,TwitterListFragment.TweetListFragmentListener {


    private TwitterListFragment fragmenttwitterlist;
    private User user;
    private TwitterClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        client = TwitterApplication.getRestClient();
        getUserProfile();

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TwitterPagerAdapter(getSupportFragmentManager()));

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
    }

    private void getUserProfile() {
        client.getUserProfile(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                Log.d("DEBUG", "user: " + jsonObject.toString());
                user = new User(jsonObject);
            }
        });
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

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_tweet) {
            FragmentManager fm = getSupportFragmentManager();
            PostTweetDialog postTweetDialog = PostTweetDialog.newInstance(user, null);
            postTweetDialog.show(fm, "fragment_post_tweet");
        } else if (id == R.id.action_profile) {
            showUserProfile(user);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPost(Tweets tweet) {
        if (tweet != null) {
            // pass tweet to tweet list fragment
            fragmenttwitterlist.insertTweet(tweet);
        }
    }

    @Override
    public void onReplyTweet(Tweets tweet) {
        FragmentManager fm = getSupportFragmentManager();
        PostTweetDialog postTweetDialog = PostTweetDialog.newInstance(user, tweet);
        postTweetDialog.show(fm, "fragment_reply_tweet");
    }

    @Override
    public void onViewUserProfile(User user) {
        if (user != null) {
            showUserProfile(user);
        }
    }

    private void showUserProfile(User user) {
        Intent userProfileIntent = new Intent(this, UserProfileActivity.class);
        userProfileIntent.putExtra("user", user);
        startActivity(userProfileIntent);
    }

}


