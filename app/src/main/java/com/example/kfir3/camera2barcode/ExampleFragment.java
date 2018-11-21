package com.example.kfir3.camera2barcode;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kfir3.camera2barcode.Camera.Camera;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class ExampleFragment extends Fragment {
    private static final String TAG = "Barcode";
    private EditText etBarCode;
    private TextureView textureView;
    private Camera camera;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.example_fragment, container, false);
        etBarCode = view.findViewById(R.id.etBarCode);
        textureView = view.findViewById(R.id.texture);

        this.camera = new Camera(view, getContext());
        textureView.setSurfaceTextureListener(textureListener);

        return view;
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            camera.openCamera(getActivity());
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private void createDetector() {
        BarcodeDetector detector =
                new BarcodeDetector.Builder(getContext())
                        .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                        .build();

        if (detector.isOperational()) {
            detector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    if (barcodes.size() > 0) {
                        Log.i(TAG,barcodes.size() + " barcodes detected!");
                        etBarCode.setText(barcodes.valueAt(0).displayValue);
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "BARCODE DETECTION NOT AVAILABLE", Toast.LENGTH_SHORT).show();
        }
        camera.setmDetector(detector);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        camera.startBackgroundThread();
        createDetector();
        if (textureView.isAvailable()) {
            camera.openCamera(getActivity());
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        camera.closeCamera();
        camera.stopBackgroundThread();
        super.onPause();
    }
}
