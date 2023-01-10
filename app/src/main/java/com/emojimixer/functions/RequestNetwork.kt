package com.emojimixer.functions

import android.app.Activity

class RequestNetwork(val activity: Activity) {
    var params = HashMap<String, Any>()
        private set
    var headers = HashMap<String, Any>()
    var requestType = 0
        private set

    fun startRequestNetwork(
        method: String?,
        url: String?,
        tag: String?,
        requestListener: RequestListener?
    ) {
        RequestNetworkController.getInstance().execute(this, method, url, tag, requestListener)
    }

    interface RequestListener {
        fun onResponse(tag: String?, response: String?, responseHeaders: HashMap<String?, Any?>?)
        fun onErrorResponse(tag: String?, message: String?)
    }
}