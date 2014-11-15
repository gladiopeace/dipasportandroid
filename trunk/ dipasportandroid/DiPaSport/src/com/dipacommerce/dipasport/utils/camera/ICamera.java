package com.dipacommerce.dipasport.utils.camera;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;

import com.dipacommerce.dipasport.DiPaSport;

public interface ICamera {
    public abstract class CaptureEventChanged {
        public void OnCaptureSucess() {
        }

        public void OnCaptureSuccess(final Bitmap bmp) {
        }

        public void OnCaptureSeccess(final ImageObject bmpObj) {
        }

        public void OnCaptureFail(final String errorMessage) {
        }
    }
    
    // Support to pass parameters when send mail
    public interface BUNDLE{
        public static final String FILE_NAME = "file_name";
        public static final String FILE_PATH = "file_path";
    }

    /**
     * Return the capture via methods {@link DiPaSport#OnCaptureSucess()},
     * {@link DiPaSport#OnCaptureSuccess(Bitmap)} and
     * {@link DiPaSport#OnCaptureFail(String)}
     */
    public void openCamera();

    public void processDataImage(int requestCode, int resultCode, Intent imageReturnedImage);

    public Bitmap getImageProduct();

    public File getImageFile();

    public String getFileName();

    public void clearImageProduct();

    public void registerEventChanged(CaptureEventChanged event);

    public void setAutoCallback(boolean value);
}
