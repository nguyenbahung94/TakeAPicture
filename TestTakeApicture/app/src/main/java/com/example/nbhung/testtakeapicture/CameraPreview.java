package com.example.nbhung.testtakeapicture;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by nbhung on 10/23/2017.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String CAMERA_PARAM_ORIENTATION = "orientation";
    private static final String CAMERA_PARAM_LANDSCAPE = "landscape";
    private static final String CAMERA_PARAM_PORTRAIT = "portrait";
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Activity context;
    private int mOrientationDetective;
    private int defaultOrientation;

    public int getOrientationDetective() {
        return mOrientationDetective;
    }

    public void setOrientationDetective(int mOrientationDetective) {
        this.mOrientationDetective = mOrientationDetective;
    }

    public int getDefaultOrientation() {
        return defaultOrientation;
    }

    public void setDefaultOrientation(int defaultOrientation) {
        this.defaultOrientation = defaultOrientation;
    }

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.context = (Activity) context;
        this.mCamera = camera;
        this.mSurfaceHolder = this.getHolder();
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            refreshCamera(mCamera);
            mCamera.startPreview();
        } catch (IOException e) {
            // left blank for now
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            // intentionally left blank for a test
        }
    }

    private int mCameraId;
    public void refreshCamera(Camera camera) {

        if (mSurfaceHolder.getSurface() == null) {

            // preview surface does not exist

            return;

        }

        // stop preview before making changes

        try {

            mCamera.stopPreview();

        } catch (Exception e) {

            // ignore: tried to stop a non-existent preview

        }

        // set preview size and make any resize, rotate or

        // reformatting changes here

        // start preview with new settings

        setCamera(camera);

        try {


            Camera.Parameters cameraParams = mCamera.getParameters();
            /*boolean portrait = isPortrait();
            configureCameraParameters(cameraParams, portrait);*/

            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraId, cameraInfo);
            if (getDefaultOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
                //Default lanscape: SamSung Tab: 0 but must set display orientation = 90
                mCamera.setDisplayOrientation((cameraInfo.orientation + 90) % 360);
            } else {
                //Default portrait: SamSung Phone: 90, Nexus 5: 270
                mCamera.setDisplayOrientation(cameraInfo.orientation);
            }

            cameraParams.setRotation(getImageRotation(cameraInfo));
            mCamera.setParameters(cameraParams);
            ///
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();

        } catch (Exception e) {

            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());

        }

    }

    private int getImageRotation(Camera.CameraInfo cameraInfo) {
        // set Image rotate angle when camera auto rotate
        int imageRotate;

        /*LogUtil.e(TAG, "getImageRotationFrontCamera", getOrientationDetective() + " " + cameraInfo.orientation);*/
        switch (getOrientationDetective()) {
            case 0:
                imageRotate = cameraInfo.orientation;
                break;
            case 1:
                imageRotate = (cameraInfo.orientation + 90) % 360;
                break;
            case 2:
                imageRotate = (cameraInfo.orientation + 180) % 360;
                break;
            default:
                imageRotate = (cameraInfo.orientation + 270) % 360;
                break;
        }
        if (mCameraId != Camera.CameraInfo.CAMERA_FACING_BACK) {
            imageRotate -= 360;
        }
        if (imageRotate < 0) {
            imageRotate *= -1;
        }
        return imageRotate;
    }

   /* protected void configureCameraParameters(Camera.Parameters cameraParams, boolean portrait) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) { // for 2.1 and before
            if (portrait) {
                cameraParams.set(CAMERA_PARAM_ORIENTATION, CAMERA_PARAM_PORTRAIT);
            } else {
                cameraParams.set(CAMERA_PARAM_ORIENTATION, CAMERA_PARAM_LANDSCAPE);
            }
        } else { // for 2.2 and later
            int angle;
            Display display = context.getWindowManager().getDefaultDisplay();
            switch (display.getRotation()) {
                case Surface.ROTATION_0: // This is display orientation
                    angle = 90; // This is camera orientation
                    break;
                case Surface.ROTATION_90:
                    angle = 0;
                    break;
                case Surface.ROTATION_180:
                    angle = 270;
                    break;
                case Surface.ROTATION_270:
                    angle = 180;
                    break;
                default:
                    angle = 90;
                    break;
            }
            Log.v("LOG_TAG", "angle: " + angle);
            mCamera.setDisplayOrientation(angle);
        }
    }
    */

    public void setCamera(Camera camera) {

        //method to set a camera instance
        mCamera = camera;

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
    }
}
