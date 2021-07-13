package m.kampukter.smarthomemanagement.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import m.kampukter.smarthomemanagement.data.ResultSensorDataApi
import m.kampukter.smarthomemanagement.data.UnitInfo
import m.kampukter.smarthomemanagement.data.UnitInfoView
import m.kampukter.smarthomemanagement.data.UnitView
import m.kampukter.smarthomemanagement.data.dto.WSConnectionStatus
import m.kampukter.smarthomemanagement.data.repository.SensorsRepository
import java.net.URL

class MainViewModel(private val sensorsRepository: SensorsRepository) : ViewModel() {

    val sensorListLiveData: LiveData<List<UnitView>> = sensorsRepository.sensorListFlow.asLiveData()

    val unitListLiveData: LiveData<List<UnitInfoView>> =
        MediatorLiveData<List<UnitInfoView>>().apply {
            var lastUnitListView = mutableListOf<UnitInfoView>()
            var lastUnitStatus: Pair<URL, WSConnectionStatus>? = null
            fun update() {
                lastUnitStatus?.let { status ->
                    lastUnitListView.find { URL(it.deviceIp) == status.first }
                        ?.let { it.wsConnectionStatus = status.second }
                }
                postValue(lastUnitListView)
            }
            addSource(sensorsRepository.unitListFlow.asLiveData()) {
                lastUnitListView = mutableListOf()
                it.forEach { item ->
                    lastUnitListView.add(
                        UnitInfoView(
                            item.deviceId,
                            item.deviceName,
                            item.deviceIp,
                            item.deviceDescription,
                            WSConnectionStatus.Disconnected
                        )
                    )
                }
                update()
            }
            addSource(sensorsRepository.unitStatusFlow.asLiveData()) {
                it?.let { lastUnitStatus = it }
                update()
            }
        }

    private val searchIdUnit = MutableLiveData<String>()
    fun setIdUnitForSearch(idUnit: String) {
        searchIdUnit.postValue(idUnit)
    }

    private val unitInformationLiveData: LiveData<UnitInfo> =
        Transformations.switchMap(searchIdUnit) { searchId ->
            sensorsRepository.getSearchUnitInfo(searchId).asLiveData()
        }
    val unitLiveData: LiveData<UnitInfoView> =
        MediatorLiveData<UnitInfoView>().apply {
            var lastUnitView: UnitInfoView? = null
            var lastUnitStatus: Pair<URL, WSConnectionStatus>? = null
            fun update() {
                lastUnitStatus?.let { status ->
                    lastUnitView?.let {
                        if (URL(it.deviceIp) == status.first) it.wsConnectionStatus = status.second
                    }
                }
                postValue(lastUnitView)
            }
            addSource(unitInformationLiveData) {
                lastUnitView =
                    UnitInfoView(
                        it.deviceId,
                        it.deviceName,
                        it.deviceIp,
                        it.deviceDescription,
                        WSConnectionStatus.Disconnected
                    )


                update()
            }
            addSource(sensorsRepository.unitStatusFlow.asLiveData()) {
                it?.let { lastUnitStatus = it }
                update()
            }
        }

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
        viewModelScope.launch { sensorsRepository.sendCommand(id) }
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