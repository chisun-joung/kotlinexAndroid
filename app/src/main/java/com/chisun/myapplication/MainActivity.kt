package com.chisun.myapplication

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private var allPass : MutableList<Int> = mutableListOf()
    private var toPrint : List<String> = mutableListOf()

    private val sharedPrefFile = "kotlinsharedpreference"
    private lateinit var sharedPreference : SharedPreferences

    private var remainPassCount : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        sharedPreference = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        initData()
        toPrint = getPasswords()
        updateTextView(toPrint)
        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            toPrint = getPasswords()
            updateTextView(toPrint)
        }
    }

    private fun updateTextView(toPrint: List<String>) {
        val passwords = toPrint.reduceIndexed{ index, acc, pass ->
            if (index == 1)
                acc + "\n" + pass + "\n"
            else
                acc + pass + "\n"
        }
        val textView = findViewById<TextView>(R.id.textView)
        val textView2 = findViewById<TextView>(R.id.textView2)
        textView.text = passwords
        textView2.text = remainPassCount.toString()
    }

    private fun initData() {
        // load data from resources android system
        val data = getList()
        if (data.isEmpty()) {
            allPass = (0..9999).toMutableList()
            allPass.shuffle()
            setLists(allPass)
            remainPassCount = allPass.size
        }
        else {
            allPass = data.toMutableList()
            allPass.shuffle()
            remainPassCount = allPass.size
        }
    }

    private fun getPasswords(): List<String> {
        val passwords = allPass.slice(0..9).map{"%04d".format(it)}
        remainPassCount = allPass.size
        allPass.subList(0,10).clear()
        setLists(allPass)
        return passwords
    }

    //saving list in Shared Preference
    private fun setLists(list:List<Int>){
        val gson = Gson()
        val json = gson.toJson(list)//converting list to Json
        val editor = sharedPreference.edit()
        editor.putString("LIST",json)
        editor.commit()
    }
    //getting the list from shared preference
    fun getList():List<Int>{
        val gson = Gson()
        val json = sharedPreference.getString("LIST", null)
        if (json.isNullOrEmpty()) {
            return listOf()
        }
        val type = object : TypeToken<List<Int>>(){}.type//converting the json to list
        return gson.fromJson(json,type)//returning the list
    }
}