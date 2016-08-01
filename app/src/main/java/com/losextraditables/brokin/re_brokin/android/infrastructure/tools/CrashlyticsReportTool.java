package com.losextraditables.brokin.re_brokin.android.infrastructure.tools;

import com.crashlytics.android.Crashlytics;
import javax.inject.Inject;

public class CrashlyticsReportTool implements CrashReportTool {

  @Inject public CrashlyticsReportTool() {
  }

  @Override public void logException(Throwable error) {
    Crashlytics.logException(error);
  }

  @Override public void logException(String message) {
    Crashlytics.log(message);
  }
}
