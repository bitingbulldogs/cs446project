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
        for(int i = 0; i < maxTexts; ++i) hitrates[i] = 0;
        state = 1;
    }

    // TODO:  Once this implements Detector.Processor<TextBlock>, implement the abstract methods.
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        if(state == 0) return;

        graphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();

        //add line record
        if(lineIndex < maxlines) lines[lineIndex++] = items.size();

        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            /*
            if (item != null && item.getValue() != null) {
                Log.d("Processor", "Text detected! " + item.getValue());
                OcrGraphic graphic = new OcrGraphic(graphicOverlay, item);
                graphicOverlay.add(graphic);
            }
            */

            List<? extends Text> textComponents = item.getComponents();
            for(Text currentText : textComponents) {
                String curText = currentText.getValue();

                //added stuffs
                //make sure we have enough buffer for texts
                if(textCount < maxTexts){
                    //see if we already have this text
                    weAlreadyHaveThisText = false;
                    for(int j = 0; j < textCount; ++j) {
                        if(texts[j].equals(curText)) {
                            weAlreadyHaveThisText = true;
                            hitrates[j]++;
                            break;
                        }
                    }

                    //if we don't have this text
                    if(!weAlreadyHaveThisText) {
                        texts[textCount++] = curText;
                        noRepeatsCount = 0;
                        everythingIsRepeated = false;
                    }
                }
                //text limit exceed maxTexts
                else {
                    //we could do something here
                }
            }
        }

        //System.out.println("Item size is " + items.size());

        if(everythingIsRepeated) {
            if(noRepeatsCount > checkTime && textCount > 0) {
                System.out.println("we are final step");

                /*
                //find out how many lines are there
                int linesize = 0;
                for(int j = 1; j < maxlines / 10; ++j){
                    int sum1 = 0, sum2 = 0, sum3 = 0;
                    for(int k = 0; k < j; ++k) {
                        sum3 += lines[k+j+j+j];
                        sum1 += lines[k+j];
                        sum2 += lines[k+j+j];
                    }
                    if(sum1 == sum2 && sum2 == sum3) linesize = j;
                }
                if(linesize == 0) System.out.println("Error! can't find line size!");
                else System.out.println("Line size is " + linesize);
                */


                //find the best hitrates of texts
                for(int i = 0; i < items.size(); ++i){
                    int maxhit = 0;
                    int maxIndex = -1;
                    for(int j = 0; j < textCount; ++j){
                        if(hitrates[j] >= maxhit) {
                            maxhit = hitrates[j];
                            maxIndex = j;
                        }
                    }
                    hitrates[maxIndex] = 0;
                    //System.out.println(texts[maxIndex]);
                    String subj = texts[maxIndex];

                    //Todo
                    //check similarity of subj and existing strings
                    result.add(texts[maxIndex]);
                }
                state = 0;
                captureAct.sendIntent(result);
            }
            else noRepeatsCount++;
        }
        else everythingIsRepeated = true;
    }

    @Override
    public void release() {
        graphicOverlay.clear();
    }

    //added stuffs
    int maxTexts = 1000;
    int textCount = 0;
    boolean weAlreadyHaveThisText = false;
    boolean everythingIsRepeated = false;
    int noRepeatsCount = 0;
    int checkTime = 4;
    private String[] texts = new String[maxTexts];
    private int[] hitrates = new int[maxTexts];
    private int maxlines = 100;
    private int[] lines = new int[maxlines];
    private int lineIndex = 0;
    private Context context;
    private OcrCaptureActivity captureAct;
    private int state = 0; //0 means finished, 1 means active
    public ArrayList<String> result = new ArrayList<String>();

    //the idea is from wikipedia
    int LevenshteinDistance(String s, int len_s, String t, int len_t) {
        int cost;

        /* base case: empty strings */
        if (len_s == 0) return len_t;
        if (len_t == 0) return len_s;

        /* test if last characters of the strings match */
        if (s.charAt(len_s-1) == t.charAt(len_t-1))
            cost = 0;
        else
            cost = 1;

        /* return minimum of delete char from s, delete char from t, and delete char from both */
        return Math.min(Math.min(LevenshteinDistance(s, len_s - 1, t, len_t    ) + 1,
                LevenshteinDistance(s, len_s    , t, len_t - 1) + 1),
                LevenshteinDistance(s, len_s - 1, t, len_t - 1) + cost);
    }
}
