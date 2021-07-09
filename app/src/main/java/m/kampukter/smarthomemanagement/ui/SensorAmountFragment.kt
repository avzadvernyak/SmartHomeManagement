package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.sensor_amount_fragment.*
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.ResultSensorDataApi
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SensorAmountFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sensor_amount_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var measure = ""
        viewModel.sensorInformationLiveData.observe(viewLifecycleOwner) { sensor ->
            sensor?.measure?.let { measure = it }
            sensorNameTextView.text = sensor?.name
        }
        viewModel.sensorDataApi.observe(viewLifecycleOwner) { resultSensorData ->
            if (resultSensorData is ResultSensorDataApi.Success) {
                val begTime =
                    DateFormat.format(
                        getString(R.string.formatDT),
                        resultSensorData.sensorValue.first().date * 1000L
                    )
                val endTime =
                    DateFormat.format(
                        getString(R.string.formatDT),
                        resultSensorData.sensorValue.last().date * 1000L
                    )
                intervalTextView.text =
                    getString(
                        R.string.dateInfoView,
                        begTime.toString(),
                        endTime.toString()
                    )
                countTextView.text = resultSensorData.sensorValue.count().toString()
                val dateMax =
                    resultSensorData.sensorValue.maxByOrNull { it.value }?.date?.let { time ->
                        DateFormat.format("dd/MM/yy HH:mm", time * 1000L)
                    }
                maxTextView.text = getString(
                    R.string.maxValuePeriod,
                    resultSensorData.sensorValue.maxByOrNull { it.value }?.value.toString(),
                    measure,
                    dateMax.toString()
                )

                val dateMin =
                    resultSensorData.sensorValue.minByOrNull { it.value }?.date?.let { time ->
                        DateFormat.format("dd/MM/yy HH:mm", time * 1000L)
                    }
                minTextView.text = getString(
                    R.string.minValuePeriod,
                    resultSensorData.sensorValue.minByOrNull { it.value }?.value.toString(),
                    measure,
                    dateMin.toString()
                )
                val sumValue = resultSensorData.sensorValue.sumByDouble { it.value.toDouble() }
                averageTextView.text = getString(
                    R.string.averageValuePeriod,
                    (sumValue / resultSensorData.sensorValue.size),
                    measure
                )
            } else {
                maxTextView.text = ""
                minTextView.text = ""
                averageTextView.text = ""

            }
        }
    }
}
