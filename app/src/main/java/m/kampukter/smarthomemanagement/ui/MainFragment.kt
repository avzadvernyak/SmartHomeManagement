package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.NetworkLiveData
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.UnitView
import m.kampukter.smarthomemanagement.databinding.MainFragmentBinding
import m.kampukter.smarthomemanagement.ui.remotedata.UnitRemoteListFragment
import m.kampukter.smarthomemanagement.ui.searchsensor.SearchSensorFragment
import m.kampukter.smarthomemanagement.ui.unitinfo.UnitInfoFragment
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

        binding?.mainFragmentProgressBar?.visibility = View.VISIBLE

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
                            UnitInfoFragment.createInstance(item.id)
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
                    activity?.supportFragmentManager?.commit {
                        replace(
                            android.R.id.content,
                            UnitInfoFragment.createInstance(item.id)
                        )
                        setReorderingAllowed(true)
                        addToBackStack("Sensors")
                    }
                }
            }

        }
        binding?.let {
            with(it.sensorRecyclerView) {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = sensorListAdapter
            }
            // Manager FAB
            it.sensorRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && !it.addRemoteSensorExtendedFab.isExtended
                        && recyclerView.computeVerticalScrollOffset() == 0
                    ) {
                        it.addRemoteSensorExtendedFab.extend()
                    }
                    super.onScrollStateChanged(recyclerView, newState)
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy != 0 && it.addRemoteSensorExtendedFab.isExtended) {
                        it.addRemoteSensorExtendedFab.shrink()
                    }
                    super.onScrolled(recyclerView, dx + 16, dy + 16)
                }
            })
            NetworkLiveData.observe(viewLifecycleOwner, { isNetwork ->
                it.addRemoteSensorExtendedFab.visibility =
                    if (isNetwork) View.VISIBLE else View.INVISIBLE
            })
            it.addRemoteSensorExtendedFab.setOnClickListener {
                activity?.supportFragmentManager?.commit {
                    replace(
                        android.R.id.content,
                        UnitRemoteListFragment.createInstance()
                    )
                    setReorderingAllowed(true)
                    addToBackStack("RemoteUnits")
                }
            }
        }
        viewModel.sensorListLiveData.observe(viewLifecycleOwner) { sensors ->
            binding?.mainFragmentProgressBar?.visibility = View.INVISIBLE
            sensorListAdapter.setList(sensors)
        }

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sensor_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.searchSensors) {
            activity?.supportFragmentManager?.commit {
                replace(
                    android.R.id.content,
                    SearchSensorFragment.createInstance()
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
