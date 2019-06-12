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
package com.example.scbcchoi.eatemup;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;

import com.example.scbcchoi.eatemup.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 * TODO: Make this implement Detector.Processor<TextBlock> and add text to the GraphicOverlay
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> graphicOverlay;

    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay, Context context, OcrCaptureActivity cap) {
        graphicOverlay = ocrGraphicOverlay;
        this.context = context;
        this.captureAct = cap;
        state = 1;
    }

    // TODO:  Once this implements Detector.Processor<TextBlock>, implement the abstract methods.
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        if(state == 0) return;

        graphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();

        int curlines = 0;
        for (int i = 0; i < items.size(); ++i){
            TextBlock item = items.valueAt(i);
            curlines += item.getComponents().size();
        }
        boolean noRepeat = true;
        for(int j = 0; j < sizeCount; ++j){
            if(lineSizes[j] == curlines) {
                noRepeat = false;
                lineSizeHitrate[j]++;
                break;
            }
        }
        if(noRepeat) {
            lineSizes[sizeCount] = curlines;
            lineSizeHitrate[sizeCount++] = 0;
        }

        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            List<? extends Text> textComponents = item.getComponents();
            for(Text currentText : textComponents) {
                String curText = currentText.getValue();

                //added stuffs
                //make sure we have enough buffer for texts
                System.out.println(inputCount);
                if(inputCount++ < maxInput){
                    //see if we already have this text
                    weAlreadyHaveThisText = false;
                    for(int j = 0; j < lines.size(); ++j) {
                        if(lines.get(j).getstr().equals(curText)) {
                            weAlreadyHaveThisText = true;
                            lines.get(j).sethits(lines.get(j).gethits() + 1);
                        }
                    }

                    //if we don't have this text
                    if(!weAlreadyHaveThisText) {
                        lines.add(new Line(curText, 0));
                    }
                }
                //that's enough of scanning
                else {

                }
            }
        }
        if(inputCount >= maxInput){
            Collections.sort(lines, Collections.<Line>reverseOrder());
            System.out.println(lines);

            int maxhit = 0;
            for(int i = 0; i < sizeCount; ++i){
                if(lineSizeHitrate[i] > maxhit) {
                    if(lineSizes[i] != 0){
                        actuallines = lineSizes[i];
                        maxhit = lineSizeHitrate[i];
                    }
                }
            }

            for(int l = 0; l < actuallines; ++l) result.add(lines.get(l).getstr());
            captureAct.sendIntent(result);
        }
    }

    @Override
    public void release() {
        graphicOverlay.clear();
    }

    //added stuffs
    boolean weAlreadyHaveThisText = false;
    private Context context;
    private OcrCaptureActivity captureAct;
    private int state = 0; //0 means finished, 1 means active
    public ArrayList<String> result = new ArrayList<String>();

    private ArrayList<Line> lines = new ArrayList<Line>();
    private int inputCount = 0;
    private int maxInput = 250;
    private int actuallines = 0;

    private int[] lineSizes = new int[100];
    private int[] lineSizeHitrate = new int[100];
    private int sizeCount = 0;


}

class Line implements Comparable<Line>{
    private String str;
    private int hits;

    Line(String s, int h){
        str = s;
        hits = h;
    }

    public String getstr(){
        return str;
    }

    public void setStr(String s){
        str = s;
    }

    public Integer gethits() {
        return hits;
    }

    public void sethits(int h) {
        hits = h;
    }

    @Override
    public String toString() {
        return "Line str = " + str + ", hits = " + hits + "\n";
    }

    @Override
    public int compareTo(Line o) {
        return this.gethits().compareTo(o.gethits());
    }
}