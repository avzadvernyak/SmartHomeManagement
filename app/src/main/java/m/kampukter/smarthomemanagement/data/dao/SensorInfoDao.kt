package m.kampukter.smarthomemanagement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import m.kampukter.smarthomemanagement.data.SensorInfo
import m.kampukter.smarthomemanagement.data.SensorInfoWithIp
import m.kampukter.smarthomemanagement.data.UnitInfo

@Dao
interface SensorInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSensors(sensors: List<SensorInfo>)

    @Query("select * from sensor")
    fun getAllSensorsFlow(): Flow<List<SensorInfo>>

    @Query("select * from sensor where id = :searchId")
    fun getSensorFlow(searchId: String): Flow<SensorInfo>

    @Query("select sensor.device_id as deviceId,sensor.deviceSensorId as deviceSensorId,unit.deviceIp as deviceIp from sensor JOIN unit ON unit.device_id = sensor.device_id where sensor.id = :searchId ")
    fun getRelayByIdFlow(searchId: String): Flow<SensorInfoWithIp>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUnit(units: List<UnitInfo>)

    @Query("select * from unit")
    fun getAllUnitFlow(): Flow<List<UnitInfo>>

    @Query("select deviceIp from unit")
    fun getUrlFlow(): Flow<List<String>>
}
