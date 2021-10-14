package m.kampukter.smarthomemanagement.ui.remotedata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.SensorInfoRemote
import m.kampukter.smarthomemanagement.databinding.SensorRemoteListFragmentBinding
import m.kampukter.smarthomemanagement.ui.ClickEventDelegate
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

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

        binding?.let {
            with(it.sensorRemoteRecyclerView) {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = sensorRemoteListAdapter
            }
        }

        //информация о сенсорах в составе заданного устройства
        viewModel.getUnitInfoApi()
        viewModel.sensorInfoApiListByUnitIdLiveData.observe(viewLifecycleOwner) { list ->
            binding?.emptyListTextView?.visibility = if (list.isNotEmpty()) {
                sensorRemoteListAdapter.setList(list)
                View.INVISIBLE
            } else {
                sensorRemoteListAdapter.setList(emptyList())
                View.VISIBLE
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
