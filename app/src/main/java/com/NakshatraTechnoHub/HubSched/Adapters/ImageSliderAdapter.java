package com.NakshatraTechnoHub.HubSched.Adapters;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.NakshatraTechnoHub.HubSched.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.List;

public class ImageSliderAdapter extends PagerAdapter {
    private List<Bitmap> bitmapList;
    Context context;


    public ImageSliderAdapter(List<Bitmap> bitmapList, Context context) {
        this.bitmapList = bitmapList;
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.cl_image_slider, container, false);


        ImageView imageView = view.findViewById(R.id.sliderView);

        Glide.with(context)
                .asBitmap()
                .centerCrop()
                .load(bitmapList.get(position))
                .into(new BitmapImageViewTarget(imageView));//this set image from bitmap

        container.addView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("BANNERWAALA", "onResponse: "+bitmapList.get(0));

            }
        });

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return bitmapList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
