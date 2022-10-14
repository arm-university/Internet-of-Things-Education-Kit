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

#include <events/mbed_events.h>
#include "mbed.h"
#include "ble/BLE.h"
#include "ble/services/WeatherService.h"
//#include "ble/services/EnvironmentalService.h"
#include "pretty_printer.h"

#define USE_BOARD_TEMP_SENSOR
#ifdef USE_BOARD_TEMP_SENSOR
#include "stm32l475e_iot01_tsensor.h"
#include "stm32l475e_iot01_hsensor.h"
#include "stm32l475e_iot01_psensor.h"
#include "stm32l475e_iot01_magneto.h"
#endif
DigitalOut led1(LED1, 1);

const static char DEVICE_NAME[] = "BLE";
 int16_t _current_windDirection[3]={0};
static events::EventQueue event_queue(/* event count */ 16 * EVENTS_EVENT_SIZE);

class WeatherStationDemo : ble::Gap::EventHandler {
public:
    WeatherStationDemo(BLE &ble, events::EventQueue &event_queue) :
        _ble(ble),
        _event_queue(event_queue),
        _sensor_event_id(0),
        _environmental_uuid(GattService::UUID_ENVIRONMENTAL_SERVICE),
        _current_temperature(0.0f),
        _current_humidity(0.0f),
        _current_pressure(0.0f),
        _wind_direction(0.0f),
        _weatherServicePtr(NULL),
        _adv_data_builder(_adv_buffer) { }

    void start() {
        _ble.gap().setEventHandler(this);

        _ble.init(this, &WeatherStationDemo::on_init_complete);

        _event_queue.call_every(1000, this, &WeatherStationDemo::blink);

        _event_queue.dispatch_forever();
    }

private:
    /** Callback triggered when the ble initialization process has finished */
    void on_init_complete(BLE::InitializationCompleteCallbackContext *params) {
        if (params->error != BLE_ERROR_NONE) {
            print_error(params->error, "Ble initialization failed.");
            return;
        }

        print_mac_address();

        /* Setup primary service. */
          _weatherServicePtr = new WeatherService(_ble);
         

        start_advertising();
    }

    void start_advertising() {
        /* Create advertising parameters and payload */

        ble::AdvertisingParameters adv_parameters(
            ble::advertising_type_t::CONNECTABLE_UNDIRECTED,
            ble::adv_interval_t(ble::millisecond_t(200))
        );

        _adv_data_builder.setFlags();
        _adv_data_builder.setLocalServiceList(mbed::make_Span(&_environmental_uuid, 1));
        _adv_data_builder.setAppearance(ble::adv_data_appearance_t::THERMOMETER_EAR);
        _adv_data_builder.setName(DEVICE_NAME);

        /* Setup advertising */

        ble_error_t error = _ble.gap().setAdvertisingParameters(
            ble::LEGACY_ADVERTISING_HANDLE,
            adv_parameters
        );

        if (error) {
            print_error(error, "_ble.gap().setAdvertisingParameters() failed");
            return;
        }

        error = _ble.gap().setAdvertisingPayload(
            ble::LEGACY_ADVERTISING_HANDLE,
            _adv_data_builder.getAdvertisingData()
        );

        if (error) {
            print_error(error, "_ble.gap().setAdvertisingPayload() failed");
            return;
        }

        /* Start advertising */

        error = _ble.gap().startAdvertising(ble::LEGACY_ADVERTISING_HANDLE);

        if (error) {
            print_error(error, "_ble.gap().startAdvertising() failed");
            return;
        }
    }

    void update_sensor_value() {
        //_current_temperature = (_current_temperature + 0.1f > 43.0f) ? 39.6f : _current_temperature + 0.1f;
        //_weatherServicePtr->updateTemperature(_current_temperature);

      
#ifdef USE_BOARD_TEMP_SENSOR
    _current_temperature=BSP_TSENSOR_ReadTemp();
    _current_humidity=BSP_HSENSOR_ReadHumidity();
    _current_pressure=BSP_PSENSOR_ReadPressure();
     BSP_MAGNETO_GetXYZ(_current_windDirection);
     if (_current_windDirection[0] < 140) _wind_direction = 0; //North
					else if (_current_windDirection[0] >= 140 && _current_windDirection[0] < 200 && -_current_windDirection[1] > 250 ) _wind_direction = 45;  //Northeast
					else if (_current_windDirection[0] >= 140 && _current_windDirection[0] < 200 && -_current_windDirection[1] < 250 ) _wind_direction = 315; //Northwest
					else if (_current_windDirection[0] >= 200 && _current_windDirection[0] < 280 && -_current_windDirection[1] > 250 ) _wind_direction = 90;  //East
					else if (_current_windDirection[0] >= 200 && _current_windDirection[0] < 280 && -_current_windDirection[1] < 250 ) _wind_direction = 270; //Weast
					else if (_current_windDirection[0] >= 280 && _current_windDirection[0] < 380 && -_current_windDirection[1] > 250 ) _wind_direction = 135; //Southeast
					else if (_current_windDirection[0] >= 280 && _current_windDirection[0] < 380 && -_current_windDirection[1] < 250 ) _wind_direction = 225; //Soutwest			
					else if (_current_windDirection[0] >= 380) _wind_direction = 180; //South
    //_wind_direction *=100; 	
     // printf("\nMAGNETO_X = %.2f\n", _current_humidity);      
#endif
    _weatherServicePtr->updateTemperature(_current_temperature);
    _weatherServicePtr->updateHumidity(_current_humidity);
    _weatherServicePtr->updatePressure(_current_pressure);
   _weatherServicePtr->updateWindDirection(_wind_direction);
    }
 
    void blink(void) {
        led1 = !led1;
    }

private:
    /* Event handler */

    virtual void onDisconnectionComplete(const ble::DisconnectionCompleteEvent&) {
        _event_queue.cancel(_sensor_event_id);
        _ble.gap().startAdvertising(ble::LEGACY_ADVERTISING_HANDLE);
    }

    virtual void onConnectionComplete(const ble::ConnectionCompleteEvent &event) {
        if (event.getStatus() == BLE_ERROR_NONE) {
            _sensor_event_id = _event_queue.call_every(1000, this, &WeatherStationDemo::update_sensor_value);
        }
    }

private:
    BLE &_ble;
    events::EventQueue &_event_queue;

    int _sensor_event_id;

    UUID _environmental_uuid;
   
float _current_temperature;
float _current_humidity;
    float _current_pressure;
    uint16_t _wind_direction;
    WeatherService *_weatherServicePtr;


    uint8_t _adv_buffer[ble::LEGACY_ADVERTISING_MAX_SIZE];
    ble::AdvertisingDataBuilder _adv_data_builder;
};

/** Schedule processing of events from the BLE middleware in the event queue. */
void schedule_ble_events(BLE::OnEventsToProcessCallbackContext *context) {
    event_queue.call(Callback<void()>(&context->ble, &BLE::processEvents));
}

int main()
{
     #ifdef USE_BOARD_TEMP_SENSOR
       BSP_TSENSOR_Init();
        BSP_HSENSOR_Init();
        BSP_PSENSOR_Init();
        BSP_MAGNETO_Init();
    #endif
    BLE &ble = BLE::Instance();
    ble.onEventsToProcess(schedule_ble_events);

    WeatherStationDemo demo(ble, event_queue);
    demo.start();

    return 0;
}
