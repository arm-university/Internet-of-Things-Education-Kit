/* mbed Microcontroller Library
 * Copyright (c) 2006-2019 ARM Limited
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
#include "WeatherService.h"
#include "pretty_printer.h"

#include "stm32l475e_iot01_tsensor.h"
#include "stm32l475e_iot01_hsensor.h"
#include "stm32l475e_iot01_psensor.h"
#include "stm32l475e_iot01_magneto.h"
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

        _event_queue.call_every(500, this, &WeatherStationDemo::blink);

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

        
	    
	/* Setup advertising */



        /* Start advertising */

    }

    void update_sensor_value() {

	/* Read from sensors here and update values for adversing */    
    
    
    
    
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
    BSP_TSENSOR_Init();
    BSP_HSENSOR_Init();
    BSP_PSENSOR_Init();
    BSP_MAGNETO_Init();
    
    BLE &ble = BLE::Instance();
    ble.onEventsToProcess(schedule_ble_events);

    WeatherStationDemo demo(ble, event_queue);
    demo.start();

    return 0;
}
