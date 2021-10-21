package com.example.runningapp.ui.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.runningapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.annotations.Until

class CancelTrackingDialog:DialogFragment() {

    private var yesListener:(()->Unit)?=null
    fun setYesListener(listener:()->Unit){
        yesListener=listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return  MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel The Run?")
            .setMessage("Are you sure to cancel the current run and delete all this data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes") { _, _ ->
                yesListener?.let { yes->
                    yes()
                }

            }.setNegativeButton("No") { dialogInterface, i ->
                dialogInterface.cancel()
            }.create()


    }

}