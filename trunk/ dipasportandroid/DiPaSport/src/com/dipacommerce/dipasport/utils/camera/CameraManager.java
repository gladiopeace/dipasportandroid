package com.dipacommerce.dipasport.utils.camera;

import com.dipacommerce.dipasport.DiPaSport;

public abstract class CameraManager extends DiPaSport<Object, Object> implements ICamera {
    private static CameraManager mCamera;

    public static CameraManager getInstance() {
        if (mCamera == null) {
            mCamera = new CameraImp();
        }
        return mCamera;
    }
}
