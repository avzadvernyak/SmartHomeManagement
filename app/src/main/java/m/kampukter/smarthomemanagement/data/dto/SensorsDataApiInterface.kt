package m.kampukter.smarthomemanagement.data.dto

import m.kampukter.smarthomemanagement.data.SensorDataApi
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SensorsDataApiInterface {

    @GET("sensor_last_data.php?")
    suspend fun getLastDataSensor(): Response<List<SensorDataApi>>

    @GET("get_info.php?")
    suspend fun getInfoSensorPeriod(
        @Query("sensor") unit: String,
        @Query("period_b") beginDate: String,
        @Query("period_e") endDate: String
    ): Response<List<SensorDataApi>>
}