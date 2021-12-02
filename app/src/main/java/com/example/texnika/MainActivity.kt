package com.example.texnika

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    var cardView1: CardView? = null
    var cardView2: CardView? = null
    var btnScane: MaterialButton? = null
    var btnEnterCode: MaterialButton? = null
    var btnEnter: MaterialButton? = null
    var edtCode: EditText? = null
    var tvText: TextView? = null
    var hide: Animation? = null
    var reveal: Animation? = null
    var chrome: MaterialButton? = null
    var wepView: WebView? = null

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        cardView1 = findViewById(R.id.catdView1)
        cardView2 = findViewById(R.id.catdView2)
        btnEnterCode = findViewById(R.id.btnEnterCode)
        btnEnter = findViewById(R.id.btnEnter)
        chrome = findViewById(R.id.btnWep)
        btnScane = findViewById(R.id.btnScan)
        edtCode = findViewById(R.id.edtCode)
        tvText = findViewById(R.id.tvTitle)
        wepView = findViewById(R.id.webView)


        hide = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
        reveal = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)

        tvText!!.startAnimation(reveal)
        cardView2!!.startAnimation(reveal)
        tvText!!.setText(getString(R.string.scaner_qr_code_here))
        cardView2!!.visibility = View.VISIBLE

        tillar.setOnClickListener {
            val fragment = ChangeLanguageFragment.newInstance()
            fragment.show(supportFragmentManager, fragment.tag)
        }

        btnScane!!.setOnClickListener {
            tvText!!.startAnimation(reveal)
            cardView1!!.startAnimation(hide)
            cardView2!!.startAnimation(reveal)
            cardView2!!.visibility = View.VISIBLE
            cardView1!!.visibility = View.GONE
            tvText!!.setText(getString(R.string.scaner_qr_code_here))


        }

        cardView2!!.setOnClickListener {
            cameraTask()
        }

        btnEnterCode!!.setOnClickListener {
            tvText!!.startAnimation(reveal)
            cardView1!!.startAnimation(reveal)
            cardView2!!.startAnimation(hide)
            cardView2!!.visibility = View.GONE
            cardView1!!.visibility = View.VISIBLE
            tvText!!.setText(getString(R.string.enter_qr_code_here))
        }

        btnEnter!!.setOnClickListener {

            val s = edtCode!!.text.toString()

            val shareIntent = Intent();
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, s)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "QR Scanner")
            startActivity(Intent.createChooser(shareIntent, "Share text via"))


        }

        chrome!!.setOnClickListener {
            val s = edtCode!!.text.toString()
//            wepView!!.webViewClient = WebViewClient()
//            wepView!!.loadUrl(s)
//            val webSettings = webView.settings
//            webSettings.javaScriptEnabled = true
            wepView!!.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?,url: String?): Boolean {
                    if (url != null) {
                        view?.loadUrl(url)
                    }
                    return true
                }
            }
            wepView!!.loadUrl(s)
            wepView!!.settings.javaScriptEnabled = true
            wepView!!.settings.allowContentAccess = true
            wepView!!.settings.domStorageEnabled = true
            wepView!!.settings.useWideViewPort = true
            wepView!!.settings.setAppCacheEnabled(true)
            wepView!!.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.canGoBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun hasCameraAccess(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)
    }

    private fun cameraTask() {
        if (hasCameraAccess()) {

            var qrScaner = IntentIntegrator(this)
            qrScaner.setPrompt(getString(R.string.scaner_qr_code_here))
            qrScaner.setCameraId(0)
            qrScaner.setOrientationLocked(true)
            qrScaner.setBeepEnabled(true)
            qrScaner.captureActivity = CaptureActivity::class.java
            qrScaner.initiateScan()
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.ruxsat_sorash),
                123,
                Manifest.permission.CAMERA
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, getString(R.string.jatija_eslatma), Toast.LENGTH_SHORT).show()
                edtCode!!.setText("")
            } else {
                try {
                    cardView1!!.startAnimation(hide)
                    cardView2!!.startAnimation(reveal)
                    cardView2!!.visibility = View.GONE
                    cardView1!!.visibility = View.VISIBLE
                    edtCode!!.setText(result.contents.toString())
                } catch (exception: JSONException) {
                    Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                    edtCode!!.setText("")
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Toast.makeText(this, getString(R.string.ruxsat_berilgan), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        TODO("Not yet implemented")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
        TODO("Not yet implemented")
    }

    override fun onRationaleDenied(requestCode: Int) {
        TODO("Not yet implemented")
    }
//
//    override fun attachBaseContext(newBase: Context?) {
//        super.attachBaseContext(LocaleManager.setLocale(newBase))
//    }


}