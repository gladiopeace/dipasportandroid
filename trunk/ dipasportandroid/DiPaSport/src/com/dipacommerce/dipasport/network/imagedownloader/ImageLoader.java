package com.dipacommerce.dipasport.network.imagedownloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.dipacommerce.dipasport.R;

public class ImageLoader<T extends ImageView> {

    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Map<T, String> imageViews = Collections.synchronizedMap(new WeakHashMap<T, String>());
    ExecutorService executorService;

    public ImageLoader(Context context) {
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
    }

    int stub_id = R.drawable.ic_launcher;

    public void DisplayImage(String url, int loader, T imageView) {
        stub_id = loader;
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            queuePhoto(url, imageView);
            imageView.setImageResource(loader);
        }

    }

    public void DisplayImage(String url, int loader, T imageView, int w, int h) {
        stub_id = loader;
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            queuePhoto(url, imageView, w, h);
            imageView.setImageResource(loader);
        }

    }

    private void queuePhoto(String url, T imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private void queuePhoto(String url, T imageView, int w, int h) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p, w, h));
    }

    private Bitmap getBitmap(String url) {
        File f = fileCache.getFile(url);

        // from SD cache
        Bitmap b = decodeFile(f);
        if (b != null)
            return b;

        // from web
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private Bitmap getBitmap(String url, int w, int h) {
        File f = fileCache.getFile(url);

        // from SD cache
        Bitmap b = decodeFile(f, w, h);
        if (b != null)
            return b;

        // from web
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f, w, h);
            return bitmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    // decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f, int w, int h) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < w || height_tmp / 2 < h)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    // Task for the queue
    private class PhotoToLoad {
        public String url;
        public T imageView;

        public PhotoToLoad(String u, T i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        int w = 0;
        int h = 0;

        public PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        PhotosLoader(PhotoToLoad photoToLoad, int w, int h) {
            this(photoToLoad);
            this.w = w;
            this.h = h;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;

            Bitmap bmp = null;
            if (w > 0 && h > 0) {
                bmp = getBitmap(photoToLoad.url, this.w, this.h);
            } else {
                bmp = getBitmap(photoToLoad.url);
            }
            memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null) {
                Drawable d = new BitmapDrawable(photoToLoad.imageView.getContext().getResources(), bitmap);
                photoToLoad.imageView.setImageDrawable(d);
            } else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}
