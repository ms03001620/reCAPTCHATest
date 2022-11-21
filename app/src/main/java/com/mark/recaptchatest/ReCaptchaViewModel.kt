package com.mark.recaptchatest

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.safetynet.SafetyNetApi
import com.nchat.im.utils.viewmodel.SingleLiveEvent
import org.json.JSONObject
import java.util.concurrent.Executors

class ReCaptchaViewModel : ViewModel() {
    val verifyNotBotLiveData = SingleLiveEvent<Boolean>()
    private val executor by lazy { Executors.newCachedThreadPool() }

    fun verifyBot(activity: Activity): MutableLiveData<Boolean> {
/*        if (BuildConfig.DEBUG) {
            verifyNotBotLiveData.value = true
            return MutableLiveData(true)
        }*/
        val callback = SingleLiveEvent<Boolean>()
        SafetyNet.getClient(activity).verifyWithRecaptcha(API_SITE_KEY)
            .addOnSuccessListener(executor) { response ->
                if (response.tokenResult?.isNotEmpty() == true) {
                    siteVerify(activity, callback, response)
                    Log.d(TAG, "success")
                }
            }
            .addOnFailureListener(executor) { e ->
                if (e is ApiException) {
                    Log.e(TAG, "Error: ${CommonStatusCodes.getStatusCodeString(e.statusCode)}")
                } else {
                    Log.e(TAG, "Error: ${e.message}")
                }
            }
        return callback
    }

    //https://www.c-sharpcorner.com/article/how-to-integrate-googles-recaptcha-validation-in-android/
    private fun siteVerify(context: Context, callback: SingleLiveEvent<Boolean>, response: SafetyNetApi.RecaptchaTokenResponse) {
        val queue = Volley.newRequestQueue(context)
        val request: StringRequest = object : StringRequest(
            Method.POST, URL,
            Response.Listener<String?> { response ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean("success")) {
                        callback.value = true
                        verifyNotBotLiveData.value = true
                    }
                } catch (ex: Exception) {
                    Log.d(TAG, "Error message: " + ex.message)
                }
            },
            Response.ErrorListener { error -> Log.d(TAG, "Error message: " + error.message) }) {

            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["secret"] = API_SITE_KEY_SERVER
                params["response"] = response.tokenResult.toString()
                return params
            }
        }
        request.setRetryPolicy(
            DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        queue.add(request)
    }

    companion object{
        const val TAG = "ReCaptchaViewModel"
        const val API_SITE_KEY = "6Le-QgUjAAAAAAQ4cL-KGjSucaWJY0E0Fqro0KxK"
        const val API_SITE_KEY_SERVER = "6Le-QgUjAAAAAPQu_pxpJ2QK0qhMnCHjlluDe3_W"
        const val URL = "https://www.google.com/recaptcha/api/siteverify"
    }
}