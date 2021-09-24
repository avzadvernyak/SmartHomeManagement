package m.kampukter.smarthomemanagement.ui.remotedata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import m.kampukter.smarthomemanagement.data.SensorType
import m.kampukter.smarthomemanagement.databinding.SensorTypesFragmentBinding
import m.kampukter.smarthomemanagement.ui.ClickEventDelegate
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChangeSensorTypeFragment : Fragment() {

    private var binding: SensorTypesFragmentBinding? = null
    private val viewModel by sharedViewModel<MainViewModel>()

    private lateinit var sensorTypesAdapter: SensorTypesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SensorTypesFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding?.sensorImageListToolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        binding?.sensorImageListToolbar?.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        sensorTypesAdapter = SensorTypesAdapter().apply {
            clickImageEventDelegate = object : ClickEventDelegate<SensorType> {
                override fun onClick(item: SensorType) {
                    viewModel.setSensorType(item)
                    activity?.supportFragmentManager?.popBackStack()
                }

                override fun onLongClick(item: SensorType) {
                }
            }
        }
        binding?.let {
            with(it.sensorImageRecyclerView) {
                layoutManager = LinearLayoutManager(
                    context,
                    RecyclerView.VERTICAL,
                    false
                )
                adapter = sensorTypesAdapter
            }
        }
    }

    companion object {
        fun createInstance() = ChangeSensorTypeFragment()
    }
}
