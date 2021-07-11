package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.main_fragment.*
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.UnitView
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

        sensorListAdapter = SensorListAdapter().apply {
            clickSensorEventDelegate = object : ClickEventDelegate<UnitView> {
                override fun onClick(item: UnitView) {

                    activity?.supportFragmentManager?.commit {
                        replace(
                            android.R.id.content,
                            SensorFragment.createInstance((item as UnitView.SensorView).id)
                        )
                        setReorderingAllowed(true)
                        addToBackStack("Sensors")
                    }
                }

                override fun onLongClick(item: UnitView) {
                    Log.d("blabla", "Long click ${(item as UnitView.SensorView).id}")
                }
            }
            clickRelayEventDelegate = object : ClickEventDelegate<UnitView> {
                override fun onClick(item: UnitView) {
                    viewModel.sendCommandToRelay((item as UnitView.RelayView))
                }

                override fun onLongClick(item: UnitView) {
                    Log.d("blabla", "Long click ${(item as UnitView.RelayView).id}")
                }
            }

        }
        with(sensorRecyclerView) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = sensorListAdapter
        }
        viewModel.sensorListLiveData.observe(viewLifecycleOwner) { sensors ->
            sensorListAdapter.setList(sensors)
        }
       /* viewModel.relayInformation.observe(viewLifecycleOwner) { info ->
            sensorListAdapter.getRelayEventDelegate(clickRelayEventDelegate)
            viewModel.sendCommand( info )
        }*/

    }

    override fun onResume() {
        super.onResume()
        viewModel.connectToDevices()
    }

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
