package org.openalbum.mixare.reality;

import java.util.Iterator;
import java.util.List;

import org.openalbum.mixare.Compatibility;
import org.openalbum.mixare.MixView;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author daniele
 * 
 */
public class CameraSurface extends SurfaceView implements
		SurfaceHolder.Callback {
	MixView app; // ?
	SurfaceHolder holder;
	Camera camera;

	public CameraSurface(final Context context) {
		super(context);

		try {
			app = (MixView) context;

			holder = getHolder();
			holder.addCallback(this);
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		} catch (final Exception ex) {
			Log.e(VIEW_LOG_TAG, ex.getMessage());
		}
	}

	public void surfaceCreated(final SurfaceHolder holder) {
		try {
			// release camera if it's in use
			if (camera != null) {
				try {
					camera.stopPreview();
				} catch (final Exception ignore) {
					Log.i(VIEW_LOG_TAG, ignore.getMessage());
				}
				try {
					camera.release();
				} catch (final Exception ignore) {
					Log.i(VIEW_LOG_TAG, ignore.getMessage());
				}
				// camera = null;
			}

			camera = Camera.open();
			camera.setPreviewDisplay(holder);
		} catch (final Exception ex) {
			Log.w(VIEW_LOG_TAG, ex.getMessage());
			try {
				if (camera != null) {
					try {
						camera.stopPreview();
					} catch (final Exception ignore) {
						Log.e(VIEW_LOG_TAG, ignore.getMessage());
					}
					try {
						camera.release();
					} catch (final Exception ignore) {
						Log.e(VIEW_LOG_TAG, ignore.getMessage());
					}
					camera = null;
				}
			} catch (final Exception ignore) {
				Log.i(VIEW_LOG_TAG, ignore.getMessage());
			}
		}
	}

	public void surfaceDestroyed(final SurfaceHolder holder) {
		try {
			if (camera != null) {
				try {
					camera.stopPreview();
				} catch (final Exception ignore) {
					Log.i(VIEW_LOG_TAG, ignore.getMessage());
				}
				try {
					camera.release();
				} catch (final Exception ignore) {
					Log.i(VIEW_LOG_TAG, ignore.getMessage());
				}
				camera = null;
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public void surfaceChanged(final SurfaceHolder holder, final int format,
			final int w, final int h) {
		try {
			final Camera.Parameters parameters = camera.getParameters();
			try {
				List<Camera.Size> supportedSizes = null;
				// On older devices (<1.6) the following will fail
				// the camera will work nevertheless
				supportedSizes = Compatibility
						.getSupportedPreviewSizes(parameters);

				// preview form factor
				final float ff = (float) w / h;
				Log.d("OpenAlbum - Mixare", "Screen res: w:" + w + " h:" + h
						+ " aspect ratio:" + ff);

				// holder for the best form factor and size
				float bff = 0;
				int bestw = 0;
				int besth = 0;
				final Iterator<Camera.Size> itr = supportedSizes.iterator();

				// we look for the best preview size, it has to be the closest
				// to the
				// screen form factor, and be less wide than the screen itself
				// the latter requirement is because the HTC Hero with update
				// 2.1 will
				// report camera preview sizes larger than the screen, and it
				// will fail
				// to initialize the camera
				// other devices could work with previews larger than the screen
				// though
				while (itr.hasNext()) {
					final Camera.Size element = itr.next();
					// current form factor
					final float cff = (float) element.width / element.height;
					// check if the current element is a candidate to replace
					// the best match so far
					// current form factor should be closer to the bff
					// preview width should be less than screen width
					// preview width should be more than current bestw
					// this combination will ensure that the highest resolution
					// will win
					Log.d("Mixare", "Candidate camera element: w:"
							+ element.width + " h:" + element.height
							+ " aspect ratio:" + cff);
					if ((ff - cff <= ff - bff) && (element.width <= w)
							&& (element.width >= bestw)) {
						bff = cff;
						bestw = element.width;
						besth = element.height;
					}
				}
				Log.d("Mixare", "Chosen camera element: w:" + bestw + " h:"
						+ besth + " aspect ratio:" + bff);
				// Some Samsung phones will end up with bestw and besth = 0
				// because their minimum preview size is bigger then the screen
				// size.
				// In this case, we use the default values: 480x320
				if ((bestw == 0) || (besth == 0)) {
					Log.d("Mixare", "Using default camera parameters!");
					bestw = 480;
					besth = 320;
				}
				parameters.setPreviewSize(bestw, besth);
			} catch (final Exception ex) {
				parameters.setPreviewSize(480, 320);
			}

			camera.setParameters(parameters);
			camera.startPreview();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}
}