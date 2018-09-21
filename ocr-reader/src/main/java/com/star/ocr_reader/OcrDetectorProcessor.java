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

import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.star.ocr_reader.camera.GraphicOverlay;

/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> graphicOverlay;
    private DetectorCallback detectorCallback;
    private Handler mHandler = new Handler();

    public OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        graphicOverlay = ocrGraphicOverlay;
    }
    public void setDetectorCallback(DetectorCallback detectorCallback) {
        this.detectorCallback = detectorCallback;
    }
    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {

        graphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if (item != null && item.getValue() != null && item.getValue().length() > 5 && item.getValue().length() < 12) {
                // Rimuovo gli spazi
                final String targa = item.getValue().replace(" ", "");
                // Check nuove targhe italiane
                if(targa.matches("[A-Z]{2}[0-9]{3}[A-Z]{2}")){
                    //Log.d("OcrDetectorProcessor", "ok è una targa..!");
                    Log.d("OcrDetectorProcessor", "Text detected! " + targa + " lunghezza: " + targa.length());
					OcrGraphic graphic = new OcrGraphic(graphicOverlay, item);
					graphicOverlay.add(graphic);
                    if (detectorCallback != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                detectorCallback.onDetected(targa);
                            }
                        });
                    }
                    // Parlo
                    //tts.speak("SOSTA VALIDA", TextToSpeech.QUEUE_ADD, null, "DEFAULT");
                }
            }
        }
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        graphicOverlay.clear();
    }
}
