package droidninja.filepicker.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.PickerManager;

public class ImageCaptureManager {

    private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";
    public static final int REQUEST_CAPTURE_MEDIA = 0x101;

    private String mCurrentMediaFilePath;
    private Context mContext;

    public ImageCaptureManager(Context mContext) {
        this.mContext = mContext;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
//    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + System.currentTimeMillis() + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (!storageDir.exists()) {
            if (!storageDir.mkdir()) {
                Log.e("TAG", "Throwing Errors....");
                throw new IOException();
            }
        }

        File image = new File(storageDir, imageFileName);
        //                File.createTempFile(
        //                imageFileName,  /* prefix */
        //                ".jpg",         /* suffix */
        //                storageDir      /* directory */
        //        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentMediaFilePath = image.getAbsolutePath();
        return image;
    }


    private File createVideoFile() throws IOException {
        // Create an image file name
//    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String videoFileName = "MP4_" + System.currentTimeMillis() + ".mp4";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

        if (!storageDir.exists()) {
            if (!storageDir.mkdir()) {
                Log.e("TAG", "Throwing Errors....");
                throw new IOException();
            }
        }

        File video = new File(storageDir, videoFileName);
        //                File.createTempFile(
        //                imageFileName,  /* prefix */
        //                ".jpg",         /* suffix */
        //                storageDir      /* directory */
        //        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentMediaFilePath = video.getAbsolutePath();
        return video;
    }


    public Intent dispatchTakePictureIntent(Context context, int fileType) throws IOException {
        Intent cameraIntent = new Intent();

        if (fileType == FilePickerConst.MEDIA_TYPE_IMAGE)
            cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        else if (fileType == FilePickerConst.MEDIA_TYPE_VIDEO)
            cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        //Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null) {

            // Create the File where the photo should go
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                File file = new File("");
                if (fileType == FilePickerConst.MEDIA_TYPE_IMAGE)
                    file = createImageFile();
                else if (fileType == FilePickerConst.MEDIA_TYPE_VIDEO)
                    file = createVideoFile();

                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                Uri mediaFileURI = FileProvider.getUriForFile(context, PickerManager.getInstance().getProviderAuthorities(), file);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mediaFileURI);

            } else {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createImageFile()));
            }
            return cameraIntent;
        }
        return null;
    }


    public String notifyMediaStoreDatabase() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        if (TextUtils.isEmpty(mCurrentMediaFilePath)) {
            return null;
        }

        File f = new File(mCurrentMediaFilePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mContext.sendBroadcast(mediaScanIntent);

        return mCurrentMediaFilePath;
    }


    public String getCurrentMediaFilePathPath() {
        return mCurrentMediaFilePath;
    }


    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && mCurrentMediaFilePath != null) {
            savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, mCurrentMediaFilePath);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
            mCurrentMediaFilePath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
        }
    }

}
