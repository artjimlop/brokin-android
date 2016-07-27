package com.extraditables.los.brokin.brokin_old.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.extraditables.los.brokin.R;
import com.extraditables.los.brokin.brokin_old.adapters.StockAdapter;
import com.extraditables.los.brokin.brokin_old.adapters.listeners.OnStockClickListener;
import com.extraditables.los.brokin.brokin_old.models.StockModel;
import com.extraditables.los.brokin.re_brokin.android.infrastructure.shares.ShareSymbols;
import com.extraditables.los.brokin.re_brokin.android.view.activities.ShareInfoActivity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.HandlerScheduler;
import rx.functions.Func0;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class StocksListFragment extends Fragment {

    private static final String TAG = "RxAndroidSamples";

    private RecyclerView mRecyclerView;
    private static StockAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Handler backgroundHandler;
    private Handler syncroHandler;
    private static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stocks_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.stock_list);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());

        context = getActivity();

        sampleObservable()
                // Run on a background thread
                .subscribeOn(HandlerScheduler.from(backgroundHandler))
                        // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<StockModel>>() {

                    List<StockModel> stocks = new ArrayList<>();

                    @Override
                    public void onCompleted() {

                        mAdapter = new StockAdapter(new OnStockClickListener() {
                            @Override
                            public void onStockClick(StockModel event) {
                              Intent intent = new Intent(getActivity(), ShareInfoActivity.class);
                              startActivity(intent);
                            }
                        }, stocks, getActivity());
                        mRecyclerView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError()", e);
                    }

                    @Override
                    public void onNext(List<StockModel> stockModels) {
                        stocks.addAll(stockModels);
                    }
                });

        BackgroundThread syncroThread = new BackgroundThread();
        syncroThread.start();
        syncroHandler = new Handler(backgroundThread.getLooper());

    }

    static Observable<List<StockModel>> sampleObservable() {
        return Observable.defer(new Func0<Observable<List<StockModel>>>() {
            @Override public Observable<List<StockModel>> call() {
                final List<StockModel> stockModels = new ArrayList<>();
                try {
                  String[] symbols = ShareSymbols.SYMBOLS;
                  Map<String, Stock> stocks = YahooFinance.get(symbols);
                  for (Stock stock : stocks.values()) {
                        StockModel stockModel = new StockModel();
                        stockModel.setName(stock.getName());
                        stockModel.setSymbol(stock.getSymbol());
                        stockModel.setChange(stock.getQuote().getChangeInPercent().floatValue());
                        stockModel.setChangePercentage(stock.getQuote().getChangeInPercent().floatValue());
                        stockModel.setValue(stock.getQuote().getPrice().floatValue());
                        stockModels.add(stockModel);
                    }
                    Collections.sort(stockModels, StockModel.alphabeticallyComparator);
                } catch (IOException e) {
                    Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
                }

                return Observable.just(stockModels);
            }
        });
    }

    public static void refreshStocks(){
        BackgroundThread syncroThread = new BackgroundThread();
        syncroThread.start();
        Handler handler = new Handler(syncroThread.getLooper());
        sampleObservable()
                // Run on a background thread
                .subscribeOn(HandlerScheduler.from(handler))
                        // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<StockModel>>() {

                    List<StockModel> stocks = new ArrayList<>();

                    @Override
                    public void onCompleted() {
                        mAdapter.setStocks(stocks);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError()", e);
                    }

                    @Override
                    public void onNext(List<StockModel> stockModels) {
                        stocks.addAll(stockModels);
                    }
                });
    }

    static class BackgroundThread extends HandlerThread {
        BackgroundThread() {
            super("SchedulerSample-BackgroundThread", THREAD_PRIORITY_BACKGROUND);
        }
    }

}
