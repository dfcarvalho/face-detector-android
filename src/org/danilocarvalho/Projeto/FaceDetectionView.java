package org.danilocarvalho.Projeto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.danilocarvalho.Projeto.Distortion.Type;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

class FaceDetectionView extends CameraViewBase {
	private static final String TAG = "Proejto::FaceDetectionView";
	private Mat mRgba;
	private Mat mGray;
	private Mat mGrayFace;
	private Mat mGrayFaceTopHalf;
	private Mat mGrayFaceMidThird;
	private Mat mGrayFaceBotThird;

	private CascadeClassifier mCascadeFace;
	private CascadeClassifier mCascadeEyes;
	private CascadeClassifier mCascadeNose;

	private Bitmap bmpGlasses;
	
	private Canvas canvas;
	
	public FaceDetectionView(Context context) {
		super(context);
		
		try {
			InputStream is = context.getResources().openRawResource(R.raw.lbpcascade_frontalface);
			File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
			File cascadeFileFace = new File(cascadeDir, "lbpcascade_frontalface.xml");
			FileOutputStream os = new FileOutputStream(cascadeFileFace);

			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			is.close();
			os.close();

			mCascadeFace = new CascadeClassifier(cascadeFileFace.getAbsolutePath());
			if (mCascadeFace.empty()) {
				Log.e(TAG, "Failed to load cascade classifier");
				mCascadeFace = null;
			} else
				Log.i(TAG,
						"Loaded cascade classifier from "
								+ cascadeFileFace.getAbsolutePath());

			cascadeFileFace.delete();
			
			// EYES
			is = context.getResources().openRawResource(R.raw.haarcascade_eye);
			File cascadeFileEyes = new File(cascadeDir, "haarcascade_eye.xml");
			os = new FileOutputStream(cascadeFileEyes);

			byte[] bufferEyes = new byte[4096];
			int bytesReadEyes;
			while ((bytesReadEyes = is.read(bufferEyes)) != -1) {
				os.write(bufferEyes, 0, bytesReadEyes);
			}
			is.close();
			os.close();

			mCascadeEyes = new CascadeClassifier(cascadeFileEyes.getAbsolutePath());
			if (mCascadeEyes.empty()) {
				Log.e(TAG, "Failed to load cascade classifier");
				mCascadeEyes = null;
			} else {
				Log.i(TAG, "Loaded cascade classifier from " + cascadeFileEyes.getAbsolutePath());
			}

			cascadeFileEyes.delete();
			
			// NOSE
			is = context.getResources().openRawResource(R.raw.haarcascade_mcs_nose);
			File cascadeFileNose = new File(cascadeDir, "haarcascade_mcs_nose.xml");
			os = new FileOutputStream(cascadeFileNose);

			byte[] bufferNose = new byte[4096];
			int bytesReadNose;
			while ((bytesReadNose = is.read(bufferNose)) != -1) {
				os.write(bufferNose, 0, bytesReadNose);
			}
			is.close();
			os.close();

			mCascadeNose = new CascadeClassifier(cascadeFileNose.getAbsolutePath());
			if (mCascadeNose.empty()) {
				Log.e(TAG, "Failed to load cascade classifier");
				mCascadeNose = null;
			} else {
				Log.i(TAG, "Loaded cascade classifier from " + cascadeFileNose.getAbsolutePath());
			}

			cascadeFileNose.delete();
			
			cascadeDir.delete();

		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
		}
		
		bmpGlasses = BitmapFactory.decodeResource(context.getResources(), R.drawable.glasses);
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder _holder, int format, int width,
			int height) {
		super.surfaceChanged(_holder, format, width, height);

		synchronized (this) {
			// initialize Mats before usage
			mGray = new Mat();
			mRgba = new Mat();
		}
	}

	@Override
	protected Bitmap processFrame(VideoCapture capture) {
		capture.retrieve(mRgba, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
		capture.retrieve(mGray, Highgui.CV_CAP_ANDROID_GREY_FRAME);

		if (mCascadeFace != null && mCascadeEyes != null && mCascadeNose != null) {
			int height = mGray.rows();
			int faceSize = Math.round(height * ProjetoActivity.minFaceSize);
			List<Rect> faces = new LinkedList<Rect>();
			mCascadeFace.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(faceSize, faceSize));

			for (Rect r : faces) {
				Core.rectangle(mRgba, r.tl(), r.br(), new Scalar(0, 255, 0, 255), 3);
				
				List<Rect> eyes = new LinkedList<Rect>();
				double offsetX = 0;
				double offsetY = 0;
				mGrayFace = mGray.submat(r);
				int row2 = mGrayFace.rows()/3;
				int row3 = 2 * mGrayFace.rows()/3;
				mGrayFaceTopHalf = mGrayFace.submat(0, mGrayFace.rows()/2, 0, mGrayFace.cols());
				mGrayFaceMidThird = mGrayFace.submat(row2, row3, 0, mGrayFace.cols());
				mGrayFaceBotThird = mGrayFace.submat(row3, mGrayFace.rows(), 0, mGrayFace.cols());
				offsetX = r.tl().x;
				offsetY = r.tl().y;
				mCascadeEyes.detectMultiScale(mGrayFaceTopHalf, eyes, 1.1, 3, 2, new Size(30, 20));
				
				for (Rect e : eyes) {
					Core.rectangle(mRgba, new Point(e.tl().x + offsetX, e.tl().y + offsetY),
						new Point(e.br().x + offsetX, e.br().y + offsetY),
						new Scalar(255, 0, 0, 255), 3);
				}
				
				List<Rect> nose = new LinkedList<Rect>();
				double offsetYNose = offsetY + row2;
				
				mCascadeNose.detectMultiScale(mGrayFaceMidThird, nose, 1.1, 2, 2, new Size(30, 30));
				
				for (Rect n : nose) {
					Core.rectangle(mRgba, new Point(n.tl().x + offsetX, n.tl().y + offsetYNose),
						new Point(n.br().x + offsetX, n.br().y + offsetYNose),
						new Scalar(0, 0, 255, 255), 3);
				}
			}
		}
		
		Bitmap bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
		
		if (!Utils.matToBitmap(mRgba, bmp)) {
			return null;
		}
		
		return bmp;
	}

	@Override
	public void run() {
		super.run();

		synchronized (this) {
			// Explicitly deallocate Mats
			if (mRgba != null)
				mRgba.release();
			if (mGray != null)
				mGray.release();

			mRgba = null;
			mGray = null;
		}
	}
}
