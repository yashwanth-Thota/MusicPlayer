package com.example.yashwanth.echo.Adapters;

import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.yashwanth.echo.Fragments.AboutUsFragment;
import com.example.yashwanth.echo.Fragments.FavouritesFragment;
import com.example.yashwanth.echo.Fragments.MainScreenFragment;
import com.example.yashwanth.echo.Fragments.SettingsFragment;

/**
 * Created by YASHWANTH on 11-04-2018.
 */

public class PagerADAPTER extends FragmentStatePagerAdapter {
    int noOfTabs;

    public PagerADAPTER(FragmentManager fm,int n) {
        super(fm);
        noOfTabs=n;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position){
            case 0:
                MainScreenFragment m=new MainScreenFragment();
                return m;

            case 1:
                FavouritesFragment f=new FavouritesFragment();
                return f;
            case 2:
                SettingsFragment s=new SettingsFragment();
                return s;
            case 3:
                AboutUsFragment a=new AboutUsFragment();
                return a;

                default:
                    return null;
        }

    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
