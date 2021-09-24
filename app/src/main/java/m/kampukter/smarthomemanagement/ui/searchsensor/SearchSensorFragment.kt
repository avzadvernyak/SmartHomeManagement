package m.kampukter.smarthomemanagement.ui.searchsensor

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
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.SensorType
import m.kampukter.smarthomemanagement.data.UnitInfo
import m.kampukter.smarthomemanagement.databinding.SearchSensorFragmentBinding
import m.kampukter.smarthomemanagement.ui.ClickEventDelegate
import m.kampukter.smarthomemanagement.ui.RelayInfoFragment
import m.kampukter.smarthomemanagement.ui.remotedata.SensorTypesAdapter
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SearchSensorFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private var binding: SearchSensorFragmentBinding? = null
    private lateinit var unitListAdapter: UnitListAdapter
    private lateinit var sensorTypesAdapter: SensorTypesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = SearchSensorFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.setSupportActionBar(binding?.searchSensorFragmentToolbar)
        (activity as AppCompatActivity).title = getString(R.string.title_search)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        binding?.searchSensorFragmentToolbar?.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        unitListAdapter = UnitListAdapter().apply {
            clickUnitEventDelegate = object : ClickEventDelegate<UnitInfo> {
                override fun onClick(item: UnitInfo) {
                    viewModel.setSearchSensor(item)
                    activity?.supportFragmentManager?.commit {
                        replace(
                            android.R.id.content,
                            ResultSearchFragment.createInstance()
                        )
                        setReorderingAllowed(true)
                        addToBackStack("SearchSensors")
                    }
                }

                override fun onLongClick(item: UnitInfo) {

                }
            }
        }
        binding?.let {
            with(it.unitsRecyclerView) {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = unitListAdapter
            }
        }
        sensorTypesAdapter = SensorTypesAdapter().apply {
            clickImageEventDelegate = object : ClickEventDelegate<SensorType> {
                override fun onClick(item: SensorType) {
                    viewModel.setSearchSensor( item )
                    activity?.supportFragmentManager?.commit {
                        replace(
                            android.R.id.content,
                            ResultSearchFragment.createInstance()
                        )
                        setReorderingAllowed(true)
                        addToBackStack("SearchSensors")
                    }
                }

                override fun onLongClick(item: SensorType) {
                }
            }
        }
        binding?.let {
            with(it.semsorTypesRecyclerView) {
                layoutManager = LinearLayoutManager(
                    context,
                    RecyclerView.VERTICAL,
                    false
                )
                adapter = sensorTypesAdapter
            }
        }
        viewModel.unitsAllLiveData.observe(viewLifecycleOwner) {
            unitListAdapter.setList(it)
        }
    }

    companion object {
        fun createInstance(): SearchSensorFragment {
            return SearchSensorFragment()
        }
    }
}
