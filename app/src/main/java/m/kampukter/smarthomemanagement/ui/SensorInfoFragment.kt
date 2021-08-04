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
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.ResultSensorDataApi
import m.kampukter.smarthomemanagement.data.SensorView
import m.kampukter.smarthomemanagement.databinding.SensorInfoFragmentBinding
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*


class SensorInfoFragment : Fragment() {

    private var binding: SensorInfoFragmentBinding? = null

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
        binding = SensorInfoFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var measure = ""
        val series: LineGraphSeries<DataPoint> = LineGraphSeries()

        @Suppress("UNCHECKED_CAST")
        pickerRange =
            parentFragmentManager.findFragmentByTag("Picker") as? MaterialDatePicker<Pair<Long, Long>>
                ?: MaterialDatePicker.Builder.dateRangePicker().build()

        binding?.previewGraphView?.removeAllSeries()

        (activity as? AppCompatActivity)?.setSupportActionBar(binding?.sensorInfoToolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        binding?.sensorInfoToolbar?.setNavigationOnClickListener {
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

            val sensorFullId = "${sensor?.unitId}${sensor?.unitSensorId}"
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
                    sensors?.find { it.id == id } as SensorView
                binding?.valueTextView?.text = currentSensor.value.toString()

                binding?.lastUpdateTextView?.text = getString(
                    R.string.last_value_title,
                    DateFormat.format("HH:mm dd-MM-yyyy", currentSensor.lastUpdateDate)
                )


                binding?.dimensionTextView?.text = currentSensor.dimension
                currentSensor.dimension?.let { measure = it }
            }
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
                val value = resultSensorData.sensorValue
                val graphValue = Array(value.size) { i ->
                    DataPoint(Date(value[i].date * 1000L), value[i].value.toDouble())
                }

                series.resetData(graphValue)
                binding?.let { _binding ->
                    with(_binding.previewGraphView) {
                        value.map { it.value }.minOrNull()
                            ?.let { viewport.setMinY((it - (it / 20)).toDouble()) }
                        value.map { it.value }.maxOrNull()
                            ?.let { viewport.setMaxY((it + (it / 20)).toDouble()) }
                        viewport.setMinX(value.first().date * 1000L.toDouble())
                        viewport.setMaxX(value.last().date * 1000L.toDouble())

                        series.color = Color.GRAY
                        series.thickness = 8
                        gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.NONE
                        gridLabelRenderer.isHorizontalLabelsVisible = false
                        gridLabelRenderer.isVerticalLabelsVisible = false
                        viewport.isXAxisBoundsManual = true
                        viewport.isYAxisBoundsManual = true

                        addSeries(series)

                    }
                }
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



                val dateMax =
                    resultSensorData.sensorValue.maxByOrNull { it.value }?.date?.let { time ->
                        DateFormat.format("dd/MM/yy HH:mm", time * 1000L)
                    }
                binding?.maxTextView?.text = getString(
                    R.string.maxValuePeriod,
                    resultSensorData.sensorValue.maxByOrNull { it.value }?.value.toString(),
                    measure,
                    dateMax.toString()
                )

                val dateMin =
                    resultSensorData.sensorValue.minByOrNull { it.value }?.date?.let { time ->
                        DateFormat.format("dd/MM/yy HH:mm", time * 1000L)
                    }
                binding?.minTextView?.text = getString(
                    R.string.minValuePeriod,
                    resultSensorData.sensorValue.minByOrNull { it.value }?.value.toString(),
                    measure,
                    dateMin.toString()
                )
                binding?.avgTextView?.text = getString(
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
            binding?.intervalTextView?.text =
                getString(R.string.dateInfoView, begTime.toString(), endTime.toString())
        }
        binding?.graphImageButton?.setOnClickListener {
            activity?.supportFragmentManager?.commit {
                replace(
                    android.R.id.content,
                    SensorGraphFragment.createInstance()
                )
                setReorderingAllowed(true)
                addToBackStack("SensorInfo")
            }
        }
        binding?.listImageButton?.setOnClickListener {
            activity?.supportFragmentManager?.commit {
                replace(
                    android.R.id.content,
                    SensorDetailedFragment.createInstance()
                )
                setReorderingAllowed(true)
                addToBackStack("SensorInfo")
            }
        }
        binding?.intervalImageButton?.setOnClickListener {
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

    override fun onResume() {
        super.onResume()
        sensorId?.let {
            viewModel.connectToUnit(it)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorId?.let {
            viewModel.disconnectToUnit(it)
        }
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