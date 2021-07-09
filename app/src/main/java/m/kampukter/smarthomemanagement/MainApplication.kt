package m.kampukter.smarthomemanagement

import android.app.Application
import android.os.Build
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import m.kampukter.smarthomemanagement.data.SensorInfo
import m.kampukter.smarthomemanagement.data.SensorType
import m.kampukter.smarthomemanagement.data.SmartHomeDatabase
import m.kampukter.smarthomemanagement.data.UnitInfo
import m.kampukter.smarthomemanagement.data.dto.DeviceInteractionApi
import m.kampukter.smarthomemanagement.data.dto.NetworkModule
import m.kampukter.smarthomemanagement.data.dto.SensorsDataApiInterface
import m.kampukter.smarthomemanagement.data.dto.WebSocketDeviceInteractionApi
import m.kampukter.smarthomemanagement.data.repository.SensorsRepository
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.*

class MainApplication : Application() {
    private val module = module {
        single {
            Room.databaseBuilder(androidContext(), SmartHomeDatabase::class.java, "smart_home.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(supportDb: SupportSQLiteDatabase) {
                        GlobalScope.launch(context = Dispatchers.IO) {
                            get<SmartHomeDatabase>().sensorInfoDao().insertAll(
                                listOf(
                                    SensorInfo(
                                        "1", "ESP8266-2", "1", "Thermometer", "°C",
                                        SensorType.SENSOR, 1
                                    ), SensorInfo(
                                        "2", "ESP8266-1", "0", "Температура на улице", "°C",
                                        SensorType.SENSOR, 1
                                    ), SensorInfo(
                                        "3", "ESP8266-1", "1", "Термометр в тамбуре", "°C",
                                        SensorType.SENSOR, 1
                                    ), SensorInfo(
                                        "4", "ESP8266-1", "2", "Атмосферное давление", "mm Hg",
                                        SensorType.SENSOR, 2
                                    ), SensorInfo(
                                        "5", "ESP8266-1", "3", "Влажность", "%",
                                        SensorType.SENSOR, 3
                                    )
                                )
                            )
                        }
                        GlobalScope.launch(context = Dispatchers.IO) {
                            get<SmartHomeDatabase>().unitInfoDao().insertAll(
                                listOf(
                                    UnitInfo(
                                        "ESP8266-1",
                                        "Name ESP8266-1",
                                        "http://192.168.0.82:81/",
                                        null
                                    ), UnitInfo(
                                        "ESP8266-2",
                                        "Name ESP8266-2",
                                        "http://192.168.0.83:81/",
                                        null
                                    )
                                )
                            )
                        }
                    }
                }).build()
        }
        single { NetworkModule.retrofitBuild() }
        single { get<Retrofit>().create(SensorsDataApiInterface::class.java) }
        single<DeviceInteractionApi> { WebSocketDeviceInteractionApi() }
        single {
            SensorsRepository(
                get<SmartHomeDatabase>().sensorInfoDao(),
                get<SmartHomeDatabase>().unitInfoDao(), get(), get()
            )
        }
        viewModel { MainViewModel(get()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(module)
        }
    }
}