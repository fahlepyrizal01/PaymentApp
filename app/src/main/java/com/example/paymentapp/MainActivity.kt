package com.example.paymentapp

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.MenuItem
import com.example.paymentapp.fragment.HistoryTransactionFragment
import com.example.paymentapp.fragment.ProfileUserFragment
import com.example.paymentapp.fragment.TransactionFragment

class MainActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    lateinit var context: Context
    private lateinit var navigation : BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initiationWidget()
    }

    private fun initiationWidget(){

        context = this@MainActivity

        navigation = findViewById(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(this)

        changeFragment(HistoryTransactionFragment.newInstance())

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigationHistory -> {
                changeFragment(HistoryTransactionFragment.newInstance())
                return true
            }
            R.id.navigationTransaction -> {
                changeFragment(TransactionFragment.newInstance())
                return  true
            }
            R.id.navigationProfile -> {
                changeFragment(ProfileUserFragment.newInstance())
                return true
            }
        }
        item.isChecked = true
        return false
    }

    private fun changeFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFrame, fragment)
        fragmentTransaction.commit()
    }
}

