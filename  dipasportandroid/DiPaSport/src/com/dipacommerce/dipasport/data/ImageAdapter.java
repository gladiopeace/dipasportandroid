package com.dipacommerce.dipasport.data;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.dipacommerce.dipasport.utils.camera.ImageObject;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ImageObject> mImages;

    public ImageAdapter(Context context, ArrayList<ImageObject> images) {
        mContext = context;
        mImages = images;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public Object getItem(int position) {
        return mImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SquareImageView imageView;
        if (convertView == null) {
            imageView = new SquareImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (SquareImageView) convertView;
        }

        imageView.setImageBitmap(mImages.get(position).getBitmap());
        return imageView;
    }

}
