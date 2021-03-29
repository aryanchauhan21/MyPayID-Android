package com.leotarius.mypayid.UI.Home

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leotarius.mypayid.MainActivity
import com.leotarius.mypayid.R
import com.leotarius.mypayid.UI.FragmentHome
import kotlinx.android.synthetic.main.fragment_search.*


class FragmentSearch : Fragment() {
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var searchViewModel: SearchViewModel

    private val TAG = "FragmentSearch"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        return inflater.inflate(R.layout.fragment_search, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up recycler view
        viewManager = LinearLayoutManager(context)

        search.setOnClickListener {
            // Now make request to get data from database
            makeSearchUsingQuery()
            context?.let { it1 -> MainActivity.hideKeyboardFrom(it1, search) }
        }

        query.setOnKeyListener(View.OnKeyListener { v, keyCode, event -> // If the event is a key-down event on the "enter" button
            if (event.action === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Perform action on key press
                Log.d(TAG, "onViewCreated: called")
                makeSearchUsingQuery()
                return@OnKeyListener true
            }
            false
        })

        // observe data from firebase here and update the method list in adapter
        searchViewModel.methodList.observe(viewLifecycleOwner){
            // set up recycler view which is hidden now
            // we will show it when we will have something
            viewAdapter = SearchResponseListAdapter(context, viewLifecycleOwner, it)
            searchRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }

            if(it.size == 0){
                loading_search.visibility = View.GONE
                searchRecyclerView.visibility = View.GONE
                empty_search.visibility = View.VISIBLE
            } else {
                loading_search.visibility = View.GONE
                searchRecyclerView.visibility = View.VISIBLE
                empty_search.visibility = View.GONE
            }

        }
    }

    private fun makeSearchUsingQuery(){
        // queryType = 0 (name), 1 (phone), 2 (email)
        searchViewModel.getMethodList(FragmentHome.queryType, query.text.toString())
        loading_search.visibility = View.VISIBLE
        searchRecyclerView.visibility = View.GONE
        empty_search.visibility = View.GONE
    }

}