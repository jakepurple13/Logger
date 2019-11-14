package com.programmersbox.logger

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.programmersbox.loged.BiometricBuilder
import com.vanniktech.textbuilder.FormableOptions
import com.vanniktech.textbuilder.FormableText
import com.vanniktech.textbuilder.TextBuilder
import io.kimo.konamicode.KonamiCode
import io.kimo.konamicode.KonamiCodeLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private fun FormableText.format(textToFormat: String, block: FormableOptions.() -> Unit) = format(textToFormat).apply(block).done()

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        KonamiCode.Installer(this)
            .on(this)
            .callback {
                Toast.makeText(this, "Hello World", Toast.LENGTH_SHORT).show()
            }.install {
                +KonamiCodeLayout.Direction.UP
                +KonamiCodeLayout.Button.START
                KonamiCodeLayout.Direction.DOWN.add()
                KonamiCodeLayout.Button.A.add()
            }

        TextBuilder(this)
            .addText("Good Bye")
            .addFormableText("Hello World")
            .format("Hello") {
                bold()
                underline()
            }

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
                        negativeButton = null
                        confirmationRequired = true
                        deviceCredentialAllowed = true
                    }
                }
            }
        }
    }
}
