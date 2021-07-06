package m.kampukter.smarthomemanagement.data.dto

import m.kampukter.smarthomemanagement.data.SensorDataApi
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SensorIDataApi {
    @GET("sensor_last_data.php?")
    fun getLastDataSensor(
        @Query("sensor") unit: String,
    ): Call<SensorDataApi>

    companion object Factory {
        private const val BASE_URL = "http://orbis.in.ua/api/"
        fun create(): SensorIDataApi {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(SensorIDataApi::class.java)
        }
    }
}