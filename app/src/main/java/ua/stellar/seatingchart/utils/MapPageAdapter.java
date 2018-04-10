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

    private int position = -1;

    private OnChangeListener changeListener;

    public MapPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(final MapFragment fragment, String title) {
        fragmentList.add(fragment);
        titleList.add(title);
    }

    @Override
    public String getPageTitle(int position) {
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

    public int getPosition() {
        return position;
    }

    public void setPosition(final int position) {
        if (this.position != position) {
            this.position = position;
            doChangeListener(position);
        }
    }

    public void setOnChangeListener(OnChangeListener listener) {
        this.changeListener = listener;
    }

    private void doChangeListener(final int position) {
        if (changeListener != null) {
            changeListener.onChange(position, getPageTitle(position));
        }
    }

    public interface OnChangeListener {
        void onChange(final int position, final String title);
    }
}
