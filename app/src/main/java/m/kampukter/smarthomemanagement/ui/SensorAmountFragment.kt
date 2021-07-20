package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.ResultSensorDataApi
import m.kampukter.smarthomemanagement.databinding.SensorAmountFragmentBinding
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SensorAmountFragment : Fragment() {

    private var binding: SensorAmountFragmentBinding? = null

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SensorAmountFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var measure = ""
        viewModel.sensorInformationLiveData.observe(viewLifecycleOwner) { sensor ->
            sensor?.measure?.let { measure = it }
            binding?.sensorNameTextView?.text = sensor?.name
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
                binding?.let { _binding ->
                    with(_binding) {
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
                        val sumValue =
                            resultSensorData.sensorValue.sumByDouble { it.value.toDouble() }
                        averageTextView.text = getString(
                            R.string.averageValuePeriod,
                            (sumValue / resultSensorData.sensorValue.size),
                            measure
                        )
                    }}
            } else {
                binding?.maxTextView?.text = ""
                binding?.minTextView?.text = ""
                binding?.averageTextView?.text = ""

            }
        }
    }
}
