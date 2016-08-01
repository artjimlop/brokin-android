package com.losextraditables.brokin.brokin_old.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.losextraditables.brokin.R;
import com.losextraditables.brokin.brokin_old.adapters.listeners.OnUserStockClickListener;
import com.losextraditables.brokin.brokin_old.models.UserStockModel;
import com.losextraditables.brokin.re_brokin.android.view.activities.ShareInfoActivity;
import java.math.BigDecimal;

public class UserStockViewHolder extends RecyclerView.ViewHolder {

    private final OnUserStockClickListener onUserStockClickListener;

    @Bind(R.id.stock_author) TextView author;
    @Bind(R.id.stock_value) TextView value;
    @Bind(R.id.stock_percent_change) TextView percent;
    @Bind(R.id.stock_number_of_stocks) TextView numberOfStocks;
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
            value.setTextColor(context.getResources().getColor(R.color.red));
        } else if (earns > 0) {
            value.setTextColor(context.getResources().getColor(R.color.primary));
        } else {
            value.setTextColor(context.getResources().getColor(R.color.gray));
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent callingIntent =
                    ShareInfoActivity.getCallingIntent(context, userStockModel.getSymbol(), userStockModel.getUserStockId(), true);
                context.startActivity(callingIntent);
            }
        });

    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    private void setClickListener(final UserStockModel userStockModel) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onUserStockClickListener.onUserStockClick(userStockModel);
            }
        });
    }

}
