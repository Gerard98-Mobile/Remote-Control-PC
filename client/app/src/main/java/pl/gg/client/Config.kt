package pl.gg.client

import android.content.SharedPreferences

object Config {

    private var preferences : SharedPreferences? = null

    var moveSpeed = 1f

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