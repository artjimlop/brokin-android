package com.extraditables.los.brokin.brokin_old.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.extraditables.los.brokin.R;
import com.extraditables.los.brokin.brokin_old.adapters.listeners.OnStockClickListener;
import com.extraditables.los.brokin.brokin_old.db.DatabaseHelper;
import com.extraditables.los.brokin.brokin_old.models.StockModel;
import com.extraditables.los.brokin.brokin_old.models.UserModel;
import com.extraditables.los.brokin.brokin_old.views.activity.MainTabbedActivity;
import com.extraditables.los.brokin.re_brokin.android.view.activities.ShareInfoActivity;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import java.math.BigDecimal;
import java.sql.SQLException;

public class StockViewHolder extends RecyclerView.ViewHolder {

    public static final String USER_USERNAME = "user_username";
    private final OnStockClickListener onStockClickListener;
    private final String LOG_TAG = getClass().getSimpleName();

    @Bind(R.id.stock_author) TextView author;
    @Bind(R.id.stock_number_of_stocks) TextView name;
    @Bind(R.id.stock_value) TextView value;
    @Bind(R.id.stock_percent_change) TextView percent;
    Context context;

    public StockViewHolder(View itemView,
                           OnStockClickListener onStockClickListener, Context context) {
        super(itemView);
        this.onStockClickListener = onStockClickListener;
        this.context = context;
        ButterKnife.bind(this, itemView);
    }

    public void render(final StockModel stockModel) {
        this.setClickListener(stockModel);
        author.setText(stockModel.getSymbol());
        name.setText(stockModel.getName());
        BigDecimal valueTwoDecimals = round(stockModel.getValue(),2);

        value.setText("$" + String.valueOf(valueTwoDecimals));
        percent.setText("Change: " + String.valueOf(stockModel.getChangePercentage()+"%"));

        if (stockModel.getChangePercentage() < 0) {
            value.setTextColor(Color.parseColor("#D50000"));
        } else if (stockModel.getChangePercentage() > 0) {
            value.setTextColor(Color.parseColor("#00C853"));
        } else {
            value.setTextColor(Color.parseColor("#424242"));
        }


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                if (prefs.getString(USER_USERNAME, null) == null) {
                    createUserAdapter(v);
                } else {
                    Intent callingIntent =
                        ShareInfoActivity.getCallingIntent(context, stockModel.getSymbol(), false);
                    context.startActivity(callingIntent);
                }
            }
        });

    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    private void createUserAdapter(final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        final EditText username = new EditText(v.getContext());

        builder.setView(username);
        builder.setMessage("You haven't created a user yet. Please insert your name to continue:")
                .setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String userNameString = username.getText().toString();
                        String usernameWithoutSpaces = userNameString.replaceAll("\\s+", "");
                        if(usernameWithoutSpaces.length() <2) {
                            Toast.makeText(context, "Username must have two characters minimum", Toast.LENGTH_SHORT).show();
                        } else {
                            UserModel userModel = new UserModel();
                            createUserInfo(userModel, username);
                            updateUserInfoInToolbar(userModel);
                            insertUserInfoInDB(userModel);
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor prefEditor = prefs.edit();
                            prefEditor.putString(USER_USERNAME, userModel.getUserName());
                            prefEditor.apply();
                        }
                    }
                })
                .setNegativeButton("Cancel", null).create().show();
    }

    private void insertUserInfoInDB(UserModel userModel) {
        DatabaseHelper helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        Dao dao;
        try {
            dao = helper.getUserModelDao();
            dao.create(userModel);
            Log.e(LOG_TAG, "Creado usuario");
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error creando usuario");
        }
        OpenHelperManager.releaseHelper();
    }

    private void createUserInfo(UserModel userModel, EditText username) {
        if(username.getText().toString().isEmpty()) {
            userModel.setUserName("user"+String.valueOf(Math.random()));
        } else {
            userModel.setUserName(username.getText().toString());
        }
        userModel.setCash(10000.0F);
    }

    private void updateUserInfoInToolbar(UserModel userModel) {
        ((MainTabbedActivity) context).getSupportActionBar().setTitle(userModel.getUserName());
        ((MainTabbedActivity) context).getSupportActionBar().setSubtitle(userModel.getCash().toString() + "$");
    }

    private void setClickListener(final StockModel stockModel) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onStockClickListener.onStockClick(stockModel);
            }
        });
    }
}
