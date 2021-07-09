package m.kampukter.smarthomemanagement.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import m.kampukter.smarthomemanagement.data.ResultSensorDataApi
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

    private val searchIdSensor = MutableLiveData<String>()
    fun setIdSensorForSearch( id: String){
        searchIdSensor.postValue( id )
    }
    val sensorInformationLiveData = Transformations.switchMap(searchIdSensor) { searchId ->
        sensorsRepository.getSearchSensorInfo( searchId ).asLiveData()
    }

    private val searchData = MutableLiveData<Triple<String,String,String>>()
    fun setQuestionSensorsData(setSensorRequest: Triple<String,String,String>) =
        searchData.postValue(setSensorRequest)

    val sensorDataApi: LiveData<ResultSensorDataApi> = Transformations.switchMap(searchData){ query ->
        sensorsRepository.getResultSensorDataApi( query ).asLiveData()
    }
}