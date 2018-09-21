package com.star.ocr_reader;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class OrcPermissionChecker {
    private static final String TAG = "OrcPermissionChecker";
    private static final int RC_HANDLE_CAMERA_PERM = 779;
    private Activity activity;

    // Constants used to pass extra data in the intent
    private static final String AutoFocus = "AutoFocus";
    private static final String UseFlash = "UseFlash";
    private static final String TextBlockObject = "String";

    private OnPermissionGrantedListener onPermissionGrantedListener;
    public interface OnPermissionGrantedListener{
        void onSuccess(boolean autoFocus, boolean useFlash);
    }

    public OrcPermissionChecker(Activity activity) {
        this.activity = activity;
    }

    public void startCheck(OnPermissionGrantedListener listener){
        int rc = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        onPermissionGrantedListener = listener;
        boolean autoFocus = true;
        boolean useFlash = false;
        if (rc == PackageManager.PERMISSION_GRANTED){
             if (listener != null) {
                 listener.onSuccess(autoFocus, useFlash);
             }
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(activity, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(activity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Toast.makeText(activity, R.string.permission_camera_rationale,
                Toast.LENGTH_LONG).show();
    }

    public boolean onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            return false;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            boolean autoFocus = activity.getIntent().getBooleanExtra(AutoFocus,true);
            boolean useFlash = activity.getIntent().getBooleanExtra(UseFlash, false);
            if (onPermissionGrantedListener != null) {
                onPermissionGrantedListener.onSuccess(autoFocus, useFlash);
            }
            return true;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                activity.finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Permission")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
        return false;
    }

}
