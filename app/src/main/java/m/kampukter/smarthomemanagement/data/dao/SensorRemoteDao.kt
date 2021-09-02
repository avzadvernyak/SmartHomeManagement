package m.kampukter.smarthomemanagement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import m.kampukter.smarthomemanagement.data.SensorInfoRemote
import m.kampukter.smarthomemanagement.data.UnitInfo
import m.kampukter.smarthomemanagement.data.UnitInfoRemote

@Dao
interface SensorRemoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSensors(sensors: List<SensorInfoRemote>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUnit(units: List<UnitInfoRemote>)

    @Query("SELECT * FROM unit_remote")
    fun getAllUnitsFlow(): Flow<List<UnitInfoRemote>>

    @Query("SELECT * FROM sensor_remote WHERE unit_id = :searchId AND isCandidate")
    fun getSensorRemoteListById(searchId: String): Flow<List<SensorInfoRemote>>

    @Query("UPDATE sensor_remote SET isCandidate = :status WHERE id = :sensorId")
    suspend fun changeCandidateStatus(sensorId: String, status: Boolean)

    @Query("UPDATE unit_remote SET description = :value WHERE id = :unitId")
    suspend fun changeUnitDescription(unitId: String, value: String?)

    @Query("SELECT * FROM unit_remote WHERE id = :name")
    suspend fun getUnitRemoteByName( name: String): UnitInfoRemote?

    @Query("SELECT * FROM sensor_remote WHERE  unit_id = :unitId AND unitSensorId = :name")
    suspend fun getSensorRemoteByName( unitId: String, name: String): SensorInfoRemote?

    @Query("SELECT * FROM unit_remote")
    suspend fun getAllUnits(): List<UnitInfoRemote>

    @Query("SELECT * FROM sensor_remote")
    suspend fun getAllSensors(): List<SensorInfoRemote>
}