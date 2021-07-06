package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.SensorDataApiResult
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {

    private val viewModel by viewModel<MainViewModel>()
    private lateinit var sensorListAdapter: SensorListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorListAdapter = SensorListAdapter()
        with(sensorRecyclerView){
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = sensorListAdapter
        }
        viewModel.sensorListLiveData.observe(viewLifecycleOwner) { sensors ->
            sensorListAdapter.setList(sensors)
        }
        viewModel.apiData.observe(viewLifecycleOwner){
            when(it){
                is SensorDataApiResult.Success -> Log.w("blabla","${it.sensorValue.first().value}")
                else -> Log.w("blabla","$it")
            }

        }
    }

    @ExperimentalCoroutinesApi
    override fun onResume() {
        super.onResume()
        viewModel.connectToDevices()
    }

    @ExperimentalCoroutinesApi
    override fun onPause() {
        super.onPause()
        viewModel.disconnectToDevices()
    }

    companion object {
        fun createInstance(): MainFragment {
            return MainFragment()
        }

    }

}
