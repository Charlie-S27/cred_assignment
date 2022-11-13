package com.example.cred.model

import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.cred.MyResponse
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class NetworkCalls constructor( private val client: OkHttpClient = OkHttpClient()){

    public var lambda : ((Boolean) -> Unit)? = null

    @RequiresApi(Build.VERSION_CODES.N)
    fun makeApiCall(success: Boolean) {
        val url =
            if (success) "https://api.mocklets.com/p68348/success_case" else "https://api.mocklets.com/p68348/failure_case"
        val request = Request.Builder()
            .url(url)
            .build()
        val call = client.newCall(request)

        Handler().postDelayed(({
            Thread(Runnable {
                val rawResponse = call.execute()
                Log.d("raw: ", rawResponse.toString())
                val gson = Gson()
                val response = gson.fromJson(rawResponse.body()!!.string(), MyResponse::class.java)
                Log.d("response: ", response.success.toString())
                this.lambda!!(response.success)
             //   handleApiResponse(response.success)
            }).start()
        }), 5000)

    }

//    private fun handleApiResponse(success: Boolean) {
//        if (success) {
//            lambda("Sucess")
//            runOnUiThread() {
//                cardView.textMsg.text = "Success"
//            }
//        } else (
//                lambda("Failure")
//                runOnUiThread() {
//            cardView.textMsg.text = "Failure"
//        })
//        val loadingAnim = findViewById<LoadingAnimation>(R.id.loadingAnim);
//        loadingAnim.visibility = View.INVISIBLE
//    }
}