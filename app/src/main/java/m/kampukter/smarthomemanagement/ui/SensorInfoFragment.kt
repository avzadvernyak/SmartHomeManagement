package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.sensor_info_fragment.*
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.ResultSensorDataApi
import m.kampukter.smarthomemanagement.data.UnitView
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class SensorInfoFragment : Fragment() {
    private val viewModel by sharedViewModel<MainViewModel>()

    private var sensorId: String? = null

    private var strDateBegin =
        DateFormat.format("yyyy-MM-dd", Date(Date().time - (1000 * 60 * 60 * 24))).toString()
    private var strDateEnd: String = DateFormat.format("yyyy-MM-dd", Date()).toString()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.sensor_info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val series: LineGraphSeries<DataPoint> = LineGraphSeries()

        (activity as? AppCompatActivity)?.setSupportActionBar(sensorInfoToolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        sensorInfoToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        savedInstanceState?.let { bundle ->
            bundle.getStringArray(KEY_SELECTED_PERIOD)?.let { saveDate ->
                strDateBegin = saveDate[0]
                strDateEnd = saveDate[1]
            }
        }

        arguments?.getString("ARG_ID_SENSOR")?.let {
            sensorId = it
            viewModel.setIdSensorForSearch(it)
        }
        viewModel.sensorInformationLiveData.observe(viewLifecycleOwner) { sensor ->
            (activity as AppCompatActivity).title = sensor?.name
            val sensorFullId = "${sensor?.unitId}${sensor?.deviceSensorId}"
            viewModel.setQuestionSensorsData(Triple(sensorFullId, strDateBegin, strDateEnd))
        }
        viewModel.sensorListLiveData.observe(viewLifecycleOwner) { sensors ->

            sensorId?.let { id ->
                val currentSensor =
                    sensors?.find { (it as UnitView.SensorView).id == id } as UnitView.SensorView
                valueTextView.text = "${currentSensor.value}${currentSensor.dimension}"
            }
        }
        viewModel.sensorDataApi.observe(viewLifecycleOwner) { resultSensorData ->
            with(graphSensorInfoFS) {
                addSeries(series)
                gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.NONE
                gridLabelRenderer.isHorizontalLabelsVisible = false
                gridLabelRenderer.isVerticalLabelsVisible = false
                viewport.isXAxisBoundsManual = true
                viewport.isYAxisBoundsManual = true
            }
            if (resultSensorData is ResultSensorDataApi.Success) {
                val value = resultSensorData.sensorValue
                val graphValue = Array(value.size) { i ->
                    DataPoint(Date(value[i].date * 1000L), value[i].value.toDouble())
                }
                series.resetData(graphValue)
                value.map { it.value }.minOrNull()
                    ?.let { graphSensorInfoFS.viewport.setMinY((it - (it / 20)).toDouble()) }
                value.map { it.value }.maxOrNull()
                    ?.let { graphSensorInfoFS.viewport.setMaxY((it + (it / 20)).toDouble()) }
                graphSensorInfoFS.viewport.setMinX(value.first().date * 1000L.toDouble())
                graphSensorInfoFS.viewport.setMaxX(value.last().date * 1000L.toDouble())
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArray(KEY_SELECTED_PERIOD, arrayOf(strDateBegin, strDateEnd))
        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val KEY_SELECTED_PERIOD = "KEY_SELECTED_PERIOD"
        private const val ARG_ID_SENSOR = "ARG_ID_SENSOR"
        fun createInstance(message: String) = SensorInfoFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ID_SENSOR, message)
            }
        }
    }
}