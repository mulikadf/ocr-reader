/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.star.ocr_reader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

/**
 * Activity for the Ocr Detecting app.  This app detects text and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and contents of each TextBlock.
 */
public abstract class OcrActivity extends AppCompatActivity {
    private OcrView ocrView;
    private OrcPermissionChecker permissionChecker;

    protected  abstract void onDetected(String text);
    protected  abstract OcrView getOcrView();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        ocrView =  getOcrView();

        permissionChecker = new OrcPermissionChecker(this);
        permissionChecker.startCheck(new OrcPermissionChecker.OnPermissionGrantedListener() {
            @Override
            public void onSuccess(boolean autoFocus, boolean useFlash) {
                ocrView.createCameraSource(autoFocus, useFlash);
            }
        });

        ocrView.setDetectorCallback(new DetectorCallback() {
            @Override
            public void onDetected(String text) {
                OcrActivity.this.onDetected(text);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        ocrView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ocrView.pause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ocrView.release();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
