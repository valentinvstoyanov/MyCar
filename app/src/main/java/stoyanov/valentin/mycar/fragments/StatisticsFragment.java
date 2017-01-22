package stoyanov.valentin.mycar.fragments;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import stoyanov.valentin.mycar.R;

public class StatisticsFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;

    public StatisticsFragment() {}


    class ViewPagerAdapter extends FragmentPagerAdapter{

        private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
        private final ArrayList<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar_layout);
        viewPager = (ViewPager) view.findViewById(R.id.vp_statistics);
        tabLayout = new TabLayout(getContext());
        int color = ResourcesCompat.getColor(getResources(), R.color.colorTextIcons, null);
        tabLayout.setSelectedTabIndicatorHeight(16);
        tabLayout.setTabTextColors(color, color);
        tabLayout.setupWithViewPager(viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        Bundle bundle = getArguments();
        LineChartFragment fragment = new LineChartFragment();
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, "EXPENSES");
        FuelPriceFragment fuelPriceFragment = new FuelPriceFragment();
        fuelPriceFragment.setArguments(bundle);
        adapter.addFragment(fuelPriceFragment, "FUEL PRICE");
        viewPager.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        appBarLayout.addView(tabLayout);
    }

    @Override
    public void onPause() {
        super.onPause();
        appBarLayout.removeView(tabLayout);
    }
}
