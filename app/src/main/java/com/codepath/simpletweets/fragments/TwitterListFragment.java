package com.codepath.simpletweets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.codepath.simpletweets.R;
import com.codepath.simpletweets.TwitterApplication;
import com.codepath.simpletweets.TwitterClient;
import com.codepath.simpletweets.activities.DetailActivity;
import com.codepath.simpletweets.adapters.TweetsArrayAdapter;
import com.codepath.simpletweets.enums.TweetType;
import com.codepath.simpletweets.helper.EndlessScrollListener;
import com.codepath.simpletweets.models.Tweets;
import com.codepath.simpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pnroy on 10/6/15.
 */
public class TwitterListFragment extends Fragment implements AdapterView.OnItemClickListener, TweetsArrayAdapter.TweetsAdapterListener {
    //Inflation logic
    private TwitterListFragment fragmenttweetslist;
    private ArrayList<Tweets> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private User user;
    private int type;
    private TwitterClient twitterClient;
    private ProgressBar progressBarFooter;
    private SwipeRefreshLayout swipeContainer;

    private static final String ARG_TYPE = "ARG_TYPE";
    private static final String ARG_USER = "ARG_USER";

    public static TwitterListFragment newInstance(int type, User user) {
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        args.putParcelable(ARG_USER, user);
        TwitterListFragment tweetListFragment = new TwitterListFragment();
        tweetListFragment.setArguments(args);
        return tweetListFragment;
    }

    public interface TweetListFragmentListener {
        public void onReplyTweet(Tweets tweet);
        public void onViewUserProfile(User user);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        type = getArguments().getInt(ARG_TYPE);
        user = getArguments().getParcelable(ARG_USER);

        twitterClient = TwitterApplication.getRestClient();
        List<Tweets> tweets;

        if (type == TweetType.USER.ordinal() && user != null) {
            tweets = Tweets.getUserTweets(user.uid);
        } else {
            tweets = Tweets.getAll(type);
        }
        if (tweets.size() == 0) {
            populateTimeline(1);
        }
        aTweets = new TweetsArrayAdapter(getActivity(), tweets, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweet_list, parent, false);

        //set up list view
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        //Inflate the footer
        View footer = inflater.inflate(R.layout.footer_tweet_list_progress, null);
        //find the progress bar within the footer
        progressBarFooter = (ProgressBar) footer.findViewById(R.id.pbFooterLoading);
        //Add the footer to the listview
        lvTweets.addFooterView(footer);
        //setting adapter after adding footer
        lvTweets.setAdapter(aTweets);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline(page);
            }
        });

        lvTweets.setOnItemClickListener(this);

        // setup our swipe to refresh container
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateTimeline(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return v;
    }

    public void AddAll(List<Tweets> tweetlist) {
        aTweets.addAll(tweetlist);
    }

    private void populateTimeline(final int page) {
        String userId = (type == TweetType.USER.ordinal() && user != null) ? user.uid : null;
        twitterClient.getTimeline(page, type, userId, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                if (page == 0) {
                    aTweets.clear();
                }
                Log.d("DEBUG", "timeline: " + jsonArray.toString());
                // Load json array into model classes
                aTweets.addAll(Tweets.fromJSONArray(jsonArray, type));
                aTweets.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
                if (progressBarFooter != null) {
                    progressBarFooter.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                if (progressBarFooter != null) {
                    progressBarFooter.setVisibility(View.INVISIBLE);
                }
            }
        });
        if (progressBarFooter != null) {
            progressBarFooter.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Tweets tweet = aTweets.getItem(position);
        Intent i = new Intent(getActivity(), DetailActivity.class);
        i.putExtra("tweet", tweet);
        startActivity(i);
    }

    @Override
    public void onReplyTweet(Tweets tweet) {
        TweetListFragmentListener tweetListFragmentListener = (TweetListFragmentListener)getActivity();
        tweetListFragmentListener.onReplyTweet(tweet);
    }
    @Override
    public void onViewUserProfile(User user) {
        TweetListFragmentListener tweetListFragmentListener = (TweetListFragmentListener)getActivity();
        tweetListFragmentListener.onViewUserProfile(user);
    }

    @Override
    public void onRetweet(Tweets tweet) {
        twitterClient.postRetweet(tweet.uid, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {

                Log.d("DEBUG", "retweet: " + jsonObject.toString());
                // Load json array into model classes
                Tweets retweetedTweet = new Tweets(jsonObject);
                retweetedTweet.save();

                aTweets.insert(retweetedTweet, 0);
                aTweets.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    @Override
    public void onFavoriteTweet(final Tweets tweet) {
        twitterClient.postFavoriteTweet(tweet.uid, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                Log.d("DEBUG", "favorited: " + jsonObject.toString());

                Tweets retweetedTweet = new Tweets(jsonObject);
                retweetedTweet.save();
                // update the favorited status
                tweet.favorited = retweetedTweet.favorited;
                aTweets.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }



    public void insertTweet(Tweets tweet) {
        aTweets.insert(tweet, 0);
        aTweets.notifyDataSetChanged();
    }
}












