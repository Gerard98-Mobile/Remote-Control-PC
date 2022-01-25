package pl.gg.client

import android.content.SharedPreferences

object Config {

    private var preferences : SharedPreferences? = null

    fun init(preferences: SharedPreferences){
        this.preferences = preferences
    }

    var moveSpeed : Float
        get(){
            return preferences?.getFloat("moveSpeed", 1f) ?: 1f
        }
        set(value){
            with(preferences?.edit()){
                this?.putFloat("moveSpeed", value)
                this?.apply()
            }
        }

    var serverInetAddress : String?
        get(){
            return preferences?.getString("serverInetAddress", "")
        }
        set(value){
            with(preferences?.edit()){
                this?.putString("serverInetAddress", value)
                this?.apply()
            }
        }

}