package com.android.cameralib;

import android.graphics.Bitmap;
import android.hardware.Camera;

/**
 * The Interface CameraHandler.
 */
public interface CameraHandler {

	/**
	 * Return if front facing camera is available or not.
	 * 
	 * @return true, if available
	 */
	boolean isFrontCamera();

	/**
	 * Gets the camera id.
	 * 
	 * @return the ID of the camera that you want to use for previews and
	 *         picture/video taking with the associated CameraView instance
	 */
	int getCameraId();

	/**
	 * Called when a picture has been taken. This will be called on a background
	 * thread.
	 * 
	 * @param bitmap
	 *            Bitmap of the picture
	 * 
	 */
	void saveImage(Bitmap bitmap);

	/**
	 * Called when a picture has been taken. This will be called on a background
	 * thread.
	 * 
	 * @param image
	 *            byte array of the picture data (e.g., JPEG)
	 */
	void saveImage(byte[] image);
	
	/**
	 * Sets the front camera.
	 *
	 * @param isFrontCameraAvailable the new front camera
	 */
	void setFrontCamera(boolean isFrontCameraAvailable);

	/**
	 * This will be called by the library to let you know that auto-focus is
	 * available for your use, so you can update your UI accordingly.
	 */
	void autoFocusAvailable();

	/**
	 * This will be called by the library to let you know that auto-focus is not
	 * available for your use, so you can update your UI accordingly.
	 */
	void autoFocusUnavailable();

	/**
	 * @return the Camera.ShutterCallback to be used with the camera, for sound
	 *         effects and such
	 */
	Camera.ShutterCallback getShutterCallback();
}
