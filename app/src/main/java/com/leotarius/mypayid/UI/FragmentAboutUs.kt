package com.leotarius.mypayid.UI

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.leotarius.mypayid.R
import kotlinx.android.synthetic.main.fragment_about.*

class FragmentAboutUs: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        github.setOnClickListener {
            val url = "https://github.com/aryanchauhan21"
            openURL(url)
        }

        insta.setOnClickListener {
            val url = "https://www.instagram.com/aryanchauhan21/"
            openURL(url)
        }

        linkedin.setOnClickListener {
            val url = "https://www.linkedin.com/in/aryan-chauhan-715157177/"
            openURL(url)
        }

        website.setOnClickListener {
            val url = "https://sites.google.com/view/aryanchauhan/home"
            openURL(url)
        }

    }

    private fun openURL(url: String)
    {   val uri = Uri.parse(url)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}