package com.codepath.simpletweets.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pnroy on 10/1/15.
 */

/*user": {
        "name": "OAuth Dancer",
        "profile_sidebar_fill_color": "DDEEF6",
        "profile_background_tile": true,
        "profile_sidebar_border_color": "C0DEED",
        "profile_image_url": "http://a0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",*/
@Table(name = "Users")
public class User extends Model implements Parcelable {
    //list the attributes
    @Column(name = "remoteId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String uid;
    @Column(name = "name")
    public String name;
    @Column(name = "screenName")
    public String screenName;
    @Column(name = "profileImageUrl")
    public String profileImageUrl;
    @Column(name = "profileBackgroundImageUrl")
    public String profileBackgroundImageUrl;
    @Column(name = "description")
    public String description;
    @Column(name = "statusesCount")
    public int statusesCount;
    @Column(name = "followersCount")
    public int followersCount;
    @Column(name = "followingCount")
    public int followingCount;
    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public User() {
        super();
    }

   /* public static User fromJSON(JSONObject json){
       User u=new User();
        try{
            u.name=json.getString("name");
            u.uid=json.getString("id");
            u.screenName=json.getString("screen_name");
            u.profileImageUrl=json.getString("profile_image_url");

    }catch(JSONException e){
            e.printStackTrace();
        }
        return u;

        }*/
    @Override
    public int describeContents() {
        return 0;
    }

    public User(JSONObject object) {
        super();

        try {
            this.uid = object.getString("id_str");
            this.name = object.getString("name");
            this.screenName = object.getString("screen_name");
            this.profileImageUrl = object.getString("profile_image_url");
            this.profileBackgroundImageUrl = object.getString("profile_background_image_url");
            this.description = object.getString("description");
            this.statusesCount = object.getInt("statuses_count");
            this.followersCount = object.getInt("followers_count");
            this.followingCount = object.getInt("listed_count");
            this.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void writeToParcel(Parcel dest,int flags){
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(screenName);
        dest.writeString(profileImageUrl);
        dest.writeString(profileBackgroundImageUrl);
        dest.writeString(description);
        dest.writeInt(statusesCount);
        dest.writeInt(followersCount);
        dest.writeInt(followingCount);
    }

    public static final Creator<User> CREATOR
            = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public static User getById(String userId) {
        return new Select()
                .from(User.class)
                .where("id = ?", userId)
                .executeSingle();
    }

    private User(Parcel in) {
        uid = in.readString();
        name = in.readString();
        screenName = in.readString();
        profileImageUrl = in.readString();
        profileBackgroundImageUrl = in.readString();
        description = in.readString();
        statusesCount = in.readInt();
        followersCount = in.readInt();
        followingCount = in.readInt();
    }


    //
}
