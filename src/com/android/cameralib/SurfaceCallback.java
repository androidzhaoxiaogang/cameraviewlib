package com.android.cameralib;

import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import java.io.IOException;

/**
 * The Class SurfaceCallback set the SurfaceView for the camera. 
 * The camera initialization jobs and  
 */
class SurfaceCallback implements PreviewHandler, SurfaceHolder.Callback {
	
	/** The camera view. */
	private final CameraView cameraView;
	
	/** The preview. */
	private SurfaceView preview = null;
	
	/** The preview holder. */
	private SurfaceHolder previewHolder = null;

	/**
	 * Instantiates a new surface callback.
	 *
	 * @param cameraView the camera view
	 */
	@SuppressWarnings("deprecation")
	SurfaceCallback(CameraView cameraView) {
		this.cameraView = cameraView;
		preview = new SurfaceView(cameraView.getContext());
		previewHolder = preview.getHolder();
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		previewHolder.addCallback(this);
	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		cameraView.previewCreated();
	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		cameraView.initPreview(width, height);
	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		cameraView.previewDestroyed();
	}

	/* (non-Javadoc)
	 * @see com.example.mycamera.PreviewHandler#display(android.hardware.Camera)
	 */
	@Override
	public void display(Camera camera) throws IOException {
		camera.setPreviewDisplay(previewHolder);
	}

	/* (non-Javadoc)
	 * @see com.example.mycamera.PreviewHandler#getPreview()
	 */
	@Override
	public View getPreview() {
		return (preview);
	}
}