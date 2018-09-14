package mx.com.atmen.photolearn;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends Activity implements Camera.PictureCallback, SurfaceHolder.Callback {

    private static final String KEY_IS_CAPTURING = "is_capturing";
    String mCurrentPhotoPath = null;
    private Camera mCamera;
    private FrameLayout mCameraImage;
    private SurfaceView mCameraPreview;
    private FloatingActionButton mCaptureImageButton;
    private byte[] mCameraData;
    private boolean mIsCapturing;
    FloatingActionButton doneButton;
    FloatingActionButton saveButton;
    Bitmap bitmap;
    classDB db = new classDB(this);
    int currentZoomLevel = 0;
    Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_camera);
        mCameraImage = (FrameLayout) findViewById(R.id.camera_image_view);
        mCameraImage.setVisibility(View.INVISIBLE);
        mCameraPreview = (SurfaceView) findViewById(R.id.preview_view);
        final SurfaceHolder surfaceHolder = mCameraPreview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCaptureImageButton = (FloatingActionButton) findViewById(R.id.capture_image_button);
        mCaptureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
        doneButton = (FloatingActionButton) findViewById(R.id.done_button);
        doneButton.setVisibility(View.INVISIBLE);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupImageCapture();
            }
        });
        saveButton = (FloatingActionButton) findViewById(R.id.save_button);
        saveButton.setVisibility(View.INVISIBLE);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    final int cat = getIntent().getIntExtra("c", 0);
                    final int vt = getIntent().getIntExtra("t", 0);
                    try {
                        save(cat, vt);
                    } catch (Exception e) {
                        Toast.makeText(CameraActivity.this, "Can't save image.", Toast.LENGTH_LONG).show();
                    }
                }
                Intent intent = new Intent(CameraActivity.this, Photo.class);
                intent.putExtra("Cat", getIntent().getIntExtra("c", 0));
                intent.putExtra("Value", getIntent().getIntExtra("t", 0));
                startActivity(intent);
                finish();
            }
        });
        mIsCapturing = true;
        picasso = new Picasso.Builder(this).memoryCache(new LruCache(999999999)).build();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(KEY_IS_CAPTURING, mIsCapturing);
        savedInstanceState.putByteArray("mCameraData", mCameraData);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mIsCapturing = savedInstanceState.getBoolean(KEY_IS_CAPTURING);
        mCameraData = savedInstanceState.getByteArray("mCameraData");
        if (mCameraData != null) {
            setupImageDisplay();
        } else {
            setupImageCapture();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(mCameraPreview.getHolder());
                Camera.Parameters params = mCamera.getParameters();
                List<Camera.Size> supportedSizes = params.getSupportedPictureSizes();
                Camera.Size sizePicture = supportedSizes.get(0);
                params.setPictureSize(sizePicture.width, sizePicture.height);
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                mCamera.setParameters(params);
                if (mIsCapturing) {
                    mCamera.startPreview();
                }
            } catch (Exception e) {
                Toast.makeText(CameraActivity.this, "Unable to open camera", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        mCameraData = data;
        setupImageDisplay();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
                if (mIsCapturing) {
                    mCamera.startPreview();
                }
            } catch (Exception e) {
                Toast.makeText(CameraActivity.this, "Unable to start camera preview", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    private void captureImage() {
        mCamera.takePicture(null, null, this);
    }


    private void setupImageCapture() {
        mCameraImage.setVisibility(View.INVISIBLE);
        mCameraPreview.setVisibility(View.VISIBLE);
        doneButton.setVisibility(View.INVISIBLE);
        mCaptureImageButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mCamera.setPreviewDisplay(mCameraPreview.getHolder());
                Camera.Parameters params = mCamera.getParameters();
                List<Camera.Size> supportedSizes = params.getSupportedPictureSizes();
                Camera.Size sizePicture = supportedSizes.get(0);
                params.setPictureSize(sizePicture.width, sizePicture.height);
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                mCamera.setParameters(params);
                if (mIsCapturing) {
                    mCamera.startPreview();
                }
            } catch (Exception e) {
                Toast.makeText(CameraActivity.this, "Unable to open camera", Toast.LENGTH_LONG).show();
            }
        } else {
            mCamera.startPreview();
        }
        mCaptureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
    }

    private void setupImageDisplay() {
        if (bitmap != null) {
            bitmap.recycle();
        }
        bitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);
        mCameraImage.setRotation(90);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        mCameraImage.setBackground(bitmapDrawable);
        if (mCamera != null) {
            mCamera.stopPreview();
        }
        mCameraPreview.setVisibility(View.INVISIBLE);
        mCameraImage.setVisibility(View.VISIBLE);
        doneButton.setVisibility(View.VISIBLE);
        mCaptureImageButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.VISIBLE);
    }

    private void save(int Category, int Type) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh_mm_ss", Locale.getDefault());
        String imageFileName = "JPEG_" + dateFormat + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".png", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        FileOutputStream fileOutputStream = new FileOutputStream(image);
        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)) {
            Toast.makeText(CameraActivity.this, "Unable to save image", Toast.LENGTH_LONG).show();
        }
        MediaScannerConnection.scanFile(getBaseContext(), new String[]{image.toString()}, null, null);
        db.newPhoto(mCurrentPhotoPath, db.getType(Category)[Type], db.getCategory()[Category]);
    }
    float mDist = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the pointer ID
        Camera.Parameters params = mCamera.getParameters();
        int action = event.getAction();


        if (event.getPointerCount() > 1) {
            // handle multi-touch events
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mDist = getFingerSpacing(event);
            } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                mCamera.cancelAutoFocus();
                handleZoom(event, params);
            }
        } else {
            // handle single touch events
            if (action == MotionEvent.ACTION_UP) {
                handleFocus(event, params);
            }
        }
        return true;
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        mCamera.setParameters(params);
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    // currently set to auto-focus on single touch
                }
            });
        }
    }

    /** Determine the space between the first two fingers */
    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

}
