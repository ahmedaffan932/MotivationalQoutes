package com.example.motivational.qoutes.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.databinding.BottomSheetOptionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialog(val contextd: Context, val model: QuotModel): BottomSheetDialogFragment()  {
    private lateinit var binding:BottomSheetOptionsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= BottomSheetOptionsBinding.inflate(inflater,container,false)
        binding.btnSetWallpaper.setOnClickListener {

        }
        binding.btnDownload.setOnClickListener {

        }
        binding.btnPlaySound.setOnClickListener {

        }
        binding.btnMoreOpts.setOnClickListener {

        }
        binding.btnRed.setOnClickListener {

        }
        binding.btnBlack.setOnClickListener {

        }
        binding.btnBlue.setOnClickListener {

        }
        binding.btnGreen.setOnClickListener {

        }
        binding.btnSparrow.setOnClickListener {

        }


        return binding.root


    }


}