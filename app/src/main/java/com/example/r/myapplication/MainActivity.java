package com.example.r.myapplication;

import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.r.myapplication.adapters.ReceiverAdapter;
import com.example.r.myapplication.adapters.ViewPagerAdapter;
import com.example.r.myapplication.views.DisableableViewPager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RelativeLayout layout;
    DisableableViewPager viewPager;

    TextView draggingView;
    String dragging;
    boolean isDragging;

    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        layout = (RelativeLayout) findViewById(R.id.layout);

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        ListView[] listViews = new ListView[] {
            new ListView(this),
            new ListView(this),
            new ListView(this)
        };

        viewPager = (DisableableViewPager) findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(listViews, 1.0f / 3);
        viewPager.setAdapter(viewPagerAdapter);

        for (int index = 0; index < listViews.length; ++index) {
            final ListView listView = listViews[index];

            ArrayList<String> elements = new ArrayList<>();

            for (int jdex = 0; jdex < 100; ++jdex) {
                elements.add(Integer.toString((index * 100 + jdex)));
            }

            ReceiverAdapter adapter = new ReceiverAdapter(elements);

            listView.setAdapter(adapter);

            listView.setOnTouchListener(listViewOnTouchListener);
            listView.setOnItemLongClickListener(listViewItemLongClickListener);
        }

        draggingView = (TextView) findViewById(R.id.dragging_view);

    }

    private View.OnTouchListener listViewOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ViewPager pager = (ViewPager) v.getParent();
            ViewPagerAdapter pagerAdapter = (ViewPagerAdapter) pager.getAdapter();
            ListView hoveredOverListView = null;

            // Convert the position from relative to the list view, to relative to
            // the view pager
            float posX = event.getX() + v.getX();
            float posY = event.getY() + v.getY();

            View[] views = pagerAdapter.getPages();

            // Iterate over all views in the view pager, to figure out which list view
            // is being hovered over by the user
            for (View view : views) {
                ListView listView = (ListView) view;

                Rect rect = new Rect(
                        listView.getLeft(),
                        listView.getTop(),
                        listView.getRight(),
                        listView.getBottom());

                if (rect.contains((int) posX, (int) posY)) {
                    hoveredOverListView = listView;
                }

                // If we are dragging, we also want to scroll if the user have the dragged
                // item at certain positions
                if (isDragging) {
                    Rect topPart =
                        new Rect(
                            listView.getLeft(),
                            listView.getTop(),
                            listView.getRight(),
                            listView.getTop() + listView.getHeight() / 10);

                    Rect buttomPart =
                        new Rect(
                            listView.getLeft(),
                            listView.getBottom() - listView.getHeight() / 10,
                            listView.getRight(),
                            listView.getBottom());

                    if (topPart.contains((int) posX, (int) posY)) {
                        listView.smoothScrollToPosition(0);
                    } else if (buttomPart.contains((int) posX, (int) posY)) {
                        listView.smoothScrollToPosition(listView.getMaxScrollAmount());
                    } else {
                        // We stop the scroll, if the user does not have the dragged item at the
                        // top or bottom part of the list view
                        listView.smoothScrollBy(0, 0);
                    }
                }
            }


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE: {
                    // Move the dragged view to the place where the user touched
                    RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) draggingView.getLayoutParams();
                    layout.leftMargin = (int) posX - draggingView.getWidth() / 2;
                    layout.topMargin = (int) posY - draggingView.getHeight() / 2;
                    draggingView.setLayoutParams(layout);

                    // We only want to eat the touch token, if we are actually dragging
                    return isDragging;
                }
                case MotionEvent.ACTION_UP: {

                    if (isDragging) {
                        // If we didn't find a view the user is hovering over, then we need to
                        // handle that, as we then don't have any list view to add the item too
                        if (hoveredOverListView == null) {
                            throw new IndexOutOfBoundsException("");
                        }

                        isDragging = false;
                        draggingView.setVisibility(View.GONE);
                        viewPager.setSwipeable(true);

                        ReceiverAdapter adapter = (ReceiverAdapter) hoveredOverListView.getAdapter();

                        // We need to keep track of a offset, as the index for the views on screen
                        // do not correspond with the adapters index'
                        int offset = hoveredOverListView.getFirstVisiblePosition();

                        // Find the place to insert the dragged item
                        for(int i = 0; i < hoveredOverListView.getChildCount(); i++){
                            View child = hoveredOverListView.getChildAt(i);

                            // Check if the position is in the top half of an item. If it is
                            // then we need to place the dragged item above it
                            Rect childRect = new Rect(
                                    hoveredOverListView.getLeft(),
                                    child.getTop(),
                                    hoveredOverListView.getRight(),
                                    (child.getBottom() - child.getHeight() / 2));

                            if(childRect.contains((int) posX, (int) posY)){
                                adapter.addItem(dragging, i + offset);
                                return true;
                            }

                            // Check if the position is in the bottom half of an item. If it is
                            // then we need to place the dragged item below it
                            childRect.set(
                                    hoveredOverListView.getLeft(),
                                    child.getTop() + child.getHeight() / 2,
                                    hoveredOverListView.getRight(),
                                    child.getBottom());

                            if(childRect.contains((int) posX, (int) posY)){
                                adapter.addItem(dragging, i + offset + 1);
                                return true;
                            }
                        }

                        // If we couldn't find a place to insert the dragged item, we just insert
                        // it at the end
                        adapter.addItem(dragging);
                        return true;
                    }
                }
            }

            return false;
        }
    };

    private ListView.OnItemLongClickListener listViewItemLongClickListener = new ListView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            // Remove the item to drags data out of the adapter
            ReceiverAdapter adapter = (ReceiverAdapter) parent.getAdapter();
            dragging = (String) parent.getItemAtPosition(position);
            adapter.removeItem(position);

            isDragging = true;
            draggingView.setText(dragging);
            draggingView.setVisibility(View.VISIBLE);

            // Disable our view pagers swipe, as it cause weird bugs under dragging
            viewPager.setSwipeable(false);
            return false;
        }
    };
}
