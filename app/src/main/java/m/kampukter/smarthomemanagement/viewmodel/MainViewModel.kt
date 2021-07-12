package m.kampukter.smarthomemanagement.viewmodel

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import m.kampukter.smarthomemanagement.data.ResultSensorDataApi
import m.kampukter.smarthomemanagement.data.SensorInfoWithIp
import m.kampukter.smarthomemanagement.data.UnitInfo
import m.kampukter.smarthomemanagement.data.UnitView
import m.kampukter.smarthomemanagement.data.repository.SensorsRepository
import java.net.URL

class MainViewModel(private val sensorsRepository: SensorsRepository) : ViewModel() {

    val sensorListLiveData: LiveData<List<UnitView>> = sensorsRepository.sensorListFlow.asLiveData()
    val unitListLiveData: LiveData<List<UnitInfo>> = sensorsRepository.getUnitsInfo().asLiveData()

    private val searchIdSensor = MutableLiveData<String>()
    fun setIdSensorForSearch(id: String) {
        searchIdSensor.postValue(id)
    }

    val sensorInformationLiveData = Transformations.switchMap(searchIdSensor) { searchId ->
        sensorsRepository.getSearchSensorInfo(searchId).asLiveData()
    }

    private val searchData = MutableLiveData<Triple<String, String, String>>()
    fun setQuestionSensorsData(setSensorRequest: Triple<String, String, String>) =
        searchData.postValue(setSensorRequest)

    fun sendCommandToRelay(id: UnitView.RelayView) {
        viewModelScope.launch { sensorsRepository.sendCommand( id ) }
    }

    fun connectToUnit(urlUnit: URL) {
        sensorsRepository.connectToUnit(urlUnit)
    }
    fun disconnectToUnit(urlUnit: URL) {
        sensorsRepository.disconnectToUnit(urlUnit)
    }

    val sensorDataApi: LiveData<ResultSensorDataApi> =
        Transformations.switchMap(searchData) { query ->
            sensorsRepository.getResultSensorDataApi(query).asLiveData()
        }
}