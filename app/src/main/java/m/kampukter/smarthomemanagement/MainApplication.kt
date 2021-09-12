package m.kampukter.smarthomemanagement

import android.app.Application
import androidx.room.Room
import androidx.work.*
import com.facebook.stetho.Stetho
import kotlinx.coroutines.DelicateCoroutinesApi
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
                .build()
        }
        single { NetworkModule.retrofitBuild() }
        single { get<Retrofit>().create(SensorsDataApiInterface::class.java) }
        single<DeviceInteractionApi> { WebSocketDeviceInteractionApi() }
        single {
            SensorsRepository(
                get<SmartHomeDatabase>().sensorInfoDao(),
                get(), get()
            )
        }
        viewModel { MainViewModel(get()) }
    }

    @DelicateCoroutinesApi
    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
        
        NetworkLiveData.init(this)

        startKoin {
            androidContext(this@MainApplication)
            modules(module)
        }
    }
}