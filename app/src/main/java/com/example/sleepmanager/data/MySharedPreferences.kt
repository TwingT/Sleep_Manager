package com.example.sleepmanager.data

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {
    val prefsFilename = "Prefs"
    val prefs: SharedPreferences = context.getSharedPreferences(prefsFilename, Context.MODE_PRIVATE)

    fun getString(key:String, defValue:String):String{
        return prefs.getString(key,defValue).toString()
    }

    fun setString(key:String, defValue: String) {
        prefs.edit().putString(key,defValue).apply()
    }

    fun getInt(key:String, defValue:Int):Int {
        return prefs.getInt(key,defValue).toInt()
    }

    fun setInt(key: String, defValue: Int) {
        prefs.edit().putInt(key,defValue).apply()
    }

    fun getBoolean(key: String, defValue: Boolean):Boolean {
        return prefs.getBoolean(key,defValue)
    }

    fun setBoolean(key: String, defValue: Boolean) {
        prefs.edit().putBoolean(key,defValue).apply()
    }
}

/*
파일 저장 방식

일단 뭘 저장해야 되냐면,
Record, Stage information, timetable_sem, timetable_week, goal

Record : 일단 list of list인데, list 저장이 안되니까 내가 indexing을 해야함
"$n sleepTime", "$n wakeUpTime", "$n feeling"으로 하고
"numberOfRecord"에 list의 크기를 저장

Stage : "stage1", "stage2", "stage3"

Timetable : 이게 은근히 쉬운게 그냥 200짜리 list임
"$n semester", "$n week"

Goal : "goalSleepTime", "goalWakeUpTime"
 */