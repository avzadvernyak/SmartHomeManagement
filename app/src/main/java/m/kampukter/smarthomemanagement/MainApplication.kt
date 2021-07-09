package m.kampukter.smarthomemanagement

import android.app.Application
import m.kampukter.smarthomemanagement.data.SensorDataApi
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

class MainApplication:  Application() {
    private val module = module {
        single { NetworkModule.retrofitBuild() }
        single { get<Retrofit>().create(SensorsDataApiInterface::class.java) }
        single<DeviceInteractionApi> { WebSocketDeviceInteractionApi() }
        single { SensorsRepository( get(), get() ) }
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