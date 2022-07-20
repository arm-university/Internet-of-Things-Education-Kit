from mbed_cloud import ConnectAPI
import datetime
import time
import pandas as pd
import sys
from queue import Queue
import numpy as np
import math

## gyro sensor calibration:
## x: -272
## y: -352
## z: -1063


reading_queue = Queue()
last_callback = None
window_size = 10 # 2 seconds

class readings(object):
    def __init__(self, acc, gyro):
        self.acc = acc
        self.gyro = gyro

    def to_list(self):
        return [*self.acc, *self.gyro]


def get_device_id(api):
    devices = api.list_connected_devices().data
    idx = 0
    last_dt = None
    for i, device in enumerate(devices):
        if last_dt is None:
            last_dt = device.updated_at
            idx = i
        if device.updated_at > last_dt:
            idx = i
    # print(idx)
    # print(len(devices))
    return devices[idx].id

def gyro_calibrate(gyro):
    return [gyro[0] - 272, gyro[1] - 352, gyro[2] - 1063]


def acc_gyro_callback(id, path, value):
    global last_callback
    if last_callback is None:
        last_callback = time.time()
    if time.time() - last_callback < 0.1:
        # print("old data")
        return 0
    value = value.decode("utf-8")
    values = value.strip(",").split(",")

    acc = [values[0], values[2], values[4]]
    gyro = [values[1], values[3], values[5]]
    acc = [int(i) * 0.001 for i in acc]
    gyro = [int(i) for i in gyro]
    gyro = gyro_calibrate(gyro)
    gyro = [math.radians(i* 0.001) for i in gyro]
    
    reading_queue.put(readings(acc, gyro))
    last_callback = time.time()

def feature_extractor(data):
    features = []
    for i in range(6):
        features.append(data[:,i].mean())
        features.append(data[:,i].std())
    return np.array(features)



def test_data_generator():
    api = ConnectAPI()
    device_id = get_device_id(api)
    api.start_notifications()
    api.add_resource_subscription_async(device_id, '/3333/0/5704', acc_gyro_callback)
    while True:
        if reading_queue.qsize() >= window_size:
            sec_readings = [reading_queue.get().to_list() for _ in range(window_size)]
            sec_readings = np.array(sec_readings)
            # print(sec_readings)
            yield feature_extractor(sec_readings)



if __name__ == '__main__':
    for data in test_data_generator():
        print(data)