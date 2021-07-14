package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.unit_list_fragment.*
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.UnitInfoView
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UnitListFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    private lateinit var unitListAdapter: UnitListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.unit_list_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(unitListToolbar)
        (activity as AppCompatActivity).title = getString(R.string.title_units)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        unitListToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        unitListAdapter = UnitListAdapter().apply {
            clickUnitEventDelegate = object : ClickEventDelegate<UnitInfoView> {
                override fun onClick(item: UnitInfoView) {

                    activity?.supportFragmentManager?.commit {
                        replace(
                            android.R.id.content,
                            UnitInfoFragment.createInstance(item.id)
                        )
                        setReorderingAllowed(true)
                        addToBackStack("Unitss")
                    }
                }

                override fun onLongClick(item: UnitInfoView) {
                }
            }
        }
        with(unitRecyclerView) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = unitListAdapter
        }
        viewModel.unitListLiveData.observe(viewLifecycleOwner) { unit ->
            unitListAdapter.setList(unit)
        }
    }

    companion object {
        fun createInstance() = UnitListFragment()
    }
}
