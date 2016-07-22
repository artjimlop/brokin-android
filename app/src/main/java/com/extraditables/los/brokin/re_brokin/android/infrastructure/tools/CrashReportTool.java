package com.extraditables.los.brokin.re_brokin.android.infrastructure.tools;

public interface CrashReportTool {

  void logException(Throwable error);

  void logException(String message);

}
