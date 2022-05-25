/*
 * Copyright (c) 2022 Arm Limited and affiliates
 *
 * Please see license at: https://github.com/arm-university/Internet-of-Things-Education-Kit/blob/main/License/LICENSE.md
 */

package com.arm.university.heartrate;

import android.util.SparseArray;

public class GapAppearance {
    private static SparseArray<String> sGapAppearences = new SparseArray<String>();

    static {
        sGapAppearences.put(0, "Unknown");
        sGapAppearences.put(64, "Generic Phone");
        sGapAppearences.put(128, "Generic Computer");
        sGapAppearences.put(192, "Generic Watch");
        sGapAppearences.put(193, "Watch: Sports Watch");
        sGapAppearences.put(256, "Generic Clock");
        sGapAppearences.put(320, "Generic Display");
        sGapAppearences.put(384, "Generic Remote Control");
        sGapAppearences.put(448, "Generic Eye-glasses");
        sGapAppearences.put(512, "Generic Tag");
        sGapAppearences.put(576, "Generic Keyring");
        sGapAppearences.put(640, "Generic Media Player");
        sGapAppearences.put(704, "Generic Barcode Scanner");
        sGapAppearences.put(768, "Generic Thermometer");
        sGapAppearences.put(769, "Thermometer: Ear");
        sGapAppearences.put(832, "Generic Heart rate Sensor");
        sGapAppearences.put(833, "Heart Rate Sensor: Heart Rate Belt");
        sGapAppearences.put(896, "Generic Blood Pressure");
        sGapAppearences.put(897, "Blood Pressure: Arm");
        sGapAppearences.put(898, "Blood Pressure: Wrist");
        sGapAppearences.put(960, "Human Interface Device (HID)");
        sGapAppearences.put(961, "Keyboard");
        sGapAppearences.put(962, "Mouse");
        sGapAppearences.put(963, "Joystick");
        sGapAppearences.put(964, "Gamepad");
        sGapAppearences.put(965, "Digitizer Tablet");
        sGapAppearences.put(966, "Card Reader");
        sGapAppearences.put(967, "Digital Pen");
        sGapAppearences.put(968, "Barcode Scanner");
        sGapAppearences.put(1024, "Generic Glucose Meter");
        sGapAppearences.put(1088, "Generic: Running Walking Sensor");
        sGapAppearences.put(1089, "Running Walking Sensor: In-Shoe");
        sGapAppearences.put(1090, "Running Walking Sensor: On-Shoe");
        sGapAppearences.put(1091, "Running Walking Sensor: On-Hip");
        sGapAppearences.put(1152, "Generic: Cycling");
        sGapAppearences.put(1153, "Cycling: Cycling Computer");
        sGapAppearences.put(1154, "Cycling: Speed Sensor");
        sGapAppearences.put(1155, "Cycling: Cadence Sensor");
        sGapAppearences.put(1156, "Cycling: Power Sensor");
        sGapAppearences.put(1157, "Cycling: Speed and Cadence Sensor");
        sGapAppearences.put(3136, "Generic: Pulse Oximeter");
        sGapAppearences.put(3137, "Fingertip");
        sGapAppearences.put(3138, "Wrist Worn");
        sGapAppearences.put(3200, "Generic: Weight Scale");
        sGapAppearences.put(5184, "Generic: Outdoor Sports Activity");
        sGapAppearences.put(5185, "Location Display Device");
        sGapAppearences.put(5186, "Location and Navigation Display Device");
        sGapAppearences.put(5187, "Location Pod");
        sGapAppearences.put(5188, "Location and Navigation Pod");
    }

    public static int get(String name) {
        for(int i = 0; i < sGapAppearences.size(); i++){
            int key = sGapAppearences.keyAt(i);
            if (sGapAppearences.valueAt(i).equals(name)) {
                return key;
            }
        }
        return -1;
    }
}
