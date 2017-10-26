package com.example.nbhung.testtakeapicture;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MainActivity extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private static String mPathNew;
    private ImageView imgShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgShow = (ImageView) findViewById(R.id.imgShow);
        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        setOrientationDetective();
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);

        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
                new CountDownTimer(3000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        mCameraPreview.refreshCamera(mCamera);
                    }
                }.start();
            }
        });
    }

    /**
     * Due to performance of using hardware camera,
     * so just use the portrait mode of camera
     * this will detect when device rotate so
     * add the param to camera to image information to rotate image to right orientation
     */
    private void setOrientationDetective() {
        OrientationDetective orientationDetective = new OrientationDetective(this) {
            @Override
            public void onSimpleOrientationChanged(int orientation) {
                if (mCameraPreview != null && mCamera != null) {
                    // this send rotation count to camera surface object
                    mCameraPreview.setOrientationDetective(orientation);
                    // then call to reset param to Camera object
                    mCameraPreview.refreshCamera(mCamera);

                    mCameraPreview.setDefaultOrientation(getDeviceDefaultOrientation());
                    //getDeviceDefaultOrientation();
                }
            }
        };
        orientationDetective.enable();
    }

    public int getDeviceDefaultOrientation() {

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        Configuration config = getResources().getConfiguration();

        int rotation = windowManager.getDefaultDisplay().getRotation();

        if (((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) &&
                config.orientation == Configuration.ORIENTATION_LANDSCAPE)
                || ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) &&
                config.orientation == Configuration.ORIENTATION_PORTRAIT)) {
            Log.e("rotation", "Landscape");
            return Configuration.ORIENTATION_LANDSCAPE;

        } else {
            Log.e("rotation", "Portrait");
            return Configuration.ORIENTATION_PORTRAIT;
        }
    }


    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            Log.e("link", pictureFile.getName());
            if (pictureFile == null) {
                Log.e("TAG", "Error creating media file, check storage permissions: ");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                Bitmap yourSelectedImage = BitmapFactory.decodeFile(mPathNew);
                //   imgShow.setImageBitmap(rotateImageIfRequired(yourSelectedImage,mPathOld));
                imgShow.setImageBitmap(yourSelectedImage);


                //
            } catch (FileNotFoundException e) {
                Log.d("TAG", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("TAG", "Error accessing file: " + e.getMessage());
            }
        }


    };

    private static File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mPathNew = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
            mediaFile = new File(mPathNew);
        } else return null;
        return mediaFile;
    }

    @Override
    protected void onDestroy() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        super.onDestroy();
    }


//    public static Bitmap rotateImageIfRequired(Bitmap bitmap, String path) throws IOException {
//        ExifInterface exif = null;
//        try {
//            exif = new ExifInterface(path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                ExifInterface.ORIENTATION_UNDEFINED);
//        return rotateBitmap(bitmap, orientation);
//    }
//
//    private static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
//
//        Matrix matrix = new Matrix();
//        switch (orientation) {
//            case ExifInterface.ORIENTATION_NORMAL:
//                return bitmap;
//            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
//                matrix.setScale(-1, 1);
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                matrix.setRotate(180);
//                break;
//            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
//                matrix.setRotate(180);
//                matrix.postScale(-1, 1);
//                break;
//            case ExifInterface.ORIENTATION_TRANSPOSE:
//                matrix.setRotate(90);
//                matrix.postScale(-1, 1);
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                matrix.setRotate(90);
//                break;
//            case ExifInterface.ORIENTATION_TRANSVERSE:
//                matrix.setRotate(-90);
//                matrix.postScale(-1, 1);
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                matrix.setRotate(-90);
//                break;
//            default:
//                return bitmap;
//        }
//        try {
//            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//            bitmap.recycle();
//            return bmRotated;
//        } catch (OutOfMemoryError e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
