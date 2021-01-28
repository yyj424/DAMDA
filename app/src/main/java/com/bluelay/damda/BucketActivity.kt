package com.bluelay.damda

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

class BucketActivity : AppCompatActivity() {
    data class DtoBucket(var content: String, var checked: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
}