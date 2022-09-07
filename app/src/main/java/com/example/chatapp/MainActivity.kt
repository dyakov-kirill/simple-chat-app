package com.example.chatapp

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        initActionBar()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")
        binding.bSend.setOnClickListener {
            myRef.setValue(binding.eMsg.text.toString())
        }
        onChangeListener(myRef)
    }

    private fun onChangeListener(dRef: DatabaseReference) {
        dRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                binding.apply {
                    tvMsg.append("\n")
                    tvMsg.append(p0.value.toString())
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }

    private fun initActionBar() {
        val bar = supportActionBar
        Thread {
            val bMap = Picasso.get().load(auth.currentUser?.photoUrl).get()
            val dIcon = BitmapDrawable(resources, bMap)
            runOnUiThread {
                bar?.setDisplayHomeAsUpEnabled(true)
                bar?.setHomeAsUpIndicator(dIcon)
                bar?.title = auth.currentUser?.displayName
            }
        }.start()
    }
}