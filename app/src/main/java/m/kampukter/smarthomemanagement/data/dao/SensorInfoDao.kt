package m.kampukter.smarthomemanagement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import m.kampukter.smarthomemanagement.data.SensorInfo

@Dao
interface SensorInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sensors: List<SensorInfo>)

    @Query("select * from sensor")
    fun getAllFlow(): Flow<List<SensorInfo>>
}
