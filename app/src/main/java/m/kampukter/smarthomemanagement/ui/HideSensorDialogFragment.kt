package m.kampukter.smarthomemanagement.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import m.kampukter.smarthomemanagement.R
import m.kampukter.smarthomemanagement.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HideSensorDialogFragment : DialogFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.dialog_hide_title))
            .setPositiveButton(resources.getString(R.string.dialog_yes)) { dialog, _ ->

                arguments?.getString("ARG_SENSOR_ID")?.let {
                    viewModel.deleteSensorById( it )
                    activity?.supportFragmentManager?.popBackStack()
                }
                dialog.dismiss()
            }
            .create()

    }
    companion object {
        private const val ARG_SENSOR_ID = "ARG_SENSOR_ID"
        fun createInstance(message: String) = HideSensorDialogFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_SENSOR_ID, message)
            }
        }
    }
}