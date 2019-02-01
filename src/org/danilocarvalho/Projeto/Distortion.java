package org.danilocarvalho.Projeto;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

public class Distortion {
	private static final String TAG = "Projeto::Distortion";
	
	public enum Type {
		DS_ENLARGE,
		DS_SHRINK
	};
	
	public static Mat distort(Mat mRbgaImage, Rect area, Type distType) {
		Log.i(TAG, "Starting distortion on Rect(" + area.toString() + ") of Image(" + mRbgaImage.toString() + ").");
		Mat src = mRbgaImage.submat(area);
		Mat dst = new Mat(new Size(area.height, area.width), mRbgaImage.type());
		double x = 0;
		double y = 0;
		
		switch(distType) {
		case DS_ENLARGE:
			x = 1.5;
			y = 1.5;
			break;
		case DS_SHRINK:
			x = 0.75;
			y = 0.75;
			break;
		}
		
		Log.i(TAG, "Resizing image. Factor: (" + x + "," + y + ").");
		
		Imgproc.resize(src, dst, null, x, y);
		
		Log.i(TAG, "Image resized.");
		
		
		
		return dst;
	}
}
