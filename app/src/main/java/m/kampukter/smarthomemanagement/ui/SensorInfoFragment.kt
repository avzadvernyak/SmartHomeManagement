package m.kampukter.smarthomemanagement.ui

import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
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

    private lateinit var pickerRange: MaterialDatePicker<Pair<Long, Long>>

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

        var measure = ""
        val series: LineGraphSeries<DataPoint> = LineGraphSeries()

        @Suppress("UNCHECKED_CAST")
        pickerRange =
            parentFragmentManager.findFragmentByTag("Picker") as? MaterialDatePicker<Pair<Long, Long>>
                ?: MaterialDatePicker.Builder.dateRangePicker().build()

        previewGraphView.removeAllSeries()

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

            pickerRange.addOnPositiveButtonClickListener { dateSelected ->
                dateSelected.first?.let {
                    strDateBegin = DateFormat.format("yyyy-MM-dd", it).toString()
                }
                dateSelected.second?.let {
                    strDateEnd = DateFormat.format("yyyy-MM-dd", it).toString()
                }
                viewModel.setQuestionSensorsData(Triple(sensorFullId, strDateBegin, strDateEnd))
            }

        }

        viewModel.sensorListLiveData.observe(viewLifecycleOwner) { sensors ->

            sensorId?.let { id ->
                val currentSensor =
                    sensors?.find { (it as UnitView.SensorView).id == id } as UnitView.SensorView
                valueTextView.text = currentSensor.value.toString()
                dimensionTextView.text = currentSensor.dimension
                currentSensor.dimension?.let { measure = it }
            }
        }
        viewModel.sensorDataApi.observe(viewLifecycleOwner) { resultSensorData ->

            if (resultSensorData is ResultSensorDataApi.Success) {
                val value = resultSensorData.sensorValue
                val graphValue = Array(value.size) { i ->
                    DataPoint(Date(value[i].date * 1000L), value[i].value.toDouble())
                }

                series.resetData(graphValue)
                value.map { it.value }.minOrNull()
                    ?.let { previewGraphView.viewport.setMinY((it - (it / 20)).toDouble()) }
                value.map { it.value }.maxOrNull()
                    ?.let { previewGraphView.viewport.setMaxY((it + (it / 20)).toDouble()) }
                previewGraphView.viewport.setMinX(value.first().date * 1000L.toDouble())
                previewGraphView.viewport.setMaxX(value.last().date * 1000L.toDouble())



                with(previewGraphView) {
                    series.color = Color.GRAY
                    series.thickness = 8
                    gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.NONE
                    gridLabelRenderer.isHorizontalLabelsVisible = false
                    gridLabelRenderer.isVerticalLabelsVisible = false
                    viewport.isXAxisBoundsManual = true
                    viewport.isYAxisBoundsManual = true

                    addSeries(series)
                }

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
                    getString(R.string.dateInfoView, begTime.toString(), endTime.toString())

                //countButton.text = getString(R.string.countButtonTitle, resultSensorData.sensorValue.count())
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
                avgTextView.text = getString(
                    R.string.averageValue,
                    resultSensorData.sensorValue.map { it.value }.average(),
                    measure, resultSensorData.sensorValue.count()
                )
            } else if (resultSensorData is ResultSensorDataApi.EmptyResponse) {
                Snackbar.make(
                    view,
                    getString(R.string.noDataMessage, strDateBegin, strDateEnd),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
        graphImageButton.setOnClickListener {
            activity?.supportFragmentManager?.commit {
                replace(
                    android.R.id.content,
                    SensorGraphFragment.createInstance()
                )
                setReorderingAllowed(true)
                addToBackStack("SensorInfo")
            }
        }
        listImageButton.setOnClickListener {
            activity?.supportFragmentManager?.commit {
                replace(
                    android.R.id.content,
                    SensorDetailedFragment.createInstance()
                )
                setReorderingAllowed(true)
                addToBackStack("SensorInfo")
            }
        }
        intervalImageButton.setOnClickListener {
            parentFragmentManager.let {
                pickerRange.show(
                    it,
                    "Picker"
                )
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