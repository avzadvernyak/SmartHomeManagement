package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.sensor_detailed_fragment.*
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.ResultSensorDataApi
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SensorDetailedFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var listSensorInfoAdapter: SensorDataHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sensor_detailed_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listSensorInfoAdapter = SensorDataHistoryAdapter()
        with(sensorRecyclerView) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = listSensorInfoAdapter
        }

        viewModel.sensorDataApi.observe(viewLifecycleOwner) { resultSensorData ->
            if (resultSensorData is ResultSensorDataApi.Success) listSensorInfoAdapter.setList(
                resultSensorData.sensorValue.sortedByDescending { it.date })
            else listSensorInfoAdapter.setList(emptyList())

        }
    }
    companion object {
        fun createInstance() = SensorDetailedFragment()
    }
}
