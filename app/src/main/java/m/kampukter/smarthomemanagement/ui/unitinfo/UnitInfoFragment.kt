package m.kampukter.smarthomemanagement.ui.unitinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.databinding.UnitInfoFragmentBinding
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UnitInfoFragment : Fragment() {

    private var binding: UnitInfoFragmentBinding? = null
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UnitInfoFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding?.unitInfoToolbar)
        (activity as AppCompatActivity).title = getString(R.string.title_unit)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        binding?.unitInfoToolbar?.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        binding?.unitViewPager2?.adapter = object : FragmentStateAdapter(this) {

            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> UnitBasicInfoFragment()
                    1 -> UnitApiInfoFragment()
                    else -> UnitStatusFragment()
                }
            }

        }
        binding?.unitTabLayout?.let { tabLayout ->
            binding?.unitViewPager2?.let { viewPager ->
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = when (position) {
                        0 -> "Общая"
                        1 -> "На сервере"
                        else -> "Состояние"
                    }
                }.attach()
            }
        }
        arguments?.getString("ARG_ID_SENSOR")?.let { sensorId ->

            viewModel.setIdSensorForSearch(sensorId)
            viewModel.sensorInformationLiveData.observe(viewLifecycleOwner) { unit ->
                if (unit != null && unit.id == sensorId) {
                    viewModel.setIdUnitForSearch(unit.unitId)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        arguments?.getString("ARG_ID_SENSOR")?.let { id ->
            id.let { viewModel.connectToUnit(it) }
        }
    }

    override fun onPause() {
        super.onPause()
        arguments?.getString("ARG_ID_SENSOR")?.let { id ->
            id.let { viewModel.disconnectToUnit(it) }
        }
    }


    companion object {
        private const val ARG_ID_SENSOR = "ARG_ID_SENSOR"
        fun createInstance(message: String) = UnitInfoFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ID_SENSOR, message)

            }
        }
    }
}