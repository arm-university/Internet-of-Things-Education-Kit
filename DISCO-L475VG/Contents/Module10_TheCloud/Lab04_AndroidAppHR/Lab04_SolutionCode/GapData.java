/*
 * Copyright (c) 2022 Arm Limited and affiliates
 *
 * Please see license at: https://github.com/arm-university/Internet-of-Things-Education-Kit/blob/main/License/LICENSE.md
 */

package com.arm.university.heartrate;

import java.util.ArrayList;

public class GapData {
    private ArrayList<EirRecord> mRecords;

    public GapData(byte[] scanRecord) {
        // Parse the scanRecord array as EIR (Extended Inquiry Response)
        // More info in the bluetooth spec v4.0, p1690
        mRecords = new ArrayList<EirRecord>();
        int buffPos = 0;
        while (buffPos < scanRecord.length)
        {
            int len = scanRecord[buffPos++];
            if (len == 0)
                break;
            byte[] data = new byte[len - 1];
            System.arraycopy(scanRecord, buffPos + 1, data, 0, data.length);
            mRecords.add(new EirRecord(scanRecord[buffPos], data));
            buffPos += len;
        }
    }

    public int getAppearance() {
        EirRecord record = getRecord(EirRecord.TYPE_APPEARANCE);
        if (record != null) {
            byte[] data = record.getData();
            return data[0] | (data[1] << 8);
        } else {
            return -1;
        }
    }

    private EirRecord getRecord(int type) {
        for (EirRecord record : mRecords) {
            if (record.getType() == type) {
                return record;
            }
        }
        return null;
    }

    class EirRecord {
        public static final char TYPE_FLAGS = 0x01;
        public static final char TYPE_SERVICE_MORE16 = 0x02;
        public static final char TYPE_SERVICE_COMPLETE16 = 0x03;
        public static final char TYPE_SERVICE_MORE32 = 0x04;
        public static final char TYPE_SERVICE_COMPLETE32 = 0x05;
        public static final char TYPE_SERVICE_MORE128 = 0x06;
        public static final char TYPE_SERVICE_COMPLETE128 = 0x07;
        public static final char TYPE_NAME_SHORT = 0x08;
        public static final char TYPE_NAME_COMPLETE = 0x09;
        public static final char TYPE_TX_POWER = 0x0A;
        public static final char TYPE_PAIRING_CLASS = 0x0D;
        public static final char TYPE_PAIRING_HASH = 0x0E;
        public static final char TYPE_PAIRING_RANDOMISER = 0x0F;
        public static final char TYPE_TK = 0x10;
        public static final char TYPE_SECURITY_FLAGS = 0x11;
        public static final char TYPE_SLAVE_INTERVAL = 0x12;
        public static final char TYPE_SERVICESOLICITATION_16 = 0x14;
        public static final char TYPE_SERVICESOLICITATION_32 = 0x1F;
        public static final char TYPE_SERVICESOLICITATION_128 = 0x15;
        public static final char TYPE_SERVICEDATA = 0x16;
        public static final char TYPE_APPEARANCE = 0x19;
        public static final char TYPE_MANUFACTURER = 0xFF;

        private byte mType;
        private byte[] mData;

        public EirRecord(byte type, byte[] data) {
            mType = type;
            mData = data;
        }

        public byte getType() {
            return mType;
        }

        public byte[] getData() {
            return mData;
        }
    }
}
