package m.kampukter.smarthomemanagement.ui.remotedata

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.ResultUnitsInfoApi
import m.kampukter.smarthomemanagement.data.SensorInfoRemote
import m.kampukter.smarthomemanagement.data.UnitInfoRemote
import m.kampukter.smarthomemanagement.databinding.SensorRemoteListFragmentBinding
import m.kampukter.smarthomemanagement.ui.ClickEventDelegate
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class SensorsRemoteListFragment : Fragment() {
    private var binding: SensorRemoteListFragmentBinding? = null
    private val viewModel by sharedViewModel<MainViewModel>()

    private lateinit var sensorRemoteListAdapter: SensorRemoteListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SensorRemoteListFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var currentUnitId: String? = null

        (activity as? AppCompatActivity)?.setSupportActionBar(binding?.sensorRemoteListToolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        binding?.sensorRemoteListToolbar?.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        arguments?.getString("ARG_REMOTE_UNIT_ID")?.let {
            (activity as AppCompatActivity).title = "Sensors $it"
            currentUnitId = it
        }
        sensorRemoteListAdapter = SensorRemoteListAdapter().apply {
            clickSensorEventDelegate = object : ClickEventDelegate<SensorInfoRemote> {
                override fun onClick(item: SensorInfoRemote) {
                    viewModel.setSelectedSensorRemote(item)
                    activity?.supportFragmentManager?.commit {
                        replace(
                            android.R.id.content,
                            AddRemoteSensorFragment.createInstance()
                        )
                        setReorderingAllowed(true)
                        addToBackStack("AddRemoteSensor")
                    }
                }

                override fun onLongClick(item: SensorInfoRemote) {
                }
            }
        }
        /*sensorRemoteListAdapter = SensorRemoteListAdapter()
        viewModel.sensorInfoListLiveData.observe(viewLifecycleOwner) { sensors ->
            sensorRemoteListAdapter.clickSensorEventDelegate =
                object : ClickEventDelegate<SensorInfoRemote> {
                    override fun onClick(item: SensorInfoRemote) {
                        if (sensors.find { item.unitId != it.unitId && item.unitSensorId == it.unitSensorId } != null) {
                            viewModel.setSelectedSensorRemote(item)
                            activity?.supportFragmentManager?.commit {
                                replace(
                                    android.R.id.content,
                                    AddRemoteSensorFragment.createInstance()
                                )
                                setReorderingAllowed(true)
                                addToBackStack("AddRemoteSensor")
                            }
                        }
                    }

                    override fun onLongClick(item: SensorInfoRemote) {
                    }
                }

        }*/
        binding?.let {
            with(it.sensorRemoteRecyclerView) {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = sensorRemoteListAdapter
            }
        }

        currentUnitId?.let { unitId ->
            viewModel.sensorInfoApiLiveData.observe(viewLifecycleOwner) { (resultUnitInfo, sensors) ->
                when (resultUnitInfo) {
                    is ResultUnitsInfoApi.Success -> {
                        val sensorList =
                            resultUnitInfo.infoApi.units.find { it.name == currentUnitId }?.sensors?.map {
                                SensorInfoRemote(
                                    id = UUID.randomUUID().toString(),
                                    unitId = unitId,
                                    unitSensorId = it.unitSensorId,
                                    name = it.name,
                                    measure = it.measure,
                                    deviceType = it.deviceType,
                                    isCandidate = sensors.find { sensor -> it.unitSensorId == sensor.unitSensorId && sensor.unitId == unitId } == null
                                )
                            }
                        binding?.emptyListTextView?.visibility = if (sensorList != null) {
                            sensorRemoteListAdapter.setList(sensorList)
                            View.INVISIBLE
                        } else {
                            sensorRemoteListAdapter.setList(emptyList())
                            View.VISIBLE
                        }
                    }
                    is ResultUnitsInfoApi.EmptyResponse -> {
                        binding?.emptyListTextView?.visibility = View.VISIBLE
                        Log.d("blabla", "UnitsInfoApi.EmptyResponse")
                    }
                    is ResultUnitsInfoApi.OtherError -> {
                        Log.d("blabla", "UnitsInfoApi.OtherError")
                    }
                }
            }
        }
    }

    companion object {
        private const val ARG_REMOTE_UNIT_ID = "ARG_REMOTE_UNIT_ID"
        fun createInstance(message: String) = SensorsRemoteListFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_REMOTE_UNIT_ID, message)
            }
        }
    }
}
