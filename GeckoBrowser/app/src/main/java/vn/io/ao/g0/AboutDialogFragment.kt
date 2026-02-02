package vn.io.ao.g0

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class AboutDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_about, null)

        builder.setView(view)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }

        return builder.create()
    }
}
