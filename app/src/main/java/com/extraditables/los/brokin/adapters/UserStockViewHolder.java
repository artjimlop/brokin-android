package com.extraditables.los.brokin.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.extraditables.los.brokin.views.activity.MainTabbedActivity;
import com.extraditables.los.brokin.R;
import com.extraditables.los.brokin.adapters.listeners.OnUserStockClickListener;
import com.extraditables.los.brokin.db.DatabaseHelper;
import com.extraditables.los.brokin.models.UserModel;
import com.extraditables.los.brokin.models.UserStockModel;
import com.extraditables.los.brokin.views.fragments.UserStockListFragment;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class UserStockViewHolder extends RecyclerView.ViewHolder {

    public static final String USER_USERNAME = "user_username";
    private final OnUserStockClickListener onUserStockClickListener;
    private final String LOG_TAG = getClass().getSimpleName();

    @Bind(R.id.stock_author) TextView author;
    @Bind(R.id.stock_value) TextView value;
    @Bind(R.id.stock_percent_change) TextView percent;
    @Bind(R.id.stock_number_of_stocks) TextView numberOfStocks;
    View notificationIndicator;
    Context context;

    public UserStockViewHolder(View itemView,
                           OnUserStockClickListener onUserStockClickListener, Context context) {
        super(itemView);
        this.onUserStockClickListener = onUserStockClickListener;
        this.context = context;
        ButterKnife.bind(this, itemView);
    }

    public void render(final UserStockModel userStockModel) {
        this.setClickListener(userStockModel);
        Float earns = userStockModel.getCurrentValue()* userStockModel.getNumberOfStocks() - userStockModel.getValue()* userStockModel.getNumberOfStocks();
        BigDecimal earnsTwoDigits;
        BigDecimal currentValueTwiDigits;
        currentValueTwiDigits = round(userStockModel.getCurrentValue(), 2);
        earnsTwoDigits= round(earns,2);

        author.setText(userStockModel.getSymbol());
        value.setText("$" + String.valueOf(currentValueTwiDigits));
        percent.setText("Earned: $" + String.valueOf(earnsTwoDigits));
        numberOfStocks.setText(String.valueOf(userStockModel.getNumberOfStocks())+" shares");
        //TODO Logica de pérdida o ganancia (deberia mostrar current value y la perdida o ganancia)
        if (earns < 0) {
            value.setTextColor(Color.parseColor("#D50000"));
        } else if (earns > 0) {
            value.setTextColor(Color.parseColor("#00C853"));
        } else {
            value.setTextColor(Color.parseColor("#424242"));
        }


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                sellStocksAdapter(v, userStockModel);
            }
        });

    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    private void sellStocksAdapter(final View v, final UserStockModel stockModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("Are you sure you want to sell?")
                .setPositiveButton("Sell", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO acceso a BDD, obtener user

                        UserModel userModel = getUserInfoFromDB();
                        Float cash = stockModel.getCurrentValue() * stockModel.getNumberOfStocks();

                        userModel.setCash(userModel.getCash() + cash);

                        updateCashInfoInToolbar(userModel);

                        removeStockFromDB(stockModel);
                        updateUserInfoInDB(userModel);
                        UserStockListFragment.refreshList(getUserStockModels());
                    }
                })
                .setNegativeButton("Cancel", null).create().show();
    }

    private List<UserStockModel> getUserStockModels() {
        List<UserStockModel> stocks = new ArrayList<>();

        try {
            DatabaseHelper helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            List<UserStockModel> userStockModels = helper.getUserStockModelDao().queryForAll();
            if (userStockModels == null) {
                Log.d(LOG_TAG, "Ningún stock");
            } else {
                stocks = userStockModels;
            }
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error obteniendo stocks del usuairo");
        }
        return stocks;
    }

    private void updateCashInfoInToolbar(UserModel userModel) {
        ((MainTabbedActivity) context).getSupportActionBar().setSubtitle(userModel.getCash().toString() + "$");
    }

    private void removeStockFromDB(UserStockModel userStockModel) {
        DatabaseHelper helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        Dao dao;
        try {
            dao = helper.getUserStockModelDao();
            dao.delete(userStockModel);
            Log.e(LOG_TAG, "Eliminada UserStockModel");
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error eliminando UserStockModel");
        }

        OpenHelperManager.releaseHelper();

    }

    private void updateUserInfoInDB(UserModel userModel) {
        DatabaseHelper helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        Dao dao;
        try {
            dao = helper.getUserModelDao();
            dao.update(userModel);
            Log.e(LOG_TAG, "Actualizado usuario");
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error actualizando usuario");
        }
        OpenHelperManager.releaseHelper();
    }

    private UserModel getUserInfoFromDB() {
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

    private void setClickListener(final UserStockModel userStockModel) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onUserStockClickListener.onUserStockClick(userStockModel);
            }
        });
    }

}
