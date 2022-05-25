/*
 * Copyright (c) 2022 Arm Limited and affiliates
 *
 * Please see license at: https://github.com/arm-university/Internet-of-Things-Education-Kit/blob/main/License/LICENSE.md
 */

package com.arm.university.heartrate;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class HeartRateActivity extends Activity {

    private static final String TAG = "HeartRateActivity";

    AlertDialog mSelectionDialog;
    DevicesAdapter mDevicesAdapter;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner mBluetoothScanner;
    Handler mHandler;
    boolean mScanning;

    BluetoothGatt mGatt;

    private static final int SCAN_PERIOD = 100000;
    private Queue<BluetoothGattDescriptor> descriptorWriteQueue = new LinkedList<>();
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
        mDevicesAdapter = new DevicesAdapter(getLayoutInflater());
        //Getting permission to have access to BLE data
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "The permission to get BLE location data is required", Toast.LENGTH_SHORT).show();
            }else{
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_device);
        builder.setAdapter(mDevicesAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishScanning();
                BluetoothDevice device = (BluetoothDevice)mDevicesAdapter.getItem(i);
                if (device != null) {
                    Log.i(TAG, "Connecting to GATT server at: " + device.getAddress());
                    mGatt = device.connectGatt(HeartRateActivity.this, false, mGattCallback);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finishScanning();
            }
        });
        mSelectionDialog = builder.create();

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothScanner=mBluetoothAdapter.getBluetoothLeScanner();
        setContentView(R.layout.activity_heart_rate);
    }

    public void onConnectClick(View view) {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            String btnText = ((Button)view).getText().toString();
            if (btnText.equals(getString(R.string.connect))) {
                openSelectionDialog();
            } else if (btnText.equals(getString(R.string.disconnect))) {
                mGatt.disconnect();
                mGatt.close();
                updateConnectButton(BluetoothProfile.STATE_DISCONNECTED);
            }
        }
    }

    void openSelectionDialog() {
        beginScanning();
        mSelectionDialog.show();
    }

    private void beginScanning() {

        if (!mScanning) {

            if (mBluetoothScanner == null) mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finishScanning();
                }
            }, SCAN_PERIOD);
            mDevicesAdapter.clear();
            mDevicesAdapter.add(null);
            mDevicesAdapter.updateScanningState(mScanning = true);
            mDevicesAdapter.notifyDataSetChanged();

            mBluetoothScanner.startScan(leScanCallback);
        }
    }
    private void finishScanning() {
        if (mBluetoothScanner == null) mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mDevicesAdapter.updateScanningState(mScanning = false);
        if (mScanning) {
            if (mDevicesAdapter.getItem(0) == null) {
                mDevicesAdapter.notifyDataSetChanged();
            }
            mBluetoothScanner.stopScan(leScanCallback);
        }
    }
    private void disconnect() {
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
            mGatt = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mBluetoothAdapter.isEnabled()) finishScanning();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                openSelectionDialog();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "App cannot run with bluetooth off", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateConnectButton(int state) {
        Button connectBtn = (Button) HeartRateActivity.this.findViewById(R.id.connect_button);
        switch (state) {
            case BluetoothProfile.STATE_DISCONNECTED:
                connectBtn.setText(getString(R.string.connect));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView heartRateTxt = (TextView)HeartRateActivity.this.findViewById(R.id.heart_rate);
                        heartRateTxt.setText(R.string.unknown_heart_rate);
                    }
                });
                break;
            case BluetoothProfile.STATE_CONNECTING:
                connectBtn.setText(getString(R.string.connecting));
                break;
            case BluetoothProfile.STATE_CONNECTED:
                connectBtn.setText(getString(R.string.disconnect));
                break;
        }
    }

    private void subscribeToNotifications(BluetoothGattCharacteristic characteristic) {
        // Enable notifications.
        mGatt.setCharacteristicNotification(characteristic, true);
        // Tell the HRM we want to receive notifications.
        UUID ccc = AssignedNumber.getBleUuid("Client Characteristic Configuration");
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(ccc);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mGatt.writeDescriptor(descriptor);
    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            super.onScanResult(callbackType, result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mDevicesAdapter.getItem(0) == null) {
                        mDevicesAdapter.remove(0);
                    }
                    mDevicesAdapter.add(result.getDevice());
                    mDevicesAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, final int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server.");
                if (mGatt.discoverServices()) {
                    Log.i(TAG, "Started service discovery.");
                } else {
                    Log.w(TAG, "Service discovery failed.");
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateConnectButton(newState);
                }
            });
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            BluetoothGattService hrService = gatt.getService(
                    AssignedNumber.getBleUuid("Heart Rate")
            );
            if (hrService != null) {
                BluetoothGattCharacteristic hrCharacteristic = hrService.getCharacteristic(
                        AssignedNumber.getBleUuid("Heart Rate Measurement")
                );
                if (hrCharacteristic != null) {
                    Log.i(TAG, "Subscribing to heart rate measurement characteristic.");
                    subscribeToNotifications(hrCharacteristic);
                } else {
                    Log.w(TAG, "Can't find heart rate measurement characteristic.");
                }
            } else {
                Log.w(TAG, "Can't find heart rate service.");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            if (characteristic.getUuid().equals(AssignedNumber.getBleUuid("Heart Rate Measurement"))) {
                HeartRateMeasurement data = new HeartRateMeasurement(characteristic);
                if (data.isContactFound()) {
                    Log.d(TAG, "Contact found.");
                }
                final int heartRate = data.getHeartRate();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView heartRateTxt = (TextView)HeartRateActivity.this.findViewById(R.id.heart_rate);
                        heartRateTxt.setText(Integer.toString(heartRate));
                    }
                });
                Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            }
        }
    };
    public void writeGattDescriptor(BluetoothGattDescriptor descriptor) {
        descriptorWriteQueue.add(descriptor);

        Log.d(TAG, "Subscribed to " + descriptorWriteQueue.size() + " notification/s");

        try {
            if (descriptorWriteQueue.size() == 1)
                mGatt.writeDescriptor(descriptor);
        } catch (Exception e) {
            e.getLocalizedMessage();
        }
    }
    class GattCharacteristicWrapper {
        protected BluetoothGattCharacteristic characteristic;

        public GattCharacteristicWrapper(BluetoothGattCharacteristic characteristic) {
            this.characteristic = characteristic;
        }
    }

    class HeartRateMeasurement extends GattCharacteristicWrapper {
        public HeartRateMeasurement(BluetoothGattCharacteristic characteristic) {
            super(characteristic);
        }

        public int getFlag() {
            return characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        }

        public int getHeartRate() {
            int format = (getFlag() & 0x01) != 0 ? BluetoothGattCharacteristic.FORMAT_UINT16 : BluetoothGattCharacteristic.FORMAT_UINT8;
            return characteristic.getIntValue(format, 1);
        }

        public boolean isContactSupported() {
            return (getFlag() & 0x04) != 0;
        }

        public boolean isContactFound() {
            return isContactSupported() && ((getFlag() & 0x02) != 0);
        }
    }
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mGatt == null)
            return null;

        return mGatt.getServices();
    }
}
