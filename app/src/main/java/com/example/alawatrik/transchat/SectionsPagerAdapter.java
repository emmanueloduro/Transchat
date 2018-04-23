package com.example.alawatrik.transchat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

import java.nio.charset.Charset;

/**
 * Created by ALAWATRIK on 3/19/2018.
 */

class SectionsPagerAdapter extends FragmentPagerAdapter {
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            //switching to chats fragments
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;

            //switching to friends fragment
            case 1:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            //default case to return nothing, with only two fragment
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    //setting the titles for the different tabs
    public CharSequence getPageTitle(int position) {

        switch (position) {

            case 0:
                return "CHATS";

            case 1:
                return "FRIENDS";

            default:
                return null;
        }
    }

}
