package com.dipacommerce.dipasport.utils.camera;

import android.graphics.Bitmap;

public class ImageObject {
    private Bitmap bitmap;
    private String filename;
    private String path;

    public ImageObject() {
    }

    public ImageObject(Bitmap bitmap, String filename, String path) {
        this.bitmap = bitmap;
        this.filename = filename;
        this.path = path;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
