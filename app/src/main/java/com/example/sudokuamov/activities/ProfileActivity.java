package com.example.sudokuamov.activities;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.sudokuamov.MenuActivity;
import com.example.sudokuamov.R;
import com.example.sudokuamov.activities.helpers.HelperMethods;
import com.example.sudokuamov.game.Profile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/*
 * References:
 *The android.hardware.camera2 package provides an interface to individual camera devices connected to
 * an Android device. It replaces the deprecated Camera class.
 *
 *
 *  https://developer.android.com/reference/android/hardware/camera2/package-summary
 * https://github.com/eddydn/AndroidCamera2API
 * https://www.youtube.com/watch?v=oPu42I0HSi4
 *
 *
 * */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String DEBUG_TAG = "ProfileActivity";

    public static final int DELAY_AFTER_PICTURE = 500;

    //Check state orientation of output image
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private Button btnCapture;
    private TextureView textureView;
    private Button btnNext;
    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };
    private ImageReader imageReader;

    //Save to FILE
    private File file;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };
    private HandlerThread mBackgroundThread;
    private String userPhotoPath;
    private String userName;
    private String userPhotoThumbNail;
    private EditText editTextNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textureView = findViewById(R.id.textureViewUserImage);
        editTextNick = findViewById(R.id.nickName);
        Intent intent = getIntent();

        boolean userWantsPhoto = intent.getBooleanExtra("userReallyWantsPicture", false);
        if (!userWantsPhoto)
            loadJsonContents();

        assert textureView != null;

        //Its a restored activity
        if (savedInstanceState != null) {
            //Whats in the state
            boolean isTextureAvailable = savedInstanceState.getBoolean("textureViewAvailable");
            if (isTextureAvailable) {
                openCamera();
            } else
                textureView.setSurfaceTextureListener(textureListener);
        } else
            textureView.setSurfaceTextureListener(textureListener);

        btnCapture = findViewById(R.id.btnCapture);
        btnNext = findViewById(R.id.registerBtn);
        btnNext.setOnClickListener(this);
        btnCapture.setOnClickListener(this);

        //The user pressed back or wanted a new photo
        String name = intent.getStringExtra("userName");
        if (name != null)
            editTextNick.setText(intent.getStringExtra("userName"));

    }

    private void loadJsonContents() {
        //Profile profile = new Profile(userName);
        String pathName = getExternalFilesDir(null) + "/" + "userData" + ".json";
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(pathName));

            //Convert json string back to object
            Profile profile = gson.fromJson(bufferedReader, Profile.class);

            if (profile != null) {
                userName = profile.getUsername();
                userPhotoThumbNail = profile.getUserPhotoThumbnailPath();
                userPhotoPath = profile.getUserPhotoPath();

                if (userName != null) {
                    if (!userName.equals("")) {
                        editTextNick.setText(userName);
                    }
                }

                startActivity(HelperMethods.makeIntentForUserNameAndPhoto(
                        new String[]{userName, userPhotoPath, userPhotoThumbNail},
                        this,
                        MenuActivity.class));
                finish();

                /*if (userPhotoThumbNail.equals("")) {
                    Toast.makeText(this, "You don't have a photo yet, take one if you want or just click continue!", Toast.LENGTH_SHORT).show();
                }*/
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "You don't have a profile yet. Take a picture and fill your nickname!", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveJsonContents() {
        String pathName = getExternalFilesDir(null) + "/" + "userData" + ".json";

        if (userPhotoPath == null || userPhotoThumbNail == null) {
            userPhotoPath = "";
            userPhotoThumbNail = "";
        }

        Profile userProfile = new Profile(userName, userPhotoPath, userPhotoThumbNail);

        try (Writer writer = new FileWriter(pathName)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(userProfile, writer);
        } catch (IOException e) {
            Toast.makeText(this, "Error saving user profile", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerBtn: {
                userName = editTextNick.getText().toString();

                if (userName.isEmpty()) {
                    editTextNick.setFocusable(true);
                    editTextNick.requestFocus();
                    editTextNick.setBackgroundColor(getResources().getColor(R.color.mustard, null));
                    //editTextNick.setText("Please fill your nickname");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.showSoftInput(editTextNick, 0);

                } else {
                    saveJsonContents();
                    startActivity(HelperMethods.makeIntentForUserNameAndPhoto(
                            new String[]{userName, userPhotoPath, userPhotoThumbNail},
                            ProfileActivity.this,
                            MenuActivity.class));
                }

                break;
            }
            case R.id.btnCapture: {
                takePicture();
                break;
            }
            default:
                break;
        }

    }

    private void takePicture() {
        if (cameraDevice == null)
            return;
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = null;
            if (manager != null) {
                characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            }
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        .getOutputSizes(ImageFormat.JPEG);
            }

            //Capture image with custom size
            int width = 100;
            int height = 100;
            if (jpegSizes != null && jpegSizes.length > 0) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            final ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurface = new ArrayList<>(2);
            outputSurface.add(reader.getSurface());

            outputSurface.add(new Surface(textureView.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            //Check orientation base on device
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            String photoName = "userPhoto"; //UUID.randomUUID().toString()
            String pathName = getExternalFilesDir(null) + "/" + photoName + ".jpg";
            file = new File(pathName);
            userPhotoPath = pathName;


            //Sets the listener for the reader of the image when the image becomes available to save
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    Image image = null;
                    try {
                        //We get the bytes of the image and save them with an OutputStream inside Save method
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);


                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        {
                            if (image != null)
                                image.close();
                        }
                    }
                }

                private void save(byte[] bytes) throws IOException {
                    OutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(file);
                        outputStream.write(bytes);
                    } finally {
                        if (outputStream != null)
                            outputStream.close();
                    }

                }


            };

            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);


            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    findViewById(R.id.btnCapture).animate().rotationBy(360);

                    mBackgroundHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ProfileActivity.this, getString(R.string.str_photo_saved) + " " + file, Toast.LENGTH_LONG).show();

                            //Creates the camera Preview on the texture surface again
                            createCameraPreview();

                            //Trying to resize the image
                            try {
                                String pathNameThumbnail = getExternalFilesDir(null) + "/userPhoto_thumb.jpg";
                                userPhotoThumbNail = pathNameThumbnail;
                                HelperMethods.ResizeImages(userPhotoPath, pathNameThumbnail);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(ProfileActivity.this, "Error writing thumbnail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, DELAY_AFTER_PICTURE);
                }
            };

            cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try {
                        cameraCaptureSession.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(ProfileActivity.this, "Something has failed on capturing session!", Toast.LENGTH_SHORT).show();
                }
            }, mBackgroundHandler);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if (cameraDevice == null)
                        return;
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(ProfileActivity.this, "Changed", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (cameraDevice == null)
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            assert manager != null;
            if (!manager.getCameraIdList()[1].equals("")) {
                cameraId = manager.getCameraIdList()[1];
            } else
                cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(ImageFormat.JPEG)[0];

            //Check realtime permission if run higher API 23
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, REQUEST_CAMERA_PERMISSION);
                return;
            }


            manager.openCamera(cameraId, stateCallback, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You can't use camera without permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void closeCamera() {
        try {
            if (cameraCaptureSessions != null) {
                cameraCaptureSessions.close();
                cameraCaptureSessions = null;
            }

            if (cameraDevice != null) {
                cameraDevice.close();
                cameraDevice = null;
            }
            if (imageReader != null) {
                imageReader.close();
                imageReader = null;
            }

        } catch (Exception e) {
            throw new RuntimeException("Exception while trying to lock camera closing");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("textureViewAvailable", textureView.isAvailable());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if (textureView.isAvailable())
            openCamera();
        else
            textureView.setSurfaceTextureListener(textureListener);
    }


    @Override
    protected void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            Log.getStackTraceString(e.fillInStackTrace());
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }


}
