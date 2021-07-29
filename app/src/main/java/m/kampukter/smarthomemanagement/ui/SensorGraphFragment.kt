package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import m.kampukter.smarthomemanagement.data.ResultSensorDataApi
import m.kampukter.smarthomemanagement.databinding.SensorGraphFragmentBinding
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class SensorGraphFragment : Fragment() {

    private var binding: SensorGraphFragmentBinding? = null
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SensorGraphFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val series: LineGraphSeries<DataPoint> = LineGraphSeries()
        var measure = ""
        var nameSensor = ""
        viewModel.sensorInformationLiveData.observe(viewLifecycleOwner) { sensor ->
            sensor?.measure?.let { measure = it }
            sensor?.name?.let { nameSensor = it }
        }
        viewModel.resultSensorDataApi.observe(viewLifecycleOwner) { resultSensorData ->
            binding?.let { _binding ->
                with(_binding.graphSensorFS) {
                    addSeries(series)
                    gridLabelRenderer.isHorizontalLabelsVisible = false
                    viewport.isXAxisBoundsManual = true
                    viewport.isYAxisBoundsManual = true
                    title = "$nameSensor $measure"
                    if (resultSensorData is ResultSensorDataApi.Success) {
                        val value = resultSensorData.sensorValue
                        val graphValue = Array(value.size) { i ->
                            DataPoint(Date(value[i].date * 1000L), value[i].value.toDouble())
                        }
                        series.resetData(graphValue)
                        value.map { it.value }.minOrNull()
                            ?.let { viewport.setMinY((it - (it / 20)).toDouble()) }
                        value.map { it.value }.maxOrNull()
                            ?.let { viewport.setMaxY((it + (it / 20)).toDouble()) }
                        viewport.setMinX(value.first().date * 1000L.toDouble())
                        viewport.setMaxX(value.last().date * 1000L.toDouble())
                    }
                }
            }

        }
    }

    companion object {
        fun createInstance() = SensorGraphFragment()
    }
}
