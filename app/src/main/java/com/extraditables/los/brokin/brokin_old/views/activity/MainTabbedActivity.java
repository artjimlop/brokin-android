package com.extraditables.los.brokin.brokin_old.views.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.extraditables.los.brokin.R;
import com.extraditables.los.brokin.brokin_old.db.DatabaseHelper;
import com.extraditables.los.brokin.brokin_old.models.UserModel;
import com.extraditables.los.brokin.brokin_old.views.fragments.StocksListFragment;
import com.extraditables.los.brokin.brokin_old.views.fragments.UserStockListFragment;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.component.ApplicationComponent;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.component.DaggerStocksComponent;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.module.ActivityModule;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.injector.module.StockModule;
import com.extraditables.los.brokin.re_brokin.android.view.activities.BaseActivity;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainTabbedActivity extends BaseActivity {

    @Bind(R.id.pager) ViewPager viewPager;
    @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Bind(R.id.toolbar_main) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle("Username");
        toolbar.setSubtitle("Sign in to play");

        getUserInfoFromDB();

        SectionsPagerAdapter mSectionsPagerAdapter =
            new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.view_pager_margin));
        viewPager.setPageMarginDrawable(R.drawable.page_margin);

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);
    }

    @Override protected void initializeInjector(ApplicationComponent applicationComponent) {
        applicationComponent.inject(this);
        DaggerStocksComponent.builder()
            .applicationComponent(applicationComponent)
            .activityModule(new ActivityModule(this))
            .stockModule(new StockModule()).build().inject(this);
    }

    @Override protected void onResume() {
        super.onResume();
        syncroProccess();
    }

    private void syncroProccess() {
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        StocksListFragment.refreshStocks();
                        UserStockListFragment.refreshStocks();
                    }
                }, 0, 10, TimeUnit.SECONDS);
    }

    private void getUserInfoFromDB() {
        try {
            DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            List<UserModel> userModels = helper.getUserModelDao().queryForAll();
            if (userModels == null || userModels.isEmpty()) {
                //TODO LOG
            } else {
                UserModel userModel = userModels.get(0);
                getSupportActionBar().setTitle(userModel.getUserName());
                getSupportActionBar().setSubtitle(userModel.getCash().toString() + "$");
                Log.e("USUARIO ", userModel.getUserName());
            }
            OpenHelperManager.releaseHelper();
        } catch (SQLException e) {
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new StocksListFragment();
                case 1:
                    return new UserStockListFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Stocks";
            case 1:
                return "Buyed Stocks";
            default:
                throw new IllegalStateException(String.format("Item title for position %d doesn't exists",
                        position));
        }
    }
    }

}
