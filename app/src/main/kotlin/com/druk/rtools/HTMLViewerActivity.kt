package com.druk.rtools

/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_html_viewer.*

import java.net.URISyntaxException

/**
 * Simple activity that shows the requested HTML page. This utility is
 * purposefully very limited in what it supports, including no network or
 * JavaScript.
 */
class HTMLViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_html_viewer)

        val toolbar = toolbar
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        webview!!.setWebChromeClient(ChromeClient())
        webview!!.setWebViewClient(ViewClient())

        val s = webview!!.settings
        s.useWideViewPort = true
        s.setSupportZoom(true)
        s.builtInZoomControls = true
        s.displayZoomControls = false
        s.savePassword = false
        s.saveFormData = false
        s.blockNetworkLoads = true

        // Javascript is purposely disabled, so that nothing can be
        // automatically run.
        s.javaScriptEnabled = false
        s.defaultTextEncodingName = "utf-8"

        val intent = intent
        if (intent.hasExtra(Intent.EXTRA_TITLE)) {
            title = intent.getStringExtra(Intent.EXTRA_TITLE)
        }

        webview!!.loadUrl(intent.data.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        webview!!.destroy()
    }

    private inner class ChromeClient : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            if (!intent.hasExtra(Intent.EXTRA_TITLE)) {
                this@HTMLViewerActivity.title = title
            }
        }
    }

    private inner class ViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            loading!!.visibility = View.GONE
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            val intent: Intent
            // Perform generic parsing of the URI to turn it into an Intent.
            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
            } catch (ex: URISyntaxException) {
                Log.w(TAG, "Bad URI " + url + ": " + ex.message)
                Toast.makeText(this@HTMLViewerActivity, R.string.cannot_open_link, Toast.LENGTH_SHORT).show()
                return true
            }

            // Sanitize the Intent, ensuring web pages can not bypass browser
            // security (only access to BROWSABLE activities).
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.component = null
            val selector = intent.selector
            if (selector != null) {
                selector.addCategory(Intent.CATEGORY_BROWSABLE)
                selector.component = null
            }

            try {
                view.context.startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Log.w(TAG, "No application can handle " + url)
                Toast.makeText(this@HTMLViewerActivity, R.string.cannot_open_link, Toast.LENGTH_SHORT).show()
            }

            return true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private val TAG = "HTMLViewer"
    }
}
