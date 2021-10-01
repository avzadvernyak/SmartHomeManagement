package m.kampukter.smarthomemanagement.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import m.kampukter.smarthomemanagement.data.*
import m.kampukter.smarthomemanagement.data.dto.WSConnectionStatus
import m.kampukter.smarthomemanagement.data.repository.SensorsRepository
import java.net.URL

class MainViewModel(private val sensorsRepository: SensorsRepository) : ViewModel() {


    private val unitApiId = MutableLiveData<String>()
    fun setUnitApiId(unitId: String) {
        unitApiId.postValue(unitId)
    }

    val sensorInfoApiListByUnitIdLiveData: LiveData<List<SensorInfoRemote>> =
        Transformations.switchMap(unitApiId) { searchId ->
            sensorsRepository.getSensorListByUnitId(searchId).asLiveData()
        }

    val unitInfoRemoteLiveData: LiveData<ResultUnitInfoRemote> =
        sensorsRepository.unitInfoRemoteFlow.asLiveData()

    fun getUnitInfoApi() {
        viewModelScope.launch {
            sensorsRepository.getUnitInfoApi()
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
                    lastSensorType = SensorType.DEFAULT
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

    private val searchSensor = MutableLiveData<Any>()
    fun setSearchSensor(search: Any){
        searchSensor.postValue(search)
    }
    val searchSensorListLiveData: LiveData<List<UnitView>> =
        Transformations.switchMap(searchSensor) { search ->
            sensorsRepository.getSearchSensorList(search).asLiveData()
        }

    private val searchIdUnit = MutableLiveData<String>()
    fun setIdUnitForSearch(idUnit: String) {
        searchIdUnit.postValue(idUnit)
    }

    /*val unitApiViewLiveData: LiveData<UnitApiView?> = Transformations.switchMap(searchIdUnit) { searchId ->
        sensorsRepository.getUnitApiView(searchId).asLiveData()
    }*/
    val unitApiViewLiveData: LiveData<UnitApiView?> = Transformations.switchMap(searchIdUnit) { searchId ->
        sensorsRepository.compareUnitInfoApiFlow(searchId).asLiveData()
    }
    private val unitInformationLiveData: LiveData<UnitInfo> =
        Transformations.switchMap(searchIdUnit) { searchId ->
            sensorsRepository.getSearchUnitInfo(searchId).asLiveData()
        }

    private val editUnitNameMutableLiveData = MutableLiveData<String>()
    fun editUnitName(name: String) {
        editUnitNameMutableLiveData.postValue(name)
    }

    private val editUnitDescriptionMutableLiveData = MutableLiveData<String>()
    fun editUnitDescription(description: String) {
        editUnitDescriptionMutableLiveData.postValue(description)
    }
/*
    fun editUnitUrl(unitId: String, url: String) {
        viewModelScope.launch {
            sensorsRepository.editUnitUrl(unitId, url)
        }
    }
     */

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
            addSource(editUnitNameMutableLiveData) { name ->
                name?.let { unitName ->
                    lastUnitView?.let { unit ->
                        viewModelScope.launch {
                            sensorsRepository.editUnitName(unit.id, unitName)
                        }
                    }
                }
            }
            addSource(editUnitDescriptionMutableLiveData) { description ->
                description?.let { unitDescription ->
                    lastUnitView?.let { unit ->
                        viewModelScope.launch {
                            sensorsRepository.editUnitDescription(unit.id, unitDescription)
                        }
                    }
                }
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

    fun deleteSensorById(sensorId: String) {
        viewModelScope.launch {
            sensorsRepository.deleteSensorById(sensorId)
        }
    }

    val unitsAllLiveData: LiveData<List<UnitInfo>> = sensorsRepository.unitsAllDao.asLiveData()
}