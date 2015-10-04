package com.codepath.simpletweets.Dialogs;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.simpletweets.R;
import com.codepath.simpletweets.TwitterApplication;
import com.codepath.simpletweets.TwitterClient;
import com.codepath.simpletweets.models.Tweets;
import com.codepath.simpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by pnroy on 10/3/15.
 */
public class PostTweetDialog extends DialogFragment implements TextWatcher, View.OnClickListener {

    private final int MAX_CHARACTERS = 140;

    private User user;
    private Tweets tweet;
    private TextView tvCharacterCount;
    private EditText etComposeTweet;
    private Button btnTweet;

    public interface PostTweetDialogListener {
        void onPost(Tweets tweet);
    }




    public PostTweetDialog() {}

    public static PostTweetDialog newInstance(User user, Tweets tweet) {
        PostTweetDialog frag = new PostTweetDialog();
        frag.setStyle(STYLE_NORMAL, R.style.Theme_CustomDialog);
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        args.putParcelable("tweet", tweet);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_tweet, container);
        user = getArguments().getParcelable("user");
        tweet = getArguments().getParcelable("tweet");

        TextView tvUserName = (TextView)view.findViewById(R.id.tvUserName);
        tvUserName.setText(user.getName());
        TextView tvScreenName = (TextView)view.findViewById(R.id.tvScreeName);
        tvScreenName.setText("@" + user.getScreenName());
        ImageView ivUserProfileImage = (ImageView)view.findViewById(R.id.ivUserProfileImage);
        Picasso.with(getActivity())
                .load(user.getProfileImageUrl())
                .into(ivUserProfileImage);
        tvCharacterCount = (TextView)view.findViewById(R.id.tvCharacterCount);
        etComposeTweet = (EditText)view.findViewById(R.id.etComposeTweet);
        etComposeTweet.addTextChangedListener(this);
        if (tweet != null) {
            etComposeTweet.setText("@" + tweet.getUser().getScreenName() + " ");
        }

        btnTweet = (Button)view.findViewById(R.id.btnTweet);
        btnTweet.setOnClickListener(this);

        return view;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int characters = s.toString().length();
        int charactersLeft = MAX_CHARACTERS - characters;
        tvCharacterCount.setText(String.valueOf(charactersLeft));
    }

    @Override
    public void onClick(View v) {
        String tweetBody = etComposeTweet.getText().toString();
        if (tweetBody.length() == 0) {
            Toast.makeText(getActivity(), "Please include a message in your tweet!", Toast.LENGTH_SHORT);
        }

        // check if this update is in reply to a given tweet
        String existingTweetId = null;
        if (tweet != null) {
            existingTweetId = tweet.getUid();
        }

        TwitterClient client = TwitterApplication.getRestClient();
        client.postTweet(tweetBody, existingTweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // make tweet object and pass back to timeline to add to it
                Tweets tweet = new Tweets(response);
                tweet.save();
                PostTweetDialogListener listener = (PostTweetDialogListener)getActivity();
                listener.onPost(tweet);

                dismiss();
            }
        });
    }
}
