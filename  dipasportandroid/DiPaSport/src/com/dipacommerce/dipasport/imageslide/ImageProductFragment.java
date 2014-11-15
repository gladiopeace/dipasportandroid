package com.dipacommerce.dipasport.imageslide;

import java.util.LinkedList;
import java.util.Queue;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.network.imagedownloader.ImageLoader;
import com.dipacommerce.dipasport.utils.imagezoom.PhotoView;

public class ImageProductFragment extends Fragment {
    private View mRootView;
    private static ImageLoader<PhotoView> mImageLoader;
    private static final String PROTOCOL = "http://";
    private static Queue<String> mImagePath = new LinkedList<String>();

    public static ImageProductFragment newInstance(String imagePath) {
        mImagePath.add(imagePath);
        ImageProductFragment fm = new ImageProductFragment();

        return fm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_fullscreen_image_product, container, false);

        String product_path = mImagePath.poll();
        PhotoView product_image = (PhotoView) mRootView.findViewById(R.id.imageEnhance);

        if (product_path != null && product_path.length() > 0) {
            mImageLoader = new ImageLoader<PhotoView>(getActivity());
            mImageLoader.DisplayImage(product_path, R.drawable.loader, product_image);
        }

        return mRootView;
    }
}
