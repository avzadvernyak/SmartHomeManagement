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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnit(unit: UnitInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSensor(sensor: SensorInfo)

    @Query("select * from sensor")
    fun getAllSensorsFlow(): Flow<List<SensorInfo>>

    @Query("select * from sensor where id = :searchId")
    fun getSensorFlow(searchId: String): Flow<SensorInfo>

    @Query("select sensor.unit_id as unitId,sensor.unitSensorId as unitSensorId,unit.url as unitIp from sensor JOIN unit ON unit.id = sensor.unit_id where sensor.id = :searchId ")
    suspend fun getSensorById(searchId: String): SensorInfoWithIp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUnit(units: List<UnitInfo>)

    @Query("select * from unit where id == :id")
    suspend fun getUnitById( id: String): UnitInfo

    @Query("select * from unit where id = :searchId")
    fun getUnitFlow(searchId: String): Flow<UnitInfo>

    @Query("update unit set description = :description where id = :unitId")
    suspend fun editUnitDescription(unitId: String, description: String)

    @Query("update unit set name = :name where id = :unitId")
    suspend fun editUnitName(unitId: String, name: String)

    @Query("update unit set url = :url where id = :unitId")
    suspend fun editUnitUrl(unitId: String, url: String)

    @Query("DELETE FROM sensor WHERE id = :id")
    suspend fun deleteSensorById(id: String)
}
