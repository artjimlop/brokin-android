package com.losextraditables.brokin.re_brokin.android.view.adapters;

import com.robinhood.spark.SparkAdapter;

public class GraphicAdapter extends SparkAdapter {

  private float[] data;

  public GraphicAdapter(float[] data) {
    this.data = data;
  }

  @Override
  public int getCount() {
    return data.length;
  }

  @Override
  public Object getItem(int index) {
    return data[index];
  }

  @Override
  public float getY(int index) {
    return data[index];
  }
}
