package com.leotarius.mypayid.UI

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.leotarius.mypayid.Models.PaymentMethod
import com.leotarius.mypayid.R
import com.leotarius.mypayid.UI.Home.FragmentDashboard
import com.leotarius.mypayid.UI.Home.FragmentSearch
import kotlinx.android.synthetic.main.fragment_home.*


class FragmentHome : Fragment() {

    private val TAG = "FragmentHome"
    private lateinit var homeViewModel: HomeViewModel

    companion object{
        public var queryType = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        childFragmentManager.beginTransaction().replace(R.id.home_fragment_container, FragmentDashboard()).commit()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigation.setOnNavigationItemSelectedListener(listener)

        plus.setOnClickListener {
            if(bottomNavigation.selectedItemId == R.id.dashboard){
                dialogShow()
            } else{
                // pop up search filters
                filterDialogShow()
            }
        }
    }

    private val listener = BottomNavigationView.OnNavigationItemSelectedListener {
        when(it.itemId){
            R.id.dashboard -> {
                childFragmentManager.beginTransaction().replace(
                    R.id.home_fragment_container,
                    FragmentDashboard()
                ).commit()
                plus.setImageResource(R.drawable.ic_add)
                return@OnNavigationItemSelectedListener true
            }

            R.id.search -> {
                childFragmentManager.beginTransaction().replace(
                    R.id.home_fragment_container,
                    FragmentSearch()
                ).commit()
                plus.setImageResource(R.drawable.ic_filter)
                return@OnNavigationItemSelectedListener true
            }

            else -> {
                return@OnNavigationItemSelectedListener false
            }
        }
    }

    private fun filterDialogShow() {
        val inflater = LayoutInflater.from(activity)
        val dialogView = inflater.inflate(R.layout.dialog_search_filter, null)
        val mDialog = activity?.let { AlertDialog.Builder(it).create() }
        mDialog?.setView(dialogView)
        mDialog?.setCancelable(true)

        val options = arrayOf("Username", "Phone", "Email")
        val arrayAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item_layout, options)
        dialogView.findViewById<AutoCompleteTextView>(R.id.dropdown).setAdapter(arrayAdapter)
        dialogView.findViewById<AutoCompleteTextView>(R.id.dropdown).keyListener = null
        mDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        // setting current selected option
        dialogView.findViewById<TextView>(R.id.selected_text).text = "Selected filter : ${options[queryType]}"

        // If option is selected than dismiss dialog

        dialogView.findViewById<AutoCompleteTextView>(R.id.dropdown).onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val type = dialogView.findViewById<AutoCompleteTextView>(R.id.dropdown).text.toString()
                queryType = options.indexOf(type)
                dialogView.findViewById<TextView>(R.id.selected_text).text = "Selected filter : ${options[queryType]}"
            }

        }

        dialogView.findViewById<Button>(R.id.done).setOnClickListener {
            mDialog?.dismiss()
        }

        mDialog?.show()
    }

    private fun dialogShow(){
        val inflater = LayoutInflater.from(activity)
        val dialogView = inflater.inflate(R.layout.dialog_add_pay_method, null)
        val mDialog = activity?.let { AlertDialog.Builder(it).create() }
        mDialog?.setView(dialogView)
        mDialog?.setCancelable(true)

        val arr = arrayOf("PayTm", "PhonePe", "Google Pay", "BHIM")
        val arrayAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item_layout, arr)
        dialogView.findViewById<AutoCompleteTextView>(R.id.filled_exposed_dropdown).setAdapter(arrayAdapter)

        dialogView.findViewById<AutoCompleteTextView>(R.id.filled_exposed_dropdown).keyListener = null
        mDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val errorPhone = dialogView.findViewById<TextView>(R.id.error_phone)
        val errorUsername = dialogView.findViewById<TextView>(R.id.error_username)
        val errorProvider = dialogView.findViewById<TextView>(R.id.error_provider)
        val error = dialogView.findViewById<TextView>(R.id.error)
        val progress = dialogView.findViewById<ProgressBar>(R.id.progress)
        val done = dialogView.findViewById<Button>(R.id.done)
        val cancel_action = dialogView.findViewById<Button>(R.id.cancel_action)
        val add_action = dialogView.findViewById<Button>(R.id.add_action)


        add_action.setOnClickListener {

            val provider = dialogView.findViewById<AutoCompleteTextView>(R.id.filled_exposed_dropdown).text.toString()
            val username = dialogView.findViewById<TextInputEditText>(R.id.username).text.toString()
            val phone = dialogView.findViewById<TextInputEditText>(R.id.mobile_number).text.toString()

            if(provider.isNotEmpty() and username.isNotEmpty() and (phone.length == 10)){

                // hide errors
                errorPhone.visibility = View.GONE
                errorProvider.visibility = View.GONE
                errorUsername.visibility = View.GONE

                // Everything is good, push data to firebase
                Log.d(TAG, "dialogShow: Good good.")
                // Make a payment method with entered data
                val paymentMethod = PaymentMethod(provider, "+91$phone", username)
                // Add user to database
                homeViewModel.addMethodToDatabase(paymentMethod)
                // Now we have made the call

                // hide cancel and add buttons
                cancel_action.visibility = View.INVISIBLE
                add_action.visibility = View.INVISIBLE

                // observe the callback and cancel the progress bar when its done
                homeViewModel.isAdded.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        1 -> {
                            progress.visibility = View.GONE
                            done.visibility = View.VISIBLE
                            done.setOnClickListener { mDialog?.dismiss() }
                            Log.d(TAG, "dialogShow: Payment method added successfully")
                        }

                        0 -> {
                            // show the progress bar
                            progress.visibility = View.VISIBLE
                            Log.d(TAG, "dialogShow: No callback received yet")
                        }

                        -1 -> {
                            progress.visibility = View.GONE
                            error.visibility = View.VISIBLE
                            Log.d(TAG, "dialogShow: operation failed")
                        }
                    }
                })

            } else {
                if(provider.isEmpty()) errorProvider.visibility = View.VISIBLE
                else errorProvider.visibility = View.GONE

                if(username.isEmpty()) errorUsername.visibility = View.VISIBLE
                else errorUsername.visibility = View.GONE

                if(phone.length!=10) errorPhone.visibility = View.VISIBLE
                else errorPhone.visibility = View.GONE
            }
        }

        cancel_action.setOnClickListener {
            mDialog?.dismiss()
        }

        mDialog?.show()
    }
}