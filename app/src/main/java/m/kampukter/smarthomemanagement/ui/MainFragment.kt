package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.UnitView
import m.kampukter.smarthomemanagement.databinding.MainFragmentBinding
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MainFragment : Fragment() {

    private var binding: MainFragmentBinding? = null

    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var sensorListAdapter: SensorListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding?.mainFragmentToolbar)
        (activity as AppCompatActivity).title = getString(R.string.title_main)

        sensorListAdapter = SensorListAdapter().apply {
            clickSensorEventDelegate = object : ClickEventDelegate<UnitView> {
                override fun onClick(item: UnitView) {
                    activity?.supportFragmentManager?.commit {
                        replace(
                            android.R.id.content,
                            SensorInfoFragment.createInstance(item.id)
                        )
                        setReorderingAllowed(true)
                        addToBackStack("Sensors")
                    }

                }

                override fun onLongClick(item: UnitView) {
                    activity?.supportFragmentManager?.commit {
                        replace(
                            android.R.id.content,
                            SensorFragment.createInstance(item.id)
                        )
                        setReorderingAllowed(true)
                        addToBackStack("Sensors")
                    }
                }
            }
            clickRelayEventDelegate = object : ClickEventDelegate<UnitView> {
                override fun onClick(item: UnitView) {
                    activity?.supportFragmentManager?.commit {
                        replace(
                            android.R.id.content,
                            RelayInfoFragment.createInstance(item.id)
                        )
                        setReorderingAllowed(true)
                        addToBackStack("Sensors")
                    }
                }

                override fun onLongClick(item: UnitView) {
                    Log.d("blabla", "Long click ${item.id}")
                }
            }

        }
        binding?.let {
            with(it.sensorRecyclerView) {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = sensorListAdapter
            }
        }
        viewModel.sensorListLiveData.observe(viewLifecycleOwner) { sensors ->
            sensorListAdapter.setList(sensors)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sensor_info_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.unitManagement) {
            activity?.supportFragmentManager?.commit {
                replace(
                    android.R.id.content,
                    UnitListFragment.createInstance()
                )
                setReorderingAllowed(true)
                addToBackStack("Sensors")
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun createInstance(): MainFragment {
            return MainFragment()
        }

    }

}
