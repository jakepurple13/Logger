package com.programmersbox.logger

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.programmersbox.loged.BiometricBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                BiometricBuilder.biometricBuilder(this) {

                    authSuccess {
                        button.text = "Success"
                    }

                    authError { _, _ ->
                        button.text = "Error"
                    }

                    authFailed {
                        button.text = "Failed"
                    }

                    error {
                        Log.d("Tag", "${it.num}: ${it.reason}")
                    }

                    promptInfo {
                        title = "Testing"
                        subtitle = "Tester"
                        description = "Test"
                        confirmationRequired = true
                        deviceCredentialAllowed = true
                    }
                }
            }
        }
    }
}
