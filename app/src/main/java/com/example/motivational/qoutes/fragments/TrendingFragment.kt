package com.example.motivational.qoutes.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.motivational.qoutes.activities.NewQuoteStudioActivity
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.databinding.FragmentTrendingBinding
import com.example.motivational.qoutes.utils.UtilLists

private const val ARG_PARAM1 = "param1"
class TrendingFragment : Fragment() {
    private lateinit var binding: FragmentTrendingBinding
    private var param1: QuotModel? = null

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
        binding=FragmentTrendingBinding.inflate(inflater,container,false)

        binding.quotLayout.qoutData.text=param1?.Quote
            binding.quotLayout.qoutWallpaper.setImageResource(UtilLists.getRandomWallpaper())
            binding.root.setOnClickListener {
                startActivity(
                    Intent(
                        requireActivity(),
                        NewQuoteStudioActivity::class.java
                    ).putExtra("cat", "")
                )
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