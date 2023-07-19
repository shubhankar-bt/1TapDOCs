package com.shubhankarsudip.a1tapdocs.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.shubhankarsudip.a1tapdocs.ui.DebitCardFragment;
import com.shubhankarsudip.a1tapdocs.ui.slideshow.CreditCardFragment;
import java.util.Objects;

public class CardViewPagerAdapter extends FragmentPagerAdapter {

    private final int totalTabs;


    public CardViewPagerAdapter(@NonNull FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if (position == 1) {
            return new CreditCardFragment();
        }
        return new DebitCardFragment();

    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
