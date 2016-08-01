package com.losextraditables.brokin.brokin_old.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.losextraditables.brokin.R;
import com.losextraditables.brokin.brokin_old.adapters.UserStockAdapter;
import com.losextraditables.brokin.brokin_old.adapters.listeners.OnUserStockClickListener;
import com.losextraditables.brokin.brokin_old.db.DatabaseHelper;
import com.losextraditables.brokin.brokin_old.models.UserModel;
import com.losextraditables.brokin.brokin_old.models.UserStockModel;
import com.losextraditables.brokin.brokin_old.views.activity.MainTabbedActivity;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.HandlerScheduler;
import rx.functions.Func0;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class UserStockListFragment extends Fragment {

    private static final String LOG_TAG = "UserStockListFragment";

    private static TextView noStocksFound;

    private RecyclerView mRecyclerView;
    private static UserStockAdapter userStockAdapter;
    private static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_stock_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.user_stocks);

        mRecyclerView.setHasFixedSize(true);

        context = getActivity();

        noStocksFound = (TextView) getActivity().findViewById(R.id.stocks_buyed_empty);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        Handler backgroundHandler = new Handler(backgroundThread.getLooper());

        sampleObservable()
                // Run on a background thread
                .subscribeOn(HandlerScheduler.from(backgroundHandler))
                        // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<UserStockModel>>() {

                    List<UserStockModel> stocks = new ArrayList<>();

                    @Override
                    public void onCompleted() {

                        userStockAdapter = new UserStockAdapter(new OnUserStockClickListener() {
                            @Override
                            public void onUserStockClick(UserStockModel event) {

                            }
                        }, stocks, getActivity());

                        mRecyclerView.setAdapter(userStockAdapter);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, "onError()", e);
                    }

                    @Override
                    public void onNext(List<UserStockModel> stockModels) {
                        stocks.addAll(stockModels);
                        for (UserStockModel stockModel : stockModels) {
                            updateUserStockModelInDB(stockModel);
                        }
                        if(stocks.isEmpty()) {
                            noStocksFound.setVisibility(View.VISIBLE);
                        } else {
                            noStocksFound.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private static void updateUserStockModelInDB(UserStockModel stockModel) {
        DatabaseHelper helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        Dao dao;
        try {
            dao = helper.getUserStockModelDao();
            dao.update(stockModel);
            Log.e(LOG_TAG, "Actualizada stock");
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error actualizando stock");
        }
        OpenHelperManager.releaseHelper();
    }

    static Observable<List<UserStockModel>> sampleObservable() {
        return Observable.defer(new Func0<Observable<List<UserStockModel>>>() {
            @Override
            public Observable<List<UserStockModel>> call() {
                final List<UserStockModel> stockModels = new ArrayList<>();
                try {
                    DatabaseHelper helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
                    List<UserStockModel> userStockModels = helper.getUserStockModelDao().queryForAll();
                    if (userStockModels == null) {
                        Log.d(LOG_TAG, "Ningún stock");
                    } else {
                        for (UserStockModel userStockModel : userStockModels) {
                            Stock stock = null;
                            try {
                                stock = YahooFinance.get(userStockModel.getSymbol());
                            } catch (IOException e) {
                                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
                            }
                            userStockModel.setCurrentValue(stock.getQuote().getPrice().floatValue());
                            stockModels.add(userStockModel);
                        }
                    }
                } catch (SQLException e) {
                    Log.e(LOG_TAG, "Error obteniendo stocks del usuairo");
                    Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
                }

                return Observable.just(stockModels);
            }
        });
    }

    static class BackgroundThread extends HandlerThread {
        BackgroundThread() {
            super("SchedulerSample-BackgroundThread", THREAD_PRIORITY_BACKGROUND);
        }
    }

    private List<UserStockModel> getUserStockModels() throws IOException {
        List<UserStockModel> stocks = new ArrayList<>();
        try {
            DatabaseHelper helper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
            List<UserStockModel> userStockModels = helper.getUserStockModelDao().queryForAll();
            if (userStockModels == null) {
                Log.d(LOG_TAG, "Ningún stock");
            } else {
                for (UserStockModel userStockModel : userStockModels) {
                    Stock stock = YahooFinance.get(userStockModel.getSymbol());
                    userStockModel.setCurrentValue(stock.getQuote().getPrice().floatValue());
                }
                stocks = userStockModels;
            }
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error obteniendo stocks del usuairo");
            Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
        }
        return stocks;
    }

    public static void refreshStocks() {
        final Float[] currentUserMoney = {0.0f};
        BackgroundThread syncroThread = new BackgroundThread();
        syncroThread.start();
        Handler handler = new Handler(syncroThread.getLooper());
        sampleObservable()
                // Run on a background thread
                .subscribeOn(HandlerScheduler.from(handler))
                        // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<UserStockModel>>() {

                    List<UserStockModel> stocks = new ArrayList<>();

                    @Override
                    public void onCompleted() {
                        userStockAdapter.setStocks(stocks);
                        userStockAdapter.notifyDataSetChanged();

                        if(stocks.isEmpty()) {
                            noStocksFound.setVisibility(View.VISIBLE);
                        } else {
                            noStocksFound.setVisibility(View.GONE);
                        }

                        if (currentUserMoney[0] != 0.0f) {
                            UserModel userInfoFromDB = getUserInfoFromDB();
                            ActionBar supportActionBar = ((MainTabbedActivity) context).getSupportActionBar();
                            supportActionBar.setSubtitle("Saved: " + String.valueOf(round(userInfoFromDB.getCash(),2)) +"$" + " & Stocks Value: " + String.valueOf(currentUserMoney[0]) + "$");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, "onError()", e);
                    }

                    @Override
                    public void onNext(List<UserStockModel> stockModels) {
                        stocks.addAll(stockModels);
                        for (UserStockModel stockModel : stockModels) {
                            updateUserStockModelInDB(stockModel);
                            currentUserMoney[0] += stockModel.getCurrentValue() * stockModel.getNumberOfStocks();
                        }
                    }
                });
    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    private static UserModel getUserInfoFromDB() {
        UserModel userModel = new UserModel();
        try {
            DatabaseHelper helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            List<UserModel> userModels = helper.getUserModelDao().queryForAll();
            if (userModels == null || userModels.isEmpty()) {
                Log.d(LOG_TAG, "Ningún usuario");
            } else {
                userModel = userModels.get(0);
                Log.e("USUARIO ", userModel.getUserName());
            }
            OpenHelperManager.releaseHelper();
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error obteniendo usuario");
        }
        return userModel;
    }

}
