package com.codepath.simpletweets.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pnroy on 10/1/15.
 */

//Parse the JSON ans store the logic
public class Tweets extends Model implements Parcelable{
    @Column(name = "remoteId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String uid;
    @Column(name = "User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public User user;
    @Column(name = "isMention")
    public int isMention;
    @Column(name = "createdAt")
    public String createdAt;
    @Column(name = "timestamp", index = true)
    private Date timestamp;
    @Column(name = "body")
    public String body;
    @Column(name = "retweetCount")
    public int retweetCount;
    @Column(name = "favoriteCount")
    public int favoriteCount;
    public boolean favorited;

    public String getBody() {
        return body;
    }

    public String getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Tweets(){
        super();
    }

    /*public static Tweets fromJSON(JSONObject jsonObject) {
        Tweets tweet = new Tweets();
        try {

            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getString("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user=(User(jsonObject.getJSONObject("user")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }*/
    public Tweets(JSONObject object){
        super();

        try {
            this.uid = object.getString("id_str");
            JSONObject userJSONObject = object.getJSONObject("user");
            this.user = new User(userJSONObject);
            this.createdAt = object.getString("created_at");
           // this.timestamp = ParseRelativeDate.getRelativetimeAgo(this.createdAt);
            this.body = object.getString("text");
            this.retweetCount = object.getInt("retweet_count");
            this.favoriteCount = object.getInt("favorite_count");
            this.favorited = object.getBoolean("favorited");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<Tweets> fromJSONArray(JSONArray jsonArray) {
       ArrayList<Tweets> tweets=new ArrayList<>();

        for(int i=0;i<jsonArray.length();i++) {
            try {

                JSONObject tweetJSON = jsonArray.getJSONObject(i);
                Tweets tweet=new Tweets(tweetJSON);
                if(tweet!=null){
                    tweets.add(tweet);

                }

            }catch(JSONException e){
                e.printStackTrace();
                continue;
            }

        }
        return tweets;

    }

    public User getUser() {
        return user;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void writeToParcel(Parcel dest,int flags){
        dest.writeParcelable(user, flags);
        dest.writeString(createdAt);
        dest.writeString(body);
        dest.writeLong(timestamp.getTime());
        dest.writeInt(retweetCount);
        dest.writeInt(favoriteCount);
        dest.writeInt(favorited ? 1 : 0);

    }
  /*  public static List<Tweets> getAll(int type) {
        if (type == TweetType.MENTIONS.ordinal()) {
            return new Select()
                    .from(Tweets.class)
                    .where("isMention = ?", 1)
                    .orderBy("timestamp DESC")
                    .execute();
        } else {
            return new Select()
                    .from(Tweets.class)
                    .orderBy("timestamp DESC")
                    .execute();
        }
    }

    public static List<Tweets> getUserTweets(String userId) {
        return new Select()
                .from(Tweets.class)
                .where("User = ?", userId)
                .orderBy("timestamp DESC")
                .execute();
    }

    public static void deleteAll() {
        new Delete()
                .from(Tweets.class)
                .execute();
    }

    public static final Creator<Tweets> CREATOR
            = new Creator<Tweets>() {
        @Override
        public Tweets createFromParcel(Parcel in) {
            return new Tweets(in);
        }

        @Override
        public Tweets[] newArray(int size) {
            return new Tweets[size];
        }
    };

    private Tweets(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
        createdAt = in.readString();
        body = in.readString();
        timestamp = new Date(in.readLong());
        retweetCount = in.readInt();
        favoriteCount = in.readInt();
        favorited = in.readInt() == 1;
    }*/
}


