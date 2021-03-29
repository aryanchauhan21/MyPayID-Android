package com.leotarius.mypayid.UI.Home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leotarius.mypayid.R
import kotlinx.android.synthetic.main.fragment_dashboard.*

class FragmentDashboard() : Fragment() {
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    companion object{
        lateinit var dashboardViewModel: DashboardViewModel
    }

    private val TAG = "FragmentDashboard"
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up recycler view
        viewManager = LinearLayoutManager(context)

        // Now make request to get data from database
        dashboardViewModel.getMethodList()

        // observe data from firebase here and update the method list in adapter
        dashboardViewModel.methodList.observe(viewLifecycleOwner){
            // set up recycler view which is hidden now
            // we will show it when we will have something
            viewAdapter = PaymentMethodListAdapter(context, viewLifecycleOwner, it)
            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }

            if(it.size == 0){
                loading.visibility = View.GONE
                recyclerView.visibility = View.GONE
                empty.visibility = View.VISIBLE
            } else {
                loading.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }

}