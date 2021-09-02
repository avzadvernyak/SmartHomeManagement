package m.kampukter.smarthomemanagement

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.*
import com.facebook.stetho.Stetho
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import m.kampukter.smarthomemanagement.data.*
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
    @DelicateCoroutinesApi
    private val module = module {
        single {
            Room.databaseBuilder(androidContext(), SmartHomeDatabase::class.java, "smart_home.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(supportDb: SupportSQLiteDatabase) {
                        GlobalScope.launch(context = Dispatchers.IO) {
                            get<SmartHomeDatabase>().sensorRemoteDao().insertAllUnit(
                                listOf(
                                    UnitInfoRemote(
                                        "ESP8266-1",
                                        "Name ESP8266-1",
                                        "http://192.168.0.82:81/",
                                        null
                                    ), UnitInfoRemote(
                                        "ESP8266-2",
                                        "Name ESP8266-2",
                                        "http://192.168.0.83:81/",
                                        null
                                    )
                                )
                            )
                        }
                        GlobalScope.launch(context = Dispatchers.IO) {
                            get<SmartHomeDatabase>().sensorRemoteDao().insertAllSensors(
                                listOf(
                                    SensorInfoRemote(
                                        "1", "ESP8266-2", "1", "Thermometer", "°C",
                                        DeviceType.Device, true
                                    ), SensorInfoRemote(
                                        "2", "ESP8266-1", "0", "Температура на улице", "°C",
                                        DeviceType.Device, true
                                    ), SensorInfoRemote(
                                        "3", "ESP8266-1", "1", "Термометр в тамбуре", "°C",
                                        DeviceType.Device, true
                                    ), SensorInfoRemote(
                                        "4", "ESP8266-1", "2", "Атмосферное давление", "mm Hg",
                                        DeviceType.Device, true
                                    ), SensorInfoRemote(
                                        "5", "ESP8266-1", "3", "Влажность", "%",
                                        DeviceType.Device, true
                                    ), SensorInfoRemote(
                                        "6", "ESP8266-2", "4", "Реле", "",
                                        DeviceType.RELAY, true
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
                get<SmartHomeDatabase>().sensorRemoteDao(),
                get(), get()
            )
        }
        viewModel { MainViewModel(get()) }
    }

    @DelicateCoroutinesApi
    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)

        startKoin {
            androidContext(this@MainApplication)
            modules(module)
        }
        getUnitInfoApi()

    }

    private fun getUnitInfoApi() {
        val downloadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<GetUnitInfoApiCoroutineWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        WorkManager
            .getInstance(this@MainApplication)
            .enqueue(downloadWorkRequest)
    }

}