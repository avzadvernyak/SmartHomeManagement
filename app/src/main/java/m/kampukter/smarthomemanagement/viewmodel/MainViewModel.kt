package m.kampukter.smarthomemanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import m.kampukter.smarthomemanagement.data.UnitView
import m.kampukter.smarthomemanagement.data.repository.SensorsRepository

class MainViewModel(private val sensorsRepository: SensorsRepository) : ViewModel() {

    val sensorListLiveData: LiveData<List<UnitView>> = sensorsRepository.sensorListFlow.asLiveData()

    fun connectToDevices() {
        viewModelScope.launch {
            sensorsRepository.connectToWS()
        }
    }

    fun disconnectToDevices() {
        viewModelScope.launch {
            sensorsRepository.disconnectToWS()
        }
    }
}