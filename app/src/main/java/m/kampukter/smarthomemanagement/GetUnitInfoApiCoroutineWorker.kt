package m.kampukter.smarthomemanagement

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import m.kampukter.smarthomemanagement.data.repository.SensorsRepository
import org.koin.core.KoinComponent

class GetUnitInfoApiCoroutineWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    override suspend fun doWork(): Result {

        getKoin().get<SensorsRepository>().compareUnitInfoApi()

        return Result.success()
    }
}
