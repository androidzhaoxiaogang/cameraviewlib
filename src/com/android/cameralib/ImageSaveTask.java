package com.android.cameralib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ImageSaveTask extends Thread {
	private byte[] data;
	private CameraHandler handler;

	public ImageSaveTask(byte[] data, CameraHandler handler) {
		this.data = data;
		this.handler = handler;
	}

	@Override
	public void run() {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inSampleSize = 2;

		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix mtx = new Matrix();
		
		if(handler.isFrontCamera()) {
			mtx.postRotate(270);
		} else {
			mtx.postRotate(90);
		}
		
		Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);

		handler.saveImage(rotatedBMP);
	}
}
