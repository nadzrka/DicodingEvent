package com.nadzirakarimantika.dicodingevent.ui

import androidx.fragment.app.Fragment
import com.nadzirakarimantika.dicodingevent.utils.NetworkUtils

abstract class BaseFragment : Fragment() {

    fun checkInternetConnection() {
        if (!NetworkUtils.isConnectedToInternet(requireContext())) {
            NetworkUtils.showNoInternetToast(requireContext())
        }
    }
}
