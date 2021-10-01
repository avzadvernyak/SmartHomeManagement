package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.ResultSensorDataApi
import m.kampukter.smarthomemanagement.databinding.SensorDetailedFragmentBinding
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SensorDetailedFragment : Fragment() {

    private var binding: SensorDetailedFragmentBinding? = null

    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var listSensorInfoAdapter: SensorDataHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SensorDetailedFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as? AppCompatActivity)?.setSupportActionBar(binding?.sensorDetailedToolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        binding?.sensorDetailedToolbar?.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        listSensorInfoAdapter = SensorDataHistoryAdapter()
        binding?.let {
            with(it.sensorRecyclerView) {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = listSensorInfoAdapter
            }
        }
        viewModel.resultSensorDataApi.observe(viewLifecycleOwner) { resultSensorData ->
            if (resultSensorData is ResultSensorDataApi.Success) listSensorInfoAdapter.setList(
                resultSensorData.sensorValue.sortedByDescending { it.date })
            else listSensorInfoAdapter.setList(emptyList())

        }
    }

    companion object {
        fun createInstance() = SensorDetailedFragment()
    }
}
