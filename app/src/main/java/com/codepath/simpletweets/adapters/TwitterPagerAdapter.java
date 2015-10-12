package com.codepath.simpletweets.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.simpletweets.enums.TweetType;
import com.codepath.simpletweets.fragments.TwitterListFragment;

/**
 * Created by pnroy on 10/10/15.
 */
public class TwitterPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Home", "Mentions" };

    public TwitterPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        TweetType tweetType = TweetType.values()[position];
        return TwitterListFragment.newInstance(tweetType.ordinal(), null);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
