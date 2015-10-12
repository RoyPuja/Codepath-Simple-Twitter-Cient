package com.codepath.simpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.simpletweets.R;
import com.codepath.simpletweets.enums.TweetType;
import com.codepath.simpletweets.fragments.TwitterListFragment;
import com.codepath.simpletweets.fragments.UserDetailFragment;
import com.codepath.simpletweets.models.User;

/**
 * Created by pnroy on 10/10/15.
 */
public class UserProfileActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        User user = getIntent().getExtras().getParcelable("user");
        UserDetailFragment userDetailFragment = UserDetailFragment.newInstance(user);
        TwitterListFragment tweetListFragment = TwitterListFragment.newInstance(TweetType.USER.ordinal(), user);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.flUserProfileDetailPlaceholder, userDetailFragment);
        fragmentTransaction.replace(R.id.flUserTweetsPlaceholder, tweetListFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
