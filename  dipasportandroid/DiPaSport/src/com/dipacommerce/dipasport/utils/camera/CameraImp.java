package com.dipacommerce.dipasport.utils.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.dipacommerce.dipasport.data.Constants;

class CameraImp extends CameraManager {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final String FILE_NAME_PATTERN = "yyyyMMddHHmmss";

    private static File mDestFile;
    // private static String mFileName = "";
    private Bitmap mImageProduct;

    private CaptureEventChanged mEvents;

    /**
     * 
     *
     */
    public class FILE_TYPE {
        public static final String BMP = ".bmp";
        public static final String GIF = ".gif";
        public static final String JPEG = ".jpg";
        public static final String PNG = ".png";
    }

    public CameraImp() {

    }

    /**
     * 
     */
    public void openCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(((Activity) sCtx).getPackageManager()) != null) {
            // Create the File where the photo should go
            mDestFile = null;
            try {
                mDestFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (mDestFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mDestFile));
                ((Activity) sCtx).startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    /**
     * 
     * @param requestCode
     * @param resultCode
     * @param imageReturndImage
     */
    @Override
    public void processDataImage(int requestCode, int resultCode, Intent imageReturnedImage) {
        // decode image from camera here
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                mEvents.OnCaptureFail("Canceled");
            } else {
                Bitmap bmp = scaleBitmap(mDestFile, Constants.IMAGE_WIDTH, Constants.IMAGE_HEIGHT);
                mImageProduct = bmp;
                // write to file
                FileOutputStream os;
                try {
                    os = new FileOutputStream(mDestFile);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 85, os);
                    if (os != null) {
                        os.flush();
                        os.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    if (mEvents != null) {
                        mEvents.OnCaptureFail("FileNotFoundException: " + e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (mEvents != null) {
                        mEvents.OnCaptureFail("IOException: " + e.getMessage());
                    }
                }

                if (mEvents != null) {
                    mEvents.OnCaptureSucess();
                    mEvents.OnCaptureSuccess(bmp);
                    mEvents.OnCaptureSeccess(new ImageObject(bmp, mDestFile.getName(), mDestFile.getPath()));
                }
            }
        }
    }

    /**
     * 
     * @return
     */
    @Override
    public Bitmap getImageProduct() {
        return mImageProduct;
    }

    @Override
    public File getImageFile() {

        return mDestFile;
    }

    @Override
    public void clearImageProduct() {
        if (mImageProduct != null) {
            mImageProduct.recycle();
        }
    }

    @Override
    public String getFileName() {
        return mDestFile.getName();
    }

    @Override
    public void registerEventChanged(CaptureEventChanged event) {
        mEvents = event;
    }

    @Override
    public void setAutoCallback(boolean value) {
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
                );
        return image;
    }

    private Bitmap scaleBitmap(File file, int width, int height) {
        // Get the dimensions of the bitmap
        Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
        int photoW = bmp.getWidth();
        int photoH = bmp.getHeight();
        boolean isPortraitDirection = photoW < photoH ? true : false;
        if (isPortraitDirection) {
            photoW = bmp.getHeight();
            photoH = bmp.getWidth();
        }
        float scaleWidth = (float) width / photoW;
        float ratio = (float) photoW / width;
        int newHeight = (int) (photoH / ratio);
        float scaleHeight = (float) newHeight / photoH;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap scaleBitmap = null;
        if(isPortraitDirection){
            scaleBitmap = Bitmap.createBitmap(bmp, 0, 0, photoH, photoW, matrix, true);   
        }else{
            scaleBitmap = Bitmap.createBitmap(bmp, 0, 0, photoW, photoH, matrix, true);
        }
        return scaleBitmap;
    }
}
