package m.kampukter.smarthomemanagement.data.dto

import m.kampukter.smarthomemanagement.data.SensorDataApi
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface SensorsDataApi {
    @GET("sensor_last_data.php?")
    fun getLastDataSensor(): Call<List<SensorDataApi>>

    companion object Factory {
        private const val BASE_URL = "http://orbis.in.ua/api/"
        fun create(): SensorsDataApi {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(SensorsDataApi::class.java)
        }
    }
}