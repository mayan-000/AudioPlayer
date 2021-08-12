package com.company.audioplayer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class viewPagerAdapter extends FragmentStateAdapter {
    public viewPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;

        if(position==0){
            fragment = MainFragment.getInstance();
        }
        else if(position==1){
            fragment = AllSongsFragment.getInstance();
        }
        else if(position==2){
            fragment = SongsFolderFragment.getInstance();
        }
        else if(position==3){
            fragment = Favourites.getInstance();
        }

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4;
    }


}
