package com.example.motivational.qoutes.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.databinding.DialogGuideClickBinding

class GuidesDialog(context: Context, private val animationName: String, private val data: String) :
    Dialog(context) {

    private lateinit var binding: DialogGuideClickBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogGuideClickBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(true)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.animationView.setAnimation(animationName)
        binding.textView5.text = data
        binding.btnGotIt.setOnClickListener {
            dismiss()
        }

    }
}
