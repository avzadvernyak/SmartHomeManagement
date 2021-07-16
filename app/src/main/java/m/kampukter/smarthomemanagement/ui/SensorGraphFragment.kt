package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.sensor_graph_fragment.*
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.ResultSensorDataApi
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class SensorGraphFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sensor_graph_fragment, container, false)
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
        viewModel.sensorDataApi.observe(viewLifecycleOwner) { resultSensorData ->
            with(graphSensorFS) {
                addSeries(series)
                gridLabelRenderer.isHorizontalLabelsVisible = false
                viewport.isXAxisBoundsManual = true
                viewport.isYAxisBoundsManual = true
                title = "$nameSensor $measure"
            }
            if (resultSensorData is ResultSensorDataApi.Success) {
                val value = resultSensorData.sensorValue
                val graphValue = Array(value.size) { i ->
                    DataPoint(Date(value[i].date * 1000L), value[i].value.toDouble())
                }
                series.resetData(graphValue)
                value.map { it.value }.minOrNull()
                    ?.let { graphSensorFS.viewport.setMinY((it - (it / 20)).toDouble()) }
                value.map { it.value }.maxOrNull()
                    ?.let { graphSensorFS.viewport.setMaxY((it + (it / 20)).toDouble()) }
                graphSensorFS.viewport.setMinX(value.first().date * 1000L.toDouble())
                graphSensorFS.viewport.setMaxX(value.last().date * 1000L.toDouble())
            }
        }
    }

    companion object {
        fun createInstance() = SensorGraphFragment()
    }
}
