package com.extraditables.los.brokin.brokin_old.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.extraditables.los.brokin.R;
import com.extraditables.los.brokin.brokin_old.adapters.listeners.OnStockClickListener;
import com.extraditables.los.brokin.brokin_old.models.StockModel;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private List<StockModel> mDataset;
    private OnStockClickListener onStockClickListener;
    private Context context;

    public StockAdapter(OnStockClickListener onStockClickListener, List<StockModel> stocks, Context context) {
        this.onStockClickListener = onStockClickListener;
        this.mDataset = stocks;
        this.context = context;
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_adapter, parent, false);
        return new StockViewHolder(v, onStockClickListener, context);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        holder.render(mDataset.get(position));

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setStocks(List<StockModel> stockModels) {
        mDataset = stockModels;
    }
}
