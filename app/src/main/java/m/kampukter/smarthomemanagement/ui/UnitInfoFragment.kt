package m.kampukter.smarthomemanagement.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.unit_info_fragment.*
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UnitInfoFragment : Fragment() {
    private val viewModel by sharedViewModel<MainViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.unit_info_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(unitInfoToolbar)
        (activity as AppCompatActivity).title = getString(R.string.title_unit)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        unitInfoToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        arguments?.getString("ARG_ID_UNIT")?.let {
            viewModel.setIdUnitForSearch(it)
        }
        viewModel.unitLiveData.observe(viewLifecycleOwner){
            unitNameTextView.text = it?.deviceName
        }
        /*when (result.wsConnectionStatus) {
            is WSConnectionStatus.Connected -> linkOnImageView.visibility = View.VISIBLE
             is WSConnectionStatus.Disconnected -> "Disconnected"
             is WSConnectionStatus.Failed -> "Failed ${(result.wsConnectionStatus as WSConnectionStatus.Failed).reason}"
             is WSConnectionStatus.Connecting -> "Connecting"
            else -> linkOffImageView.visibility = View.VISIBLE
        }*/

    }

    companion object {
        private const val ARG_ID_UNIT = "ARG_ID_UNIT"
        fun createInstance(message: String) = UnitInfoFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ID_UNIT, message)
            }
        }
    }
}