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

    @Query("select * from unit_remote")
    fun getAllUnitsFlow(): Flow<List<UnitInfoRemote>>

    @Query("select * from sensor_remote where unit_id = :searchId and isCandidate")
    fun getSensorRemoteListById(searchId: String): Flow<List<SensorInfoRemote>>

    @Query("update sensor_remote set isCandidate = :status where id = :sensorId")
    suspend fun changeCandidateStatus(sensorId: String, status: Boolean)

    @Query("update unit_remote set description = :value where id = :unitId")
    suspend fun changeUnitDescription(unitId: String, value: String)
}