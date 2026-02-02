
package vn.io.ao.g0

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import vn.io.ao.g0.databinding.DialogUrlEntryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class UrlEntryDialogFragment : DialogFragment() {

    private var listener: UrlEntryListener? = null

    interface UrlEntryListener {
        fun onUrlEntered(url: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is UrlEntryListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement UrlEntryListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogUrlEntryBinding.inflate(layoutInflater)

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setPositiveButton(R.string.go) { _, _ ->
                val url = binding.urlInput.text.toString()
                listener?.onUrlEntered(url)
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .create()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
