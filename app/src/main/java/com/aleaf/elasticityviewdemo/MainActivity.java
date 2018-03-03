package com.aleaf.elasticityviewdemo;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {


    @BindViews({R.id.vertical_linear_layout, R.id.horizontal_linear_layout, R.id.recycler_view, R.id.scroll_view})
    List<View> mElasticityViews;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUnbinder = ButterKnife.bind(this);
        ButterKnife.apply(mElasticityViews, SET_VISIBLER, 0);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MyAdapter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.view_0:
                ButterKnife.apply(mElasticityViews, SET_VISIBLER, 0);
                break;
            case R.id.view_1:
                ButterKnife.apply(mElasticityViews, SET_VISIBLER, 1);
                break;
            case R.id.view_2:
                ButterKnife.apply(mElasticityViews, SET_VISIBLER, 2);
                break;
            case R.id.view_3:
                ButterKnife.apply(mElasticityViews, SET_VISIBLER, 3);
                break;

        }
        return true;
    }

    static final ButterKnife.Setter<View, Integer> SET_VISIBLER = new ButterKnife.Setter<View, Integer>() {
        @Override public void set(View view, Integer value, int index) {
            view.setVisibility(value == index ? View.VISIBLE : View.GONE);
        }
    };

    class MyAdapter extends RecyclerView.Adapter<MyHolder>{

        int[] colors;
        public MyAdapter(){
            colors =new int[] {0xff37474f, 0xff21272b, 0xff009688, 0x66ffffff,
                  0xff424242, 0xff303030, 0xff212121,0xff9e9e9e,0xffff9800,0xfffb7299,0xffffea39,0xff616161,0xff424242 };
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_color, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            holder.itemView.setBackgroundColor(colors[position]);
        }

        @Override
        public int getItemCount() {
            return colors.length;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder{


        public MyHolder(View itemView) {
            super(itemView);
        }
    }
}
