package com.example.motivational.qoutes.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.motivational.qoutes.activities.FullViewActivity
import com.example.motivational.qoutes.activities.MainActivity
import com.example.motivational.qoutes.activities.NewQuoteStudioActivity
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.databinding.FragmentTrendingBinding
import com.example.motivational.qoutes.interfaces.InterfaceUserInterfere
import com.example.motivational.qoutes.utils.UtilLists
import com.example.motivational.qoutes.utils.UtilSharedPerefs

private const val ARG_PARAM1 = "param1"

class TrendingFragment : Fragment() {
    private lateinit var binding: FragmentTrendingBinding
    private var param1: QuotModel? = null
    private lateinit var infc: InterfaceUserInterfere

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTrendingBinding.inflate(inflater, container, false)
        if (activity is MainActivity) {
            infc = activity as InterfaceUserInterfere
        }

        binding.root.setOnTouchListener { view, motionEvent ->
            infc.onInterfere()
            return@setOnTouchListener false
        }
        binding.quotLayout.qoutData.text = param1?.Quote
        binding.quotLayout.qoutWallpaper.setImageResource(UtilLists.wallpapers[param1!!.wall])
        binding.root.setOnClickListener {
//            if (UtilSharedPerefs.getIsFullQuote(requireContext())){
            startActivity(
                Intent(
                    requireActivity(),
                    FullViewActivity::class.java
                ).putExtra("cat", "")
            )
//            }
//            else{
//                startActivity(
//                    Intent(
//                        requireActivity(),
//                        NewQuoteStudioActivity::class.java
//                    ).putExtra("cat", "")
//                )
//            }

        }


        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: QuotModel) =
            TrendingFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }
    }
}