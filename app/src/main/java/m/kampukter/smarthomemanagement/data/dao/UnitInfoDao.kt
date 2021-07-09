package m.kampukter.smarthomemanagement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import m.kampukter.smarthomemanagement.data.UnitInfo
import java.net.URL

@Dao
interface UnitInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(units: List<UnitInfo>)

    @Query("select * from unit")
    fun getAllFlow(): Flow<List<UnitInfo>>

    @Query("select deviceIp from unit")
    fun getUrlFlow(): Flow<List<String>>
}
