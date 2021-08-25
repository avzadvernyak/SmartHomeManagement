package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.ResultSensorDataApi
import m.kampukter.smarthomemanagement.databinding.SensorAmountFragmentBinding
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

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
        viewModel.resultSensorDataApi.observe(viewLifecycleOwner) { resultSensorData ->

            var begTime = DateFormat.format(
                getString(R.string.formatDT),
                Date().time - (1000 * 60 * 60 * 24)
            )
            var endTime = DateFormat.format(
                getString(R.string.formatDT),
                Date().time
            )

            if (resultSensorData is ResultSensorDataApi.Success) {
                begTime =
                    DateFormat.format(
                        getString(R.string.formatDT),
                        resultSensorData.sensorValue.first().date * 1000L
                    )
                endTime =
                    DateFormat.format(
                        getString(R.string.formatDT),
                        resultSensorData.sensorValue.last().date * 1000L
                    )
                binding?.let { _binding ->
                    with(_binding) {
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
            binding?.intervalTextView?.text =
                getString(
                    R.string.dateInfoView,
                    begTime.toString(),
                    endTime.toString()
                )
        }
    }
}
