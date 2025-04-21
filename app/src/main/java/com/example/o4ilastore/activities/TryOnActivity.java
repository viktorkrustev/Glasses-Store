package com.example.o4ilastore.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.o4ilastore.R;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TryOnActivity extends AppCompatActivity {

    private static final String TAG = "TryOnActivity";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private ExecutorService cameraExecutor;
    private FaceDetector detector;
    private PreviewView previewView;
    private ImageView glassesOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_on);

        previewView = findViewById(R.id.previewView);
        glassesOverlay = findViewById(R.id.glassesOverlay);

        initFaceDetector();

        cameraExecutor = Executors.newSingleThreadExecutor();

        int productId = getIntent().getIntExtra("product_id", -1);
        Log.d("TryOnActivity", "Received product_id: " + productId);


        setGlassesImageByProductId(productId);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startCamera();
        }
    }

    private void setGlassesImageByProductId(int productId) {
        switch (productId) {
            case 1:
                glassesOverlay.setImageResource(R.drawable.glasses1removebg);
                break;
            case 2:
                glassesOverlay.setImageResource(R.drawable.glasses2);
                break;
            case 3:
                glassesOverlay.setImageResource(R.drawable.glasses3);
                break;
            case 4:
                glassesOverlay.setImageResource(R.drawable.glasses4);
                break;
            case 5:
                glassesOverlay.setImageResource(R.drawable.glasses5);
                break;
            case 6:
                glassesOverlay.setImageResource(R.drawable.glasses6);
                break;
            case 7:
                glassesOverlay.setImageResource(R.drawable.glasses7);
                break;
            case 8:
                glassesOverlay.setImageResource(R.drawable.glasses8);
                break;
            case 9:
                glassesOverlay.setImageResource(R.drawable.glasses9);
                break;
            case 10:
                glassesOverlay.setImageResource(R.drawable.glasses10);
                break;

        }
    }


    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::detectFace);

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void initFaceDetector() {
        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

        detector = FaceDetection.getClient(options);
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void detectFace(@NonNull ImageProxy imageProxy) {
        if (imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

        detector.process(image)
                .addOnSuccessListener(faces -> {
                    if (!faces.isEmpty()) {
                        Face face = faces.get(0);

                        FaceLandmark leftEye = face.getLandmark(FaceLandmark.LEFT_EYE);
                        FaceLandmark rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE);

                        if (leftEye != null && rightEye != null) {
                            float leftX = leftEye.getPosition().x;
                            float leftY = leftEye.getPosition().y;
                            float rightX = rightEye.getPosition().x;
                            float rightY = rightEye.getPosition().y;

                            float centerX = (leftX + rightX) / 2;
                            float centerY = (leftY + rightY) / 2;

                            float distance = (float) Math.sqrt(
                                    Math.pow(rightX - leftX, 2) + Math.pow(rightY - leftY, 2)
                            );

                            runOnUiThread(() -> {
                                float scaleX = (float) previewView.getWidth() / imageProxy.getHeight();
                                float scaleY = (float) previewView.getHeight() / imageProxy.getWidth();

                                int overlayWidth = (int) (distance * 11.0f);
                                int overlayHeight = (int) (distance * 11.0f);

                                float viewX = centerX * scaleX - overlayWidth / 2f;
                                float viewY = centerY * scaleY - overlayHeight / 2f;

                                viewX = previewView.getWidth() - viewX - overlayWidth;

                                glassesOverlay.setVisibility(View.VISIBLE);
                                glassesOverlay.setX(viewX);
                                glassesOverlay.setY(viewY);
                                glassesOverlay.getLayoutParams().width = overlayWidth;
                                glassesOverlay.getLayoutParams().height = overlayHeight;
                                glassesOverlay.requestLayout();
                            });
                        } else {
                            runOnUiThread(() -> glassesOverlay.setVisibility(View.INVISIBLE));
                        }
                    } else {
                        runOnUiThread(() -> glassesOverlay.setVisibility(View.INVISIBLE));
                    }

                    imageProxy.close();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Face detection failed", e);
                    imageProxy.close();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        if (detector != null) {
            detector.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
        }
    }
}
