/* mbed Microcontroller Library
 * Copyright (c) 2006-2013 ARM Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef __BLE_WEATHER_SERVICE_H__
#define __BLE_WEATHER_SERVICE_H__

#include "ble/BLE.h"

#if BLE_FEATURE_GATT_SERVER

/**
* @class EnvironmentalService
* @brief BLE Environmental Service. This service provides temperature, humidity and pressure measurement.
* Service:  https://developer.bluetooth.org/gatt/services/Pages/ServiceViewer.aspx?u=org.bluetooth.service.environmental_sensing.xml
* Temperature: https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.temperature.xml
* Humidity: https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.humidity.xml
* Pressure: https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.pressure.xml
*/
class WeatherService {
public:
    typedef int16_t  TemperatureType_t;
    typedef uint16_t HumidityType_t;
    typedef uint32_t PressureType_t;
    typedef uint32_t magnetometer_t;

    /**
     * @brief   EnvironmentalService constructor.
     * @param   _ble Reference to BLE device.
     */
    WeatherService(BLE &_ble) :
        ble(_ble),
        temperatureCharacteristic(GattCharacteristic::UUID_TEMPERATURE_CHAR, &temperature,GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_NOTIFY),
        humidityCharacteristic(GattCharacteristic::UUID_HUMIDITY_CHAR, &humidity,GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_NOTIFY),
        pressureCharacteristic(GattCharacteristic::UUID_PRESSURE_CHAR, &pressure, GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_NOTIFY),
        windDirectionCharecteristic(GattCharacteristic::UUID_MAGNETO_CHAR, &windDirection, GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_NOTIFY)
    {
        static bool serviceAdded = false; /* We should only ever need to add the information service once. */
        if (serviceAdded) {
            return;
        }

        GattCharacteristic *charTable[] = { &temperatureCharacteristic,
                                            &humidityCharacteristic,
                                            &pressureCharacteristic,
                                            &windDirectionCharecteristic};

        GattService WeatherService(GattService::UUID_ENVIRONMENTAL_SERVICE, charTable, sizeof(charTable) / sizeof(GattCharacteristic *));

        ble.gattServer().addService(WeatherService);
        serviceAdded = true;
    }

    /**
     * @brief   Update humidity characteristic.
     * @param   newHumidityVal New humidity measurement.
     */
    void updateHumidity(HumidityType_t newHumidityVal)
    {
        humidity = (HumidityType_t) (newHumidityVal * 100);
        ble.gattServer().write(humidityCharacteristic.getValueHandle(), (uint8_t *) &humidity, sizeof(HumidityType_t));
    }
    void updatePressure(PressureType_t newPressureVal)
    {
        pressure = (PressureType_t) (newPressureVal * 10);
        ble.gattServer().write(pressureCharacteristic.getValueHandle(), (uint8_t *) &pressure, sizeof(PressureType_t));
    }
    void updateTemperature(float newTemperatureVal)
    {
        temperature = (TemperatureType_t) (newTemperatureVal * 100);
        ble.gattServer().write(temperatureCharacteristic.getValueHandle(), (uint8_t *) &temperature, sizeof(TemperatureType_t));
    }

     void updateWindDirection(magnetometer_t newMagnetoVal)
    {
        windDirection=(magnetometer_t) (newMagnetoVal * 100);
        ble.gattServer().write(windDirectionCharecteristic.getValueHandle(),(uint8_t *) &windDirection, sizeof(magnetometer_t));
    }
    /**
     * @brief   Update pressure characteristic.
     * @param   newPressureVal New pressure measurement.
     */
 

    /**
     * @brief   Update temperature characteristic.
     * @param   newTemperatureVal New temperature measurement.
     */


private:
    BLE& ble;

    TemperatureType_t temperature;
    HumidityType_t    humidity;
    PressureType_t    pressure;
  magnetometer_t    windDirection;

    ReadOnlyGattCharacteristic<TemperatureType_t> temperatureCharacteristic;
    ReadOnlyGattCharacteristic<HumidityType_t>    humidityCharacteristic;
    ReadOnlyGattCharacteristic<PressureType_t>    pressureCharacteristic;
   ReadOnlyGattCharacteristic<magnetometer_t>    windDirectionCharecteristic;  
};

#endif // BLE_FEATURE_GATT_SERVER

#endif /* #ifndef __BLE_ENVIRONMENTAL_SERVICE_H__*/
