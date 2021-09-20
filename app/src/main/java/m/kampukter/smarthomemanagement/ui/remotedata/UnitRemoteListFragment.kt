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
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.ResultUnitInfoRemote
import m.kampukter.smarthomemanagement.data.UnitInfoRemote
import m.kampukter.smarthomemanagement.databinding.UnitRemoteListFragmentBinding
import m.kampukter.smarthomemanagement.ui.ClickEventDelegate
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UnitRemoteListFragment : Fragment() {

    private var binding: UnitRemoteListFragmentBinding? = null
    private val viewModel by sharedViewModel<MainViewModel>()

    private lateinit var unitRemoteListAdapter: UnitRemoteListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UnitRemoteListFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding?.unitRemoteListToolbar)
        (activity as AppCompatActivity).title = getString(R.string.title_add_for_view)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        binding?.unitRemoteListToolbar?.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        unitRemoteListAdapter = UnitRemoteListAdapter().apply {
            clickUnitEventDelegate = object : ClickEventDelegate<UnitInfoRemote> {
                override fun onClick(item: UnitInfoRemote) {
                    viewModel.setSelectedUnitRemote(item)
                    activity?.supportFragmentManager?.commit {
                        replace(
                            android.R.id.content,
                            SensorsRemoteListFragment.createInstance(item.id)
                        )
                        setReorderingAllowed(true)
                        addToBackStack("RemoteSensors")
                    }
                }

                override fun onLongClick(item: UnitInfoRemote) {
                }
            }
        }
        binding?.let {
            with(it.unitRemoteRecyclerView) {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = unitRemoteListAdapter
            }
        }
        viewModel.getUnitInfoApi()
        viewModel.unitInfoRemoteLiveData.observe(viewLifecycleOwner) { resultUnitInfo ->
            when (resultUnitInfo) {
                is ResultUnitInfoRemote.Success -> {
                    unitRemoteListAdapter.setList(resultUnitInfo.infoApi)
                }
                is ResultUnitInfoRemote.EmptyResponse -> {

                }
                is ResultUnitInfoRemote.OtherError -> {
                }
            }
        }
        /*viewModel.unitInfoApiLiveData.observe(viewLifecycleOwner) { resultUnitInfo ->
            when (resultUnitInfo) {
                is ResultUnitsInfoApi.Success -> {
                    val unitList = resultUnitInfo.infoApi.units.map {
                        UnitInfoRemote(
                            id = it.name,
                            name = "Name ${it.name}",
                            url = it.url,
                            description = it.description
                        )
                    }
                    unitRemoteListAdapter.setList( unitList )
                }
                is ResultUnitsInfoApi.EmptyResponse -> {
                    Log.d("blabla", "UnitsInfoApi.EmptyResponse")
                }
                is ResultUnitsInfoApi.OtherError -> {
                    Log.d("blabla", "UnitsInfoApi.OtherError")
                }
            }
        }*/
    }

    companion object {
        fun createInstance() = UnitRemoteListFragment()
    }
}
