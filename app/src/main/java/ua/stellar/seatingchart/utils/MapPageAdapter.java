package ua.stellar.seatingchart.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import ua.stellar.seatingchart.MapFragment;

public class MapPageAdapter extends FragmentStatePagerAdapter {

    private final List<MapFragment> fragmentList = new ArrayList<>();
    private final List<String> titleList = new ArrayList<>();

    public MapPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(final MapFragment fragment, String title) {
        fragmentList.add(fragment);
        titleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
