package com.example.r.myapplication.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ReceiverAdapter extends BaseAdapter {
    private List<String> fItems;

    public ReceiverAdapter(List<String> items) {
        fItems = items;
    }

    @Override
    public int getCount() {
        return fItems.size();
    }

    @Override
    public Object getItem(int position) {
        return fItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) convertView;

        if (view == null) {
            view = new TextView(parent.getContext());
        }

        view.setText(fItems.get(position));

        return view;
    }

    public void addItem(String item) {
        fItems.add(item);
        notifyDataSetChanged();
    }

    public void addItem(String item, int index) {
        fItems.add(index, item);
        notifyDataSetChanged();
    }

    public void removeItem(int index) {
        fItems.remove(index);
        notifyDataSetChanged();
    }

}
