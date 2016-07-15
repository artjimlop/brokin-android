package com.extraditables.los.brokin.brokin_old.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.extraditables.los.brokin.R;
import com.extraditables.los.brokin.brokin_old.adapters.listeners.OnUserStockClickListener;
import com.extraditables.los.brokin.brokin_old.models.UserStockModel;

import java.util.List;

public class UserStockAdapter extends RecyclerView.Adapter<UserStockViewHolder> {

    private List<UserStockModel> mDataset;
    private OnUserStockClickListener onUserStockClickListener;
    private Context context;

    public UserStockAdapter(OnUserStockClickListener onUserStockClickListener, List<UserStockModel> stocks, Context context) {
        this.onUserStockClickListener = onUserStockClickListener;
        this.mDataset = stocks;
        this.context = context;
    }

    @Override
    public UserStockViewHolder onCreateViewHolder(ViewGroup parent,
                                              int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_sell_adapter, parent, false);
        return new UserStockViewHolder(v, onUserStockClickListener, context);
    }

    @Override
    public void onBindViewHolder(UserStockViewHolder holder, int position) {
        holder.render(mDataset.get(position));

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setStocks(List<UserStockModel> userStockModels) {
        mDataset = userStockModels;
    }
}
