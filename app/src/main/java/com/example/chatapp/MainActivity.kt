package com.example.chatapp

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        initActionBar()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")
        binding.bSend.setOnClickListener {
            myRef.child(myRef.push().key ?: "null").setValue(User(auth.currentUser?.displayName, binding.eMsg.text.toString()))
        }
        initRcView()
        onChangeListener(myRef)
    }

    private fun initRcView() = with(binding) {
        adapter = UserAdapter()
        rcView.layoutManager = LinearLayoutManager(this@MainActivity)
        rcView.adapter = adapter
        Log.e("MyLog", "RcView inited")
    }

    private fun onChangeListener(dRef: DatabaseReference) {
        dRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val list = ArrayList<User>()
                Log.e("MyLog", "List created")
                for (s in p0.children) {
                    val user = s.getValue(User::class.java)
                    Log.e("MyLog", "Got value")
                    if (user != null) {
                        list.add(user)
                        Log.e("MyLog", "added value")
                    }
                }
                adapter.submitList(list)
                Log.e("MyLog", "list submiyed")
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.bSignOut) {
            auth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initActionBar() {
        val bar = supportActionBar
        Thread {
            runOnUiThread {
                bar?.setDisplayHomeAsUpEnabled(false)
                bar?.setHomeButtonEnabled(false)
                bar?.title = auth.currentUser?.displayName
            }
        }.start()
    }
}