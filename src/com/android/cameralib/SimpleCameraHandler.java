package com.android.cameralib;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;

/**
 * The Class SimpleCameraHandler.
 */
@SuppressLint("NewApi")
public class SimpleCameraHandler implements CameraHandler {

	/** The is front camera. */
	private boolean isFrontCamera = true;

	/** The context. */
	private Context context = null;

	/**
	 * Instantiates a new simple camera handler.
	 * 
	 * @param context
	 *            the context
	 */
	public SimpleCameraHandler(Context context) {
		this.context = context.getApplicationContext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.mycamera.CameraHandler#getCameraId()
	 */
	@Override
	public int getCameraId() {
		int count = Camera.getNumberOfCameras();
		int result = -1;

		if (count > 0) {
			result = 0; // if we have a camera, default to this one

			Camera.CameraInfo info = new Camera.CameraInfo();

			for (int i = 0; i < count; i++) {
				Camera.getCameraInfo(i, info);

				if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK
						&& !isFrontCamera()) {
					result = i;
					break;
				} else if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT
						&& isFrontCamera()) {
					result = i;
					break;
				}
			}
		}

		return (result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.example.mycamera.CameraHandler#saveImage(android.graphics.Bitmap)
	 */
	@Override
	public void saveImage(Bitmap bitmap) {
		File photo = getPhotoPath();
	
        if (!photo.exists()){
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(photo);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (FileNotFoundException e) {
                return;
            } finally {
                if(out != null){
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.mycamera.CameraHandler#saveImage(byte[])
	 */
	@Override
	public void saveImage(byte[] image) {
		File photo = getPhotoPath();

		if (photo.exists()) {
			photo.delete();
		}

		try {
			FileOutputStream fos = new FileOutputStream(photo);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			bos.write(image);
			bos.flush();
			fos.getFD().sync();
			bos.close();

		} catch (java.io.IOException e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.mycamera.CameraHandler#useFrontFacingCamera()
	 */
	@Override
	public boolean isFrontCamera() {
		return isFrontCamera;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.mycamera.CameraHandler#autoFocusAvailable()
	 */
	@Override
	public void autoFocusAvailable() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.mycamera.CameraHandler#autoFocusUnavailable()
	 */
	@Override
	public void autoFocusUnavailable() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.mycamera.CameraHandler#getShutterCallback()
	 */
	@Override
	public Camera.ShutterCallback getShutterCallback() {
		return (null);
	}

	/**
	 * Sets the front camera to be available.
	 * 
	 * @param isFrontCamera
	 *            the new front camera
	 */
	public void setFrontCamera(boolean isFrontCamera) {
		this.isFrontCamera = isFrontCamera;
	}

	private File getPhotoPath() {
		File dir = getPhotoDirectory();
		if (!dir.exists()) {
			dir.mkdirs();
		}

		return (new File(dir, getPhotoFilename()));
	}

	private File getPhotoDirectory() {
		return new File(context.getCacheDir(), "image");
	}

	private String getPhotoFilename() {
		return ("validate_image" + ".jpg");
	}

}
