/*
 * Copyright (c) 2022 Arm Limited and affiliates
 *
 * Please see license at: https://github.com/arm-university/Internet-of-Things-Education-Kit/blob/main/License/LICENSE.md
 */

#include "mbed.h"

// Sensors drivers present in the BSP library
#include "stm32l475e_iot01_tsensor.h"
#include "stm32l475e_iot01_hsensor.h"
#include "stm32l475e_iot01_psensor.h"
#include "stm32l475e_iot01_magneto.h"
#include "stm32l475e_iot01_gyro.h"
#include "stm32l475e_iot01_accelero.h"

DigitalOut led(LED1);

int main()
{
    float sensor_value = 0;
    int16_t pDataXYZ[3] = {0};
    float pGyroDataXYZ[3] = {0};

    printf("Start sensor init\n");

    BSP_TSENSOR_Init();
    BSP_HSENSOR_Init();
    BSP_PSENSOR_Init();

    BSP_MAGNETO_Init();
    BSP_GYRO_Init();
    BSP_ACCELERO_Init();

    while(1) {
        printf("\nNew loop, LED1 should blink during sensor read\n");

        led = 1;

        sensor_value = BSP_TSENSOR_ReadTemp();
        printf("\nTEMPERATURE = %.2f degC\n", sensor_value);

        sensor_value = BSP_HSENSOR_ReadHumidity();
        printf("HUMIDITY    = %.2f %%\n", sensor_value);

        sensor_value = BSP_PSENSOR_ReadPressure();
        printf("PRESSURE is = %.2f mBar\n", sensor_value);

        led = 0;

        ThisThread::sleep_for(1000);
       
        led = 1;

        BSP_MAGNETO_GetXYZ(pDataXYZ);
        printf("\nMAGNETO_X = %d\n", pDataXYZ[0]);
        printf("MAGNETO_Y = %d\n", pDataXYZ[1]);
        printf("MAGNETO_Z = %d\n", pDataXYZ[2]);

        BSP_GYRO_GetXYZ(pGyroDataXYZ);
        printf("\nGYRO_X = %.2f\n", pGyroDataXYZ[0]);
        printf("GYRO_Y = %.2f\n", pGyroDataXYZ[1]);
        printf("GYRO_Z = %.2f\n", pGyroDataXYZ[2]);

        BSP_ACCELERO_AccGetXYZ(pDataXYZ);
        printf("\nACCELERO_X = %d\n", pDataXYZ[0]);
        printf("ACCELERO_Y = %d\n", pDataXYZ[1]);
        printf("ACCELERO_Z = %d\n", pDataXYZ[2]);

        led = 0;

        ThisThread::sleep_for(1000);

    }
}
