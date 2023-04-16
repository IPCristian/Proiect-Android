package com.example.androidproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidproject.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var mRecyclerView : RecyclerView
    private lateinit var mLostItemAdapter: CustomAdapterForItems
    private lateinit var settingsFragmentBinding : FragmentSettingsBinding
    private val lostItemDatabase by lazy { LostItemDatabase.getDatabase(this.requireContext()).itemDao() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsFragmentBinding = FragmentSettingsBinding.inflate(layoutInflater)

        mRecyclerView = settingsFragmentBinding.recyclerView
        mLostItemAdapter = CustomAdapterForItems(this.requireContext())
        mRecyclerView.adapter = mLostItemAdapter

        if (lostItemDatabase.getLostItems().isEmpty())
        {
            mRecyclerView.visibility = View.GONE
            settingsFragmentBinding.emptyView.visibility = View.VISIBLE
        }
        else
        {
            mRecyclerView.visibility = View.VISIBLE
            settingsFragmentBinding.emptyView.visibility = View.GONE
        }

        settingsFragmentBinding.logoutButton.setOnClickListener {
            (activity as HomeActivity).logOut()
        }

        return settingsFragmentBinding.root
    }
}