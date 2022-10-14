/*
 * Copyright (c) 2022 Arm Limited and affiliates
 *
 * Please see license at: https://github.com/arm-university/Internet-of-Things-Education-Kit/blob/main/License/LICENSE.md
 */

package com.arm.university.heartrate;

import android.util.SparseArray;

import java.util.UUID;


public class AssignedNumber {
    private static SparseArray<String> sAssignedNumbers = new SparseArray<String>();

    private static final String FORMAT_BLE_UUID = "0000%04x-0000-1000-8000-00805f9b34fb";

    static {
        // Services.
        // Taken from https://developer.bluetooth.org/gatt/services/Pages/ServicesHome.aspx
        sAssignedNumbers.put(0x1811, "Alert Notification Service");
        sAssignedNumbers.put(0x180F, "Battery Service");
        sAssignedNumbers.put(0x1810, "Blood Pressure");
        sAssignedNumbers.put(0x1805, "Current Time Service");
        sAssignedNumbers.put(0x1818, "Cycling Power");
        sAssignedNumbers.put(0x1816, "Cycling Speed and Cadence");
        sAssignedNumbers.put(0x180A, "Device Information");
        sAssignedNumbers.put(0x1800, "Generic Access");
        sAssignedNumbers.put(0x1801, "Generic Attribute");
        sAssignedNumbers.put(0x1808, "Glucose");
        sAssignedNumbers.put(0x1809, "Health Thermometer");
        sAssignedNumbers.put(0x180D, "Heart Rate");
        sAssignedNumbers.put(0x1812, "Human Interface Device");
        sAssignedNumbers.put(0x1802, "Immediate Alert");
        sAssignedNumbers.put(0x1803, "Link Loss");
        sAssignedNumbers.put(0x1819, "Location and Navigation");
        sAssignedNumbers.put(0x1807, "Next DST Change Service");
        sAssignedNumbers.put(0x180E, "Phone Alert Status Service");
        sAssignedNumbers.put(0x1806, "Reference Time Update Service");
        sAssignedNumbers.put(0x1814, "Running Speed and Cadence");
        sAssignedNumbers.put(0x1813, "Scan Parameters");
        sAssignedNumbers.put(0x1804, "Tx Power");
        sAssignedNumbers.put(0x181C, "User Data");

        // Characteristics.
        // Taken from https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicsHome.aspx
        sAssignedNumbers.put(0x2A7E, "Aerobic Heart Rate Lower Limit");
        sAssignedNumbers.put(0x2A84, "Aerobic Heart Rate Upper Limit");
        sAssignedNumbers.put(0x2A7F, "Aerobic Threshold");
        sAssignedNumbers.put(0x2A80, "Age");
        sAssignedNumbers.put(0x2A43, "Alert Category ID");
        sAssignedNumbers.put(0x2A42, "Alert Category ID Bit Mask");
        sAssignedNumbers.put(0x2A06, "Alert Level");
        sAssignedNumbers.put(0x2A44, "Alert Notification Control Point");
        sAssignedNumbers.put(0x2A3F, "Alert Status");
        sAssignedNumbers.put(0x2A81, "Anaerobic Heart Rate Lower Limit");
        sAssignedNumbers.put(0x2A82, "Anaerobic Heart Rate Upper Limit");
        sAssignedNumbers.put(0x2A83, "Anaerobic Threshold");
        sAssignedNumbers.put(0x2A01, "Appearance");
        sAssignedNumbers.put(0x2A19, "Battery Level");
        sAssignedNumbers.put(0x2A49, "Blood Pressure Feature");
        sAssignedNumbers.put(0x2A35, "Blood Pressure Measurement");
        sAssignedNumbers.put(0x2A38, "Body Sensor Location");
        sAssignedNumbers.put(0x2A22, "Boot Keyboard Input Report");
        sAssignedNumbers.put(0x2A32, "Boot Keyboard Output Report");
        sAssignedNumbers.put(0x2A33, "Boot Mouse Input Report");
        sAssignedNumbers.put(0x2A5C, "CSC Feature");
        sAssignedNumbers.put(0x2A5B, "CSC Measurement");
        sAssignedNumbers.put(0x2A2B, "Current Time");
        sAssignedNumbers.put(0x2A66, "Cycling Power Control Point");
        sAssignedNumbers.put(0x2A65, "Cycling Power Feature");
        sAssignedNumbers.put(0x2A63, "Cycling Power Measurement");
        sAssignedNumbers.put(0x2A64, "Cycling Power Vector");
        sAssignedNumbers.put(0x2A99, "Database Change Increment");
        sAssignedNumbers.put(0x2A85, "Date of Birth");
        sAssignedNumbers.put(0x2A86, "Date of Threshold Assessment ");
        sAssignedNumbers.put(0x2A08, "Date Time");
        sAssignedNumbers.put(0x2A0A, "Day Date Time");
        sAssignedNumbers.put(0x2A09, "Day of Week");
        sAssignedNumbers.put(0x2A00, "Device Name");
        sAssignedNumbers.put(0x2A0D, "DST Offset");
        sAssignedNumbers.put(0x2A87, "Email Address");
        sAssignedNumbers.put(0x2A0C, "Exact Time 256");
        sAssignedNumbers.put(0x2A88, "Fat Burn Heart Rate Lower Limit");
        sAssignedNumbers.put(0x2A89, "Fat Burn Heart Rate Upper Limit");
        sAssignedNumbers.put(0x2A26, "Firmware Revision String");
        sAssignedNumbers.put(0x2A8A, "First Name");
        sAssignedNumbers.put(0x2A8B, "Five Zone Heart Rate Limits");
        sAssignedNumbers.put(0x2A8C, "Gender");
        sAssignedNumbers.put(0x2A51, "Glucose Feature");
        sAssignedNumbers.put(0x2A18, "Glucose Measurement");
        sAssignedNumbers.put(0x2A34, "Glucose Measurement Context");
        sAssignedNumbers.put(0x2A27, "Hardware Revision String");
        sAssignedNumbers.put(0x2A39, "Heart Rate Control Point");
        sAssignedNumbers.put(0x2A8D, "Heart Rate Max");
        sAssignedNumbers.put(0x2A37, "Heart Rate Measurement");
        sAssignedNumbers.put(0x2A8E, "Height");
        sAssignedNumbers.put(0x2A4C, "HID Control Point");
        sAssignedNumbers.put(0x2A4A, "HID Information");
        sAssignedNumbers.put(0x2A8F, "Hip Circumference");
        sAssignedNumbers.put(0x2A2A, "IEEE 11073-20601 Regulatory Certification Data List");
        sAssignedNumbers.put(0x2A36, "Intermediate Cuff Pressure");
        sAssignedNumbers.put(0x2A1E, "Intermediate Temperature");
        sAssignedNumbers.put(0x2AA2, "Language");
        sAssignedNumbers.put(0x2A90, "Last Name");
        sAssignedNumbers.put(0x2A6B, "LN Control Point");
        sAssignedNumbers.put(0x2A6A, "LN Feature");
        sAssignedNumbers.put(0x2A0F, "Local Time Information");
        sAssignedNumbers.put(0x2A67, "Location and Speed");
        sAssignedNumbers.put(0x2A29, "Manufacturer Name String");
        sAssignedNumbers.put(0x2A91, "Maximum Recommended Heart Rate");
        sAssignedNumbers.put(0x2A21, "Measurement Interval");
        sAssignedNumbers.put(0x2A24, "Model Number String");
        sAssignedNumbers.put(0x2A68, "Navigation");
        sAssignedNumbers.put(0x2A46, "New Alert");
        sAssignedNumbers.put(0x2A04, "Peripheral Preferred Connection Parameters");
        sAssignedNumbers.put(0x2A02, "Peripheral Privacy Flag");
        sAssignedNumbers.put(0x2A50, "PnP ID");
        sAssignedNumbers.put(0x2A69, "Position Quality");
        sAssignedNumbers.put(0x2A4E, "Protocol Mode");
        sAssignedNumbers.put(0x2A03, "Reconnection Address");
        sAssignedNumbers.put(0x2A52, "Record Access Control Point");
        sAssignedNumbers.put(0x2A14, "Reference Time Information");
        sAssignedNumbers.put(0x2A4D, "Report");
        sAssignedNumbers.put(0x2A4B, "Report Map");
        sAssignedNumbers.put(0x2A92, "Resting Heart Rate");
        sAssignedNumbers.put(0x2A40, "Ringer Control Point");
        sAssignedNumbers.put(0x2A41, "Ringer Setting");
        sAssignedNumbers.put(0x2A54, "RSC Feature");
        sAssignedNumbers.put(0x2A53, "RSC Measurement");
        sAssignedNumbers.put(0x2A55, "SC Control Point");
        sAssignedNumbers.put(0x2A4F, "Scan Interval Window");
        sAssignedNumbers.put(0x2A31, "Scan Refresh");
        sAssignedNumbers.put(0x2A5D, "Sensor Location");
        sAssignedNumbers.put(0x2A25, "Serial Number String");
        sAssignedNumbers.put(0x2A05, "Service Changed");
        sAssignedNumbers.put(0x2A28, "Software Revision String");
        sAssignedNumbers.put(0x2A93, "Sport Type for Aerobic and Anaerobic Thresholds");
        sAssignedNumbers.put(0x2A47, "Supported New Alert Category");
        sAssignedNumbers.put(0x2A48, "Supported Unread Alert Category");
        sAssignedNumbers.put(0x2A23, "System ID");
        sAssignedNumbers.put(0x2A1C, "Temperature Measurement");
        sAssignedNumbers.put(0x2A1D, "Temperature Type");
        sAssignedNumbers.put(0x2A94, "Three Zone Heart Rate Limits");
        sAssignedNumbers.put(0x2A12, "Time Accuracy");
        sAssignedNumbers.put(0x2A13, "Time Source");

        // GATT
        // https://www.bluetooth.org/en-us/specification/assigned-numbers/generic-attribute-profile
        sAssignedNumbers.put(0x1800, "Generic Access Profile");
        sAssignedNumbers.put(0x1801, "Generic Attribute Profile");
        sAssignedNumbers.put(0x2800, "Primary Service");
        sAssignedNumbers.put(0x2801, "Secondary Service");
        sAssignedNumbers.put(0x2802, "Include");
        sAssignedNumbers.put(0x2803, "Characteristic");
        sAssignedNumbers.put(0x2900, "Characteristic Extended Properties");
        sAssignedNumbers.put(0x2901, "Characteristic User Description");
        sAssignedNumbers.put(0x2902, "Client Characteristic Configuration");
        sAssignedNumbers.put(0x2903, "Server Characteristic Configuration");
        sAssignedNumbers.put(0x2904, "Characteristic Format");
        sAssignedNumbers.put(0x2905, "Characteristic Aggregate Format");
        sAssignedNumbers.put(0x2A00, "Device Name");
        sAssignedNumbers.put(0x2A01, "Appearance");
        sAssignedNumbers.put(0x2A02, "Peripheral Privacy Flag");
        sAssignedNumbers.put(0x2A03, "Reconnection Address");
        sAssignedNumbers.put(0x2A04, "Peripheral Preferred Connection Parameters");
        sAssignedNumbers.put(0x2A05, "Service Changed");
    }

    public static UUID getBleUuid(String name) {
        for(int i = 0; i < sAssignedNumbers.size(); i++){
            int key = sAssignedNumbers.keyAt(i);
            if (sAssignedNumbers.valueAt(i).equals(name)) {
                return UUID.fromString(String.format(FORMAT_BLE_UUID, key));
            }
        }
        return null;
    }

    public static int get(String name) {
        for(int i = 0; i < sAssignedNumbers.size(); i++){
            int key = sAssignedNumbers.keyAt(i);
            if (sAssignedNumbers.valueAt(i).equals(name)) {
                return key;
            }
        }
        return -1;
    }
}
