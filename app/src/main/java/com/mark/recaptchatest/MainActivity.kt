package com.mark.recaptchatest

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nchat.im.utils.viewmodel.ViewModelFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * Variant: debugAndroidTest
Config: debug
Store: C:\Users\somfy\.android\debug.keystore
Alias: AndroidDebugKey
MD5: 21:37:23:15:22:E8:65:CA:97:66:39:2E:9D:51:2F:88
SHA1: 89:33:5C:BE:6F:2F:17:75:E6:08:C2:12:02:96:AD:98:35:5A:B0:DD
SHA-256: CB:2A:69:40:15:B1:67:2C:FC:D4:6E:EB:95:0A:3A:A1:F4:D2:AF:B3:4F:72:95:F5:45:EE:9A:78:04:D9:B4:03
Valid until: 2052年10月24日星期四

https://developer.android.com/training/safetynet/attestation

//https://console.cloud.google.com/security/recaptcha/6LcRHQUjAAAAABYhLk_MUUPhmX2uAQ5pN-oSnvQ6/details?project=recaptchatest-368602
//6LcRHQUjAAAAABYhLk_MUUPhmX2uAQ5pN-oSnvQ6
//您的舊版密鑰為 6LcRHQUjAAAAAM9_IDgTHQTgcxaClcT0c36_lKcm


//https://www.google.com/recaptcha/admin/create
//在向使用者顯示的 HTML 程式碼中使用這串網站金鑰
//6Le-QgUjAAAAAAQ4cL-KGjSucaWJY0E0Fqro0KxK

//用這串密鑰來建立網站和 reCAPTCHA 之間的通訊
//6Le-QgUjAAAAAPQu_pxpJ2QK0qhMnCHjlluDe3_W
 */
class MainActivity : AppCompatActivity() {
    private val reCaptchaViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory())[ReCaptchaViewModel::class.java]
    }

    lateinit var executor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        executor = Executors.newCachedThreadPool()
        findViewById<View>(R.id.btn_check).setOnClickListener {
            reCaptchaViewModel.verifyBot(this).observe(this){
                showToast("You're not a Robot")
            }
        }
    }

    fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object{
        const val TAG = "MainActivity"
    }
}