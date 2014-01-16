/*
 * 
 */
package com.android.cameralib;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * The Class CameraView.
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public class CameraView extends ViewGroup implements Camera.PictureCallback {
	/** The in preview. */
	private boolean inPreview = false;

	/** The camera id. */
	private int cameraId = -1;

	/** The camera. */
	private Camera camera = null;

	/** The preview handler. */
	private PreviewHandler previewHandler;

	/** The camera handler. */
	private CameraHandler cameraHandler;

	/** The context. */
	private Context context;

	/** The clazz. */
	private Class<?> clazz;

	/**
	 * The Enum FlashMode.
	 */
	public enum FlashMode {
		
		/** The on. */
		ON, 
		/** The off. */
		OFF, 
		/** The auto. */
		AUTO
	}

	/** The image bytes. */
	private static byte[] imageBytes = null;

	/**
	 * Instantiates a new camera view.
	 * 
	 * @param context
	 *            the context
	 */
	public CameraView(Context context) {
		super(context);
		this.previewHandler = new SurfaceCallback(this);
		this.context = context;
	}

	/**
	 * Instantiates a new camera view.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public CameraView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * Instantiates a new camera view.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 * @param defStyle
	 *            the def style
	 */
	public CameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/** The was in preview. */
	boolean wasInPreview = inPreview;

	/**
	 * On resume.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void onResume() {

		addView(previewHandler.getPreview());

		if (camera == null) {
			cameraId = cameraHandler.getCameraId();
			if (cameraId >= 0) {
				try {
					camera = Camera.open(cameraId);

					Camera.Parameters params = camera.getParameters();
					Camera.Size size = getLargestPictureSize(params);
					String focusMode = null;
					focusMode = findSettableValue(
							params.getSupportedFocusModes(),
							Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
							Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
							Camera.Parameters.FOCUS_MODE_AUTO);
					if (focusMode == null) {
						focusMode = findSettableValue(
								params.getSupportedFocusModes(),
								Camera.Parameters.FOCUS_MODE_MACRO,
								Camera.Parameters.FOCUS_MODE_EDOF);
					}
					if (focusMode != null) {
						params.setFocusMode(focusMode);
					} else {
						params.setFocusMode(Parameters.FOCUS_MODE_AUTO);
					}
					
					params.setPictureSize(size.width, size.height);
					setDisplayOrientation();
					camera.setParameters(params);
					startPreview();
				} catch (Exception e) {
					//
				}
			} else {
				//
			}
		}
	}

	/**
	 * On pause.
	 */
	public void onPause() {
		if (camera != null) {
			previewDestroyed();
			removeView(previewHandler.getPreview());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = resolveSize(getSuggestedMinimumWidth(),
				widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		setMeasuredDimension(width, height);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int)
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed && getChildCount() > 0) {
			final View child = getChildAt(0);
			final int width = r - l;
			final int height = b - t;

			child.layout(0, 0, width, height);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.hardware.Camera.PictureCallback#onPictureTaken(byte[],
	 * android.hardware.Camera)
	 */
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		if (data != null) {
			new ImageSaveTask(data, cameraHandler).start();

			imageBytes = data;

			if (clazz != null) {
				Intent intent = new Intent(context, clazz);
				context.startActivity(intent);
			} else {
				startPreview();
			}
		}
	}

	/**
	 * Restart preview.
	 */
	public void restartPreview() {
		if (!inPreview) {
			startPreview();
		}
	}

	/**
	 * Cancel auto focus.
	 */
	public void cancelAutoFocus() {
		camera.cancelAutoFocus();
	}

	/**
	 * Checks if is auto focus available.
	 * 
	 * @return true, if is auto focus available
	 */
	public boolean isAutoFocusAvailable() {
		return (inPreview);
	}

	/**
	 * Gets the flash mode.
	 * 
	 * @return the flash mode
	 */
	public String getFlashMode() {
		return (camera.getParameters().getFlashMode());
	}

	/**
	 * Sets the flash mode.
	 * 
	 * @param mode
	 *            the new flash mode
	 */
	public void setFlashMode(FlashMode mode) {
		if (camera == null) {
			return;
		}

		final Parameters p = camera.getParameters();
		List<String> flashModes = p.getSupportedFlashModes();
		
		if (flashModes == null || flashModes.isEmpty()) {
			return;
		}

		switch (mode) {
		case ON:
			setFoundFlashMode(p, flashModes, Parameters.FLASH_MODE_ON);
			break;
		case OFF:
			setFoundFlashMode(p, flashModes, Parameters.FLASH_MODE_OFF);
			break;
		case AUTO:
			setFoundFlashMode(p, flashModes, Parameters.FLASH_MODE_AUTO);
			break;
		}
	}
	
	/**
	 * Sets the found flash mode.
	 *
	 * @param p the Camera Parameters
	 * @param flashModes the flash modes
	 * @param mode the mode
	 */
	private void setFoundFlashMode(Parameters p, List<String> flashModes, String mode) {
		if (isSupported(mode, flashModes)) {
			p.setFlashMode(mode);
			camera.setParameters(p);
			camera.startPreview();
		}
	}
	
	/**
	 * Checks if is supported.
	 *
	 * @param value the value
	 * @param supported the supported
	 * @return true, if is supported
	 */
	private static boolean isSupported(String value, List<String> supported) {
        return supported == null ? false : supported.indexOf(value) >= 0;
    }

	/**
	 * Preview created.
	 */
	void previewCreated() {
		if (camera != null) {
			try {
				previewHandler.display(camera);
			} catch (IOException e) {

			}
		}
	}

	/**
	 * Preview destroyed.
	 */
	void previewDestroyed() {
		if (camera != null) {
			previewStopped();
			camera.release();
			camera = null;
		}
	}

	/**
	 * Preview reset.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 */
	void previewReset(int width, int height) {
		previewStopped();
		initPreview(width, height);
	}

	/**
	 * Preview stopped.
	 */
	private void previewStopped() {
		if (inPreview) {
			stopPreview();
		}
	}

	/**
	 * Inits the preview.
	 * 
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void initPreview(int w, int h) {
		if (camera != null) {
			Camera.Parameters parameters = camera.getParameters();

			requestLayout();

			camera.setParameters(parameters);
			startPreview();
		}
	}

	/**
	 * Start the camera preview.
	 */
	private void startPreview() {
		camera.startPreview();
		inPreview = true;
		cameraHandler.autoFocusAvailable();
	}

	/**
	 * Stop the camera preview.
	 */
	private void stopPreview() {
		inPreview = false;
		cameraHandler.autoFocusUnavailable();
		camera.stopPreview();
	}

	/**
	 * Sets the handler.
	 *
	 * @param cameraHandler the new handler
	 */
	public void setHandler(CameraHandler cameraHandler) {
		this.cameraHandler = cameraHandler;
	}

	/**
	 * Take picture.
	 *
	 * @param clazz the clazz
	 * @param authType the auth type
	 */
	public void takePicture(Class<?> clazz) {
		if (inPreview) {
			this.clazz = clazz;

			Camera.Parameters pictureParams = camera.getParameters();
			pictureParams.setPictureFormat(ImageFormat.JPEG);
			camera.setParameters(pictureParams);

			camera.takePicture(cameraHandler.getShutterCallback(), null, this);
			inPreview = false;
		}
	}

	/**
	 * Gets the image bytes.
	 *
	 * @return the image bytes
	 */
	public static byte[] getImageBytes() {
		return imageBytes;
	}

	/**
	 * Sets the image bytes.
	 *
	 * @param bytes the new image bytes
	 */
	public static void setImageBytes(byte[] bytes) {
		imageBytes = bytes;
	}

	/**
	 * Gets the largest picture size.
	 *
	 * @param parameters the parameters
	 * @return the largest picture size
	 */
	private Camera.Size getLargestPictureSize(Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPictureSizes()) {

			if (result == null) {
				result = size;
			} else {
				int resultArea = result.width * result.height;
				int newArea = size.width * size.height;

				if (newArea > resultArea) {
					result = size;
				}

			}
		}

		return (result);
	}
	
	/**
	 * Find settable value.
	 *
	 * @param supportedValues the supported values
	 * @param desiredValues the desired values
	 * @return the string
	 */
	private String findSettableValue(Collection<String> supportedValues,
			String... desiredValues) {
		String result = null;
		if (supportedValues != null) {
			for (String desiredValue : desiredValues) {
				if (supportedValues.contains(desiredValue)) {
					result = desiredValue;
					break;
				}
			}
		}
		return result;
	}

    /**
     * Sets the display orientation.
     */
    private void setDisplayOrientation() {
    	int orientation = 90;
    	if(!TextUtils.isEmpty(Build.MODEL) && Build.MODEL.equals("M9")) {
    		orientation = 180;
    	}
    	
    	camera.setDisplayOrientation(orientation);
    }
   
}