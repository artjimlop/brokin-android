package com.extraditables.los.brokin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.extraditables.los.brokin.db.DatabaseHelper;
import com.extraditables.los.brokin.models.UserModel;
import com.extraditables.los.brokin.models.UserStockModel;
import com.extraditables.los.brokin.views.fragments.StocksListFragment;
import com.extraditables.los.brokin.views.fragments.UserStockListFragment;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;


public class MainTabbedActivity extends ActionBarActivity {

    private final String LOG_TAG = getClass().getSimpleName();

    @InjectView(R.id.pager) ViewPager viewPager;
    @InjectView(R.id.tab_layout) TabLayout tabLayout;
    @InjectView(R.id.toolbar) Toolbar toolbar;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);
        ButterKnife.inject(this);
        this.context = this;
        // Set a toolbar to replace the action bar.
        setSupportActionBar(toolbar);
        //TODO get user form db, then update toolbar & insert in sharedpreferences
        toolbar.setTitle("Username");
        toolbar.setSubtitle("Sign in to play");

        getUserInfoFromDB();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.view_pager_margin));
        viewPager.setPageMarginDrawable(R.drawable.page_margin);

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);

        syncroProccess();

        handleNotificationsNYSE();

    }

    private void syncroProccess() {
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        //syncro stockListFragment
                        StocksListFragment.refreshStocks();
                        //syncro userStockListFragment
                        UserStockListFragment.refreshStocks();
                    }
                }, 0, 1, TimeUnit.MINUTES);
    }

    private void handleNotificationsNYSE() {
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        //Llamar a BDD, obtener usuario
                        //Llamar a BDD, obtener stocks
                        //Hacer calculo

                        TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
                        int offset = timeZone.getOffset(new Date().getTime());

                        int difference = offset / 1000 / 60 / 60 - 2;
                        int hours = new Date().getHours();
                        int minutes = new Date().getMinutes();
                        int hoursInAbsolute = Math.abs(hours + difference);

                        Calendar c = Calendar.getInstance();
                        c.setTime(new Date());
                        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

                        Boolean open = hoursInAbsolute == 9 && minutes == 30 && dayOfWeek != 1 && dayOfWeek != 7;
                        Boolean close = hoursInAbsolute == 16 && minutes == 0 && dayOfWeek != 1 && dayOfWeek != 7;

                        if (open || close) {
                            // call service
                            Intent intent = new Intent(context, MainTabbedActivity.class);
                            // use System.currentTimeMillis() to have a unique ID for the pending intent
                            PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

                            // build notification
                            // the addAction re-use the same intent to keep the example short
                            Notification n = null;

                            try {
                                n = new Notification.Builder(context)
                                        .setContentTitle("Brokin")
                                        .setContentText("Current earns: " + String.valueOf(getUserEarns()) + "$")
                                        .setSmallIcon(R.drawable.ic_stat_notification)
                                        .setContentIntent(pIntent)
                                        .setAutoCancel(true).getNotification();
                            } catch (IOException e) {
                                Log.d("SHIT: ", "error obteniendo earns");
                            }

                            NotificationManager notificationManager =
                                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                            notificationManager.notify(0, n);
                        }
                    }


                }, 0, 1, TimeUnit.MINUTES);
    }

    private BigDecimal getUserEarns() throws IOException {
        Float earns = 0.0f;
        Float spent = 0.0f;
        try {
            DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            List<UserStockModel> userStockModels = helper.getUserStockModelDao().queryForAll();
            if (userStockModels == null) {
                Log.d(LOG_TAG, "Ningún earn");
            } else {
                for (UserStockModel userStockModel : userStockModels) {
                    Stock stock = YahooFinance.get(userStockModel.getSymbol());
                    earns += stock.getQuote().getPrice().floatValue() * userStockModel.getNumberOfStocks();
                    spent += userStockModel.getValue() * userStockModel.getNumberOfStocks();
                }
            }
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error obteniendo earns");
            Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
        }
        return round(earns - spent,2);
    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    private void getUserInfoFromDB() {
        try {
            DatabaseHelper helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            List<UserModel> userModels = helper.getUserModelDao().queryForAll();
            if (userModels == null || userModels.isEmpty()) {
                Log.d(LOG_TAG, "Ningún usuario");
            } else {
                UserModel userModel = userModels.get(0);
                getSupportActionBar().setTitle(userModel.getUserName());
                getSupportActionBar().setSubtitle(userModel.getCash().toString() + "$");
                Log.e("USUARIO ", userModel.getUserName());
            }
            OpenHelperManager.releaseHelper();
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error obteniendo usuario");
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_main_tabbed, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    */
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
