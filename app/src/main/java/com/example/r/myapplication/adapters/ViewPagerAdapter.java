package com.example.r.myapplication.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter extends PagerAdapter {
    private View[] pages;
    private float itemScale = 1.0f;

    public ViewPagerAdapter(View[] pages, float itemScale) {
        this.pages = pages;
        this.itemScale = itemScale;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        View view = pages[position];
        collection.addView(view);

        return view;
    }

    @Override
    public float getPageWidth(int position) {
        return itemScale;
    }

    public void setPageWidth(float scale) {
        itemScale = scale;
        notifyDataSetChanged();
    }

    public View[] getPages() {
        return pages;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return pages.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}