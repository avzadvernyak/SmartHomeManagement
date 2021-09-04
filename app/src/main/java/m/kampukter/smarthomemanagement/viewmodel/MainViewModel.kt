package m.kampukter.smarthomemanagement.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import m.kampukter.smarthomemanagement.data.*
import m.kampukter.smarthomemanagement.data.dto.WSConnectionStatus
import m.kampukter.smarthomemanagement.data.repository.SensorsRepository
import java.net.URL

class MainViewModel(private val sensorsRepository: SensorsRepository) : ViewModel() {

    val unitInfoApiLiveData: LiveData<ResultUnitsInfoApi> =
        sensorsRepository.unitInfoApiFlow.asLiveData()

    private val sensorInfoListLiveData: LiveData<List<SensorInfo>> =
        sensorsRepository.getSensorsInfo().asLiveData()

    val sensorInfoApiLiveData: LiveData<Pair<ResultUnitsInfoApi, List<SensorInfo>>> =
        MediatorLiveData<Pair<ResultUnitsInfoApi, List<SensorInfo>>>().apply {
            var lastResultUnitsInfoApi: ResultUnitsInfoApi = ResultUnitsInfoApi.EmptyResponse
            var lastSensorInfoList: List<SensorInfo> = emptyList()
            fun update() {
                postValue(Pair(lastResultUnitsInfoApi, lastSensorInfoList))
            }
            addSource(sensorInfoListLiveData) {
                if (it != null) {
                    lastSensorInfoList = it
                    update()
                }
            }
            addSource(unitInfoApiLiveData){
                if (it != null){
                    lastResultUnitsInfoApi = it
                    update()
                }
            }
        }

    private val selectedUnitRemote = MutableLiveData<UnitInfoRemote>()
    fun setSelectedUnitRemote(unit: UnitInfoRemote) {
        selectedUnitRemote.postValue(unit)
    }

    private val selectedSensorRemote = MutableLiveData<SensorInfoRemote>()
    fun setSelectedSensorRemote(sensor: SensorInfoRemote) {
        selectedSensorRemote.postValue(sensor)
    }

    fun addNewSensor(unit: UnitInfo, sensor: SensorInfo) {
        viewModelScope.launch {
            sensorsRepository.insertUnit(unit)
            sensorsRepository.insertSensor(sensor)
        }
    }

    private val sensorNameMutableLiveData = MutableLiveData<String>()
    fun setSensorName(name: String) {
        sensorNameMutableLiveData.postValue(name)
    }

    private val unitDescriptionMutableLiveData = MutableLiveData<String>()
    fun setUnitDescription(description: String) {
        unitDescriptionMutableLiveData.postValue(description)
    }

    private val sensorTypeMutableLiveData = MutableLiveData<SensorType>()
    fun setSensorType(sensorType: SensorType) {
        sensorTypeMutableLiveData.postValue(sensorType)
    }

    val selectedSensorInfoLiveData: LiveData<SensorFullInfo> =
        MediatorLiveData<SensorFullInfo>().apply {
            var lastUnitRemote: UnitInfoRemote? = null
            var lastSensorRemote: SensorInfoRemote? = null
            var lastSensorInfo: SensorFullInfo? = null
            var lastSensorType: SensorType = SensorType.DEFAULT

            fun update() {
                lastUnitRemote?.let { unitRemote ->
                    lastSensorRemote?.let { sensorRemote ->
                        lastSensorInfo = SensorFullInfo(
                            id = sensorRemote.id,
                            unitId = sensorRemote.unitId,
                            unitSensorId = sensorRemote.unitSensorId,
                            unitName = unitRemote.name,
                            unitUrl = unitRemote.url,
                            unitDescription = unitRemote.description,
                            sensorName = sensorRemote.name,
                            sensorMeasure = sensorRemote.measure,
                            sensorDeviceType = sensorRemote.deviceType,
                            sensorType = lastSensorType
                        )
                    }
                }
                if (lastSensorInfo != null) postValue(lastSensorInfo)
            }

            addSource(selectedSensorRemote) {
                if (it != null) {
                    lastSensorRemote = it
                    update()
                }
            }
            addSource(selectedUnitRemote) {
                if (it != null) {
                    lastUnitRemote = it
                    update()
                }
            }
            addSource(sensorTypeMutableLiveData) { sensorType ->
                if (sensorType != null) {
                    lastSensorType = sensorType
                    update()
                }
            }
            addSource(unitDescriptionMutableLiveData) { unitDescription ->
                if (lastUnitRemote != null) {
                    val ref = lastUnitRemote?.copy(description = unitDescription)
                    lastUnitRemote = ref
                    update()

                }
            }
            addSource(sensorNameMutableLiveData) { sensorName ->
                sensorName?.let {
                    if (lastSensorRemote != null) {
                        lastSensorRemote?.name = sensorName
                        update()
                    }
                }
            }
        }

    val sensorListLiveData: LiveData<List<UnitView>> =
        sensorsRepository.sensorListFlow.asLiveData()

    val unitListLiveData: LiveData<List<UnitInfoView>> =
        MediatorLiveData<List<UnitInfoView>>().apply {
            var lastUnitListView = mutableListOf<UnitInfoView>()
            var lastUnitStatus: Pair<URL, WSConnectionStatus>? = null
            fun update() {
                lastUnitStatus?.let { status ->
                    lastUnitListView.find { URL(it.url) == status.first }
                        ?.let { it.wsConnectionStatus = status.second }
                }
                postValue(lastUnitListView)
            }
            addSource(sensorsRepository.unitListFlow.asLiveData()) {
                lastUnitListView = mutableListOf()
                it.forEach { item ->
                    lastUnitListView.add(
                        UnitInfoView(
                            item.id,
                            item.name,
                            item.url,
                            item.description,
                            null
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
                        if (URL(it.url) == status.first) it.wsConnectionStatus =
                            status.second
                    }
                }
                postValue(lastUnitView)
            }
            addSource(unitInformationLiveData) {
                lastUnitView =
                    UnitInfoView(
                        it.id,
                        it.name,
                        it.url,
                        it.description,
                        null
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

    val resultSensorDataApi: LiveData<ResultSensorDataApi> =
        Transformations.switchMap(searchData) { query ->
            sensorsRepository.getResultSensorDataApi(query).asLiveData()
        }

    fun sendCommandToRelay(id: String) {
        viewModelScope.launch { sensorsRepository.sendCommand(id) }
    }

    fun connectToUnit(sensorId: String) {
        viewModelScope.launch {
            sensorsRepository.connectToUnit(sensorId)
        }
    }

    fun disconnectToUnit(sensorId: String) {
        viewModelScope.launch {
            sensorsRepository.disconnectToUnit(sensorId)
        }
    }

    fun connectByIdUnit(unitId: String) {
        viewModelScope.launch {
            sensorsRepository.connectByIdUnit(unitId)
        }
    }

    fun disconnectByIdUnit(unitId: String) {
        viewModelScope.launch {
            sensorsRepository.connectByIdUnit(unitId)
        }
    }


    fun editUnitDescription(unitId: String, description: String) {
        viewModelScope.launch {
            sensorsRepository.editUnitDescription(unitId, description)
        }
    }

    fun editUnitName(unitId: String, name: String) {
        viewModelScope.launch {
            sensorsRepository.editUnitName(unitId, name)
        }
    }

    fun deleteSensorById(sensorId: String) {
        viewModelScope.launch {
            sensorsRepository.deleteSensorById(sensorId)
        }
    }
}