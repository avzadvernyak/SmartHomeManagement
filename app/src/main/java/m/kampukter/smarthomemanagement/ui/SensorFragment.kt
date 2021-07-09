package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.sensor_info_fragment.*
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.data.ResultSensorDataApi
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

private const val KEY_SELECTED_PERIOD = "KEY_SELECTED_PERIOD"

class SensorFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    private var strDateBegin =
        DateFormat.format("yyyy-MM-dd", Date(Date().time - (1000 * 60 * 60 * 24))).toString()
    private var strDateEnd: String = DateFormat.format("yyyy-MM-dd", Date()).toString()

    private lateinit var pickerRange: MaterialDatePicker<Pair<Long, Long>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.sensor_info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        pickerRange =
            parentFragmentManager.findFragmentByTag("Picker") as? MaterialDatePicker<Pair<Long, Long>>
                ?: MaterialDatePicker.Builder.dateRangePicker().build()

        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        savedInstanceState?.let { bundle ->
            bundle.getStringArray(KEY_SELECTED_PERIOD)?.let { saveDate ->
                strDateBegin = saveDate[0]
                strDateEnd = saveDate[1]
            }
        }
        arguments?.getString("ARG_ID_SENSOR")?.let {
            viewModel.setIdSensorForSearch(it)
        }
        viewModel.sensorInformationLiveData.observe(viewLifecycleOwner) { sensor ->

            val sensorFullId = "${sensor?.deviceId}${sensor?.deviceSensorId}"

            (activity as AppCompatActivity).title = getString(R.string.title_history)

            viewModel.setQuestionSensorsData( Triple(sensorFullId, strDateBegin, strDateEnd) )

            pickerRange.addOnPositiveButtonClickListener { dateSelected ->
                dateSelected.first?.let {
                    strDateBegin = DateFormat.format("yyyy-MM-dd", it).toString()
                }
                dateSelected.second?.let {
                    strDateEnd = DateFormat.format("yyyy-MM-dd", it).toString()
                }
                apiProgressBar.visibility = View.VISIBLE
                viewModel.setQuestionSensorsData( Triple(sensorFullId, strDateBegin, strDateEnd) )
            }
        }
        viewModel.sensorDataApi.observe(viewLifecycleOwner){ resultSensorData ->
            when (resultSensorData) {
                is ResultSensorDataApi.Success -> {
                    apiProgressBar.visibility = View.INVISIBLE

                }
                is ResultSensorDataApi.OtherError -> {
                    apiProgressBar.visibility = View.INVISIBLE
                    Log.d("blablabla", "Other Error" + resultSensorData.tError)
                }
                is ResultSensorDataApi.EmptyResponse -> {
                    apiProgressBar.visibility = View.INVISIBLE
                    Snackbar.make(
                        view,
                        getString(R.string.noDataMessage, strDateBegin, strDateEnd),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
        pager.adapter = object : FragmentStateAdapter(this) {

            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> SensorAmountFragment()
                    1 -> SensorDetailedFragment()
                    else -> SensorGraphFragment()
                }
            }
        }
        view.context.let { context ->
            TabLayoutMediator(tab_layout, pager) { tab, position ->
                tab.icon = when (position) {
                    0 -> AppCompatResources.getDrawable(context, R.drawable.ic_info)
                    1 -> AppCompatResources.getDrawable(context, R.drawable.ic_list)
                    else -> AppCompatResources.getDrawable(context, R.drawable.ic_bar_chart)
                }
            }.attach()
        }
        apiProgressBar.visibility = View.VISIBLE

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArray(KEY_SELECTED_PERIOD, arrayOf(strDateBegin, strDateEnd))
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.date_period, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.datePeriod) {
            parentFragmentManager.let { pickerRange.show(it, "Picker") }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val ARG_ID_SENSOR = "ARG_ID_SENSOR"
        fun createInstance(message: String) = SensorFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ID_SENSOR, message)
            }
        }
    }
}