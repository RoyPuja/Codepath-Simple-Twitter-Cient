package com.codepath.simpletweets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.simpletweets.R;
import com.codepath.simpletweets.helper.ParseRelativeDate;

import com.codepath.simpletweets.models.Tweets;
import com.codepath.simpletweets.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by pnroy on 10/1/15.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweets> implements View.OnClickListener{

    private TweetsAdapterListener listener;

    public interface TweetsAdapterListener{
        public void onReplyTweet(Tweets tweet);
        public void onRetweet(Tweets tweet);
        public void onFavoriteTweet(Tweets tweet);
        public void onViewUserProfile(User user);
    }

    private static class ViewHolder{
        ImageView ivUserImage;
        TextView tvUserName;
        TextView tvUserHandler;
        TextView tvBody;
        TextView tvTimestamp;
        TextView tvRetweetCount;
        TextView tvFavoriteCount;

    }

    public TweetsArrayAdapter(Context context, List<Tweets> tweets, TweetsAdapterListener listener){
        super(context,android.R.layout.simple_list_item_1,tweets);
        this.listener=listener;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //GEt the tweet
        Tweets tweet=getItem(position);
        //Find or inflate the template
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.item_tweet,parent,false);


            viewHolder=new ViewHolder();
            //Find the subview to fill with data
            viewHolder.ivUserImage=(ImageView)convertView.findViewById(R.id.ivProfileImg);
            viewHolder.tvUserName=(TextView)convertView.findViewById(R.id.tvUserName);
            viewHolder.tvBody=(TextView)convertView.findViewById(R.id.tvBody);
            viewHolder.tvTimestamp=(TextView)convertView.findViewById(R.id.tvTimestamp);
            viewHolder.tvRetweetCount = (TextView)convertView.findViewById(R.id.tvRetweetCount);
            viewHolder.tvFavoriteCount = (TextView)convertView.findViewById(R.id.tvFavoriteCount);
            viewHolder.tvUserHandler=(TextView)convertView.findViewById(R.id.tvUserHandle);
            convertView.setTag(viewHolder);}
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.tvUserName.setText(tweet.getUser().getName());
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvTimestamp.setText(ParseRelativeDate.getRelativetimeAgo(tweet.getCreatedAt()));
        viewHolder.ivUserImage.setImageResource(android.R.color.transparent);//clear out the image for a recycled view
        viewHolder.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
        viewHolder.tvRetweetCount.setTag(position);
        viewHolder.tvRetweetCount.setOnClickListener(this);
        viewHolder.tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
        viewHolder.tvFavoriteCount.setTag(position);
        viewHolder.tvUserHandler.setText("@"+tweet.getUser().screenName);
        viewHolder.tvFavoriteCount.setOnClickListener(this);
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.ivUserImage);
        viewHolder.ivUserImage.setTag(position);
        viewHolder.ivUserImage.setOnClickListener(this);

        return convertView;
    }
    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }
        int position = (int)v.getTag();
        Tweets tweet = getItem(position);
        if (v.getId() == R.id.ibReply) {
            listener.onReplyTweet(tweet);
        } else if (v.getId() == R.id.tvRetweetCount) {
            listener.onRetweet(tweet);
        } else if (v.getId() == R.id.tvFavoriteCount) {
            listener.onFavoriteTweet(tweet);
        } else if (v.getId() == R.id.ivProfileImg) {
            listener.onViewUserProfile(tweet.user);
        }
    }

    //override and set up custom template
}

