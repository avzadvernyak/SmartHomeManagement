package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.SensorType
import m.kampukter.smarthomemanagement.data.UnitInfo
import m.kampukter.smarthomemanagement.databinding.SearchSensorFragmentBinding
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SearchSensorFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private var binding: SearchSensorFragmentBinding? = null
    private lateinit var unitListAdapter: UnitListAdapter

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
        binding?.type1TypeTextView?.setOnClickListener {
            viewModel.setSearchSensor(SensorType.THERMOMETER)
        }
        viewModel.unitsAllLiveData.observe(viewLifecycleOwner) {
            unitListAdapter.setList(it)
        }
        viewModel.searchSensorListLiveData.observe(viewLifecycleOwner) {
            Log.d("blabla"," Answer $it")
        }
    }

    companion object {
        fun createInstance(): SearchSensorFragment {
            return SearchSensorFragment()
        }
    }
}
