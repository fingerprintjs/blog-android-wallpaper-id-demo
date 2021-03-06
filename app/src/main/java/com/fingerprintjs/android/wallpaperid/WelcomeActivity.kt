package com.fingerprintjs.android.wallpaperid


import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fingerprintjs.android.wallpaperid.wallpaper_id_screen.WallpaperIdActivity


class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val startButton = findViewById<View>(R.id.get_started_button)
        startButton.setOnClickListener { openScreen(WallpaperIdActivity::class.java) }

        val termsButton = findViewById<TextView>(R.id.terms_btn)

        val spannableTermsBtnText = SpannableString(this.getString(R.string.agreement))
        spannableTermsBtnText.setSpan(UnderlineSpan(), 39, 44, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        termsButton.text = spannableTermsBtnText
        termsButton.setOnClickListener { openScreen(TermsActivity::class.java) }

    }

    private fun openScreen(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}