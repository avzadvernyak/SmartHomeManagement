package m.kampukter.smarthomemanagement.ui.searchsensor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.UnitView
import m.kampukter.smarthomemanagement.databinding.ResultSearchFragmentBinding
import m.kampukter.smarthomemanagement.ui.ClickEventDelegate
import m.kampukter.smarthomemanagement.ui.RelayInfoFragment
import m.kampukter.smarthomemanagement.ui.SensorInfoFragment
import m.kampukter.smarthomemanagement.ui.SensorListAdapter
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ResultSearchFragment: Fragment() {
    private val viewModel by sharedViewModel<MainViewModel>()
    private var binding: ResultSearchFragmentBinding? = null
    private lateinit var sensorListAdapter: SensorListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = ResultSearchFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding?.resultSearchFragmentToolbar)
        (activity as AppCompatActivity).title = getString(R.string.title_result_search)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        binding?.resultSearchFragmentToolbar?.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        sensorListAdapter = SensorListAdapter().apply {
            clickSensorEventDelegate = object : ClickEventDelegate<UnitView> {
                override fun onClick(item: UnitView) {
                    activity?.supportFragmentManager?.commit {
                        replace(
                            android.R.id.content,
                            SensorInfoFragment.createInstance(item.id)
                        )
                        setReorderingAllowed(true)
                        addToBackStack("ResultSensors")
                    }
                }

                override fun onLongClick(item: UnitView) {
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
                        addToBackStack("ResultSensors")
                    }
                }

                override fun onLongClick(item: UnitView) {
                }
            }

        }
        binding?.let {
            with(it.resultSearchSensorRecyclerView) {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = sensorListAdapter
            }
        }
        viewModel.searchSensorListLiveData.observe(viewLifecycleOwner) { sensors ->
            sensorListAdapter.setList(sensors)
        }
    }
    companion object {
        fun createInstance(): ResultSearchFragment {
            return ResultSearchFragment()
        }
    }
}