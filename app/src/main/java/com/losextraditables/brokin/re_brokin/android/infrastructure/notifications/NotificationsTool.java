package com.losextraditables.brokin.re_brokin.android.infrastructure.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.losextraditables.brokin.R;
import com.losextraditables.brokin.brokin_old.db.DatabaseHelper;
import com.losextraditables.brokin.brokin_old.models.UserStockModel;
import com.losextraditables.brokin.brokin_old.views.activity.MainTabbedActivity;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class NotificationsTool implements NotificationsManager {

  private final Context context;

  @Inject public NotificationsTool(Context context) {
    this.context = context;
  }

  @Override public void handleNotificationsNYSE(final String service) {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    scheduler.scheduleAtFixedRate(new Runnable() {
      public void run() {
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
          Intent intent = new Intent(context, MainTabbedActivity.class);
          PendingIntent pIntent =
              PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
          Notification n = null;

          try {
            n = new Notification.Builder(context).setContentTitle("Brokin")
                .setContentText("Current earns: " + String.valueOf(getUserEarns(context)) + "$")
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .getNotification();
          } catch (IOException e) {
            //TODO LOG
          }

          NotificationManager notificationManager =
              (NotificationManager) context.getSystemService(service);

          notificationManager.notify(0, n);
        }
      }
    }, 0, 1, TimeUnit.MINUTES);
  }

  private BigDecimal getUserEarns(Context context) throws IOException {
    Float earns = 0.0f;
    Float spent = 0.0f;
    try {
      DatabaseHelper helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
      List<UserStockModel> userStockModels = helper.getUserStockModelDao().queryForAll();
      if (userStockModels != null) {
        for (UserStockModel userStockModel : userStockModels) {
          Stock stock = YahooFinance.get(userStockModel.getSymbol());
          earns += stock.getQuote().getPrice().floatValue() * userStockModel.getNumberOfStocks();
          spent += userStockModel.getValue() * userStockModel.getNumberOfStocks();
        }
      }
    } catch (SQLException e) {
      //TODO LOG ERROR
    }
    return round(earns - spent, 2);
  }

  public static BigDecimal round(float d, int decimalPlace) {
    BigDecimal bd = new BigDecimal(Float.toString(d));
    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
    return bd;
  }
}
