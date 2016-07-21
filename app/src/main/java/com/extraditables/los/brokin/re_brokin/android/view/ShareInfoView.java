package com.extraditables.los.brokin.re_brokin.android.view;

import java.math.BigDecimal;

public interface ShareInfoView {

  void showGraphic(float[] data);

  void showSellButton();

  void showBuyButton();

  void setQuote(BigDecimal quote);

  void setChange(BigDecimal change, BigDecimal chageInPercent);
}
