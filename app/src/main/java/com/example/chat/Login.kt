package com.example.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.chat.Login
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class Login : AppCompatActivity() {

    lateinit var preferenceM:userPreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        preferenceM = userPreferenceManager(this)
        validarLoginLocalORemoto()
        ventanaMain()


        login()
    }


    private fun validarLoginLocalORemoto(){
        if(obtenerDatosUserLocal() !=false){
            // progresBar.visibility = View.INVISIBLE
            val intent = Intent(this, Home::class.java).apply {
                putExtra("email", preferenceM.getEmail())
            }
            startActivity(intent)
        }else{
            login()
        }
    }
    private fun obtenerDatosUserLocal():Boolean{

        val userPreferenceEmail = preferenceM.getEmail()
        val userpreferencePassword = preferenceM.getPassword()

        println("Password user logeado Email   " + userPreferenceEmail)
        println("Password user logeado Password  " + userpreferencePassword)
        if(userPreferenceEmail !="" && userpreferencePassword !=""){
            return true

        }else{
            return false
        }
    }
    fun login() {

        LoginLog.setOnClickListener {
            if (txtEmailLoginLog.text.isNotEmpty() && txtpasswordLoginLog.text.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE;

                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    txtEmailLoginLog.text.toString(),
                    txtpasswordLoginLog.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        preferenceM.guaradarCredenciales( txtEmailLoginLog.text.toString(),txtpasswordLoginLog.text.toString())
                        Home(it?.result?.user?.email.toString())
                    } else {
                        alertAutentication()
                    }
                }
            } else {
                alert()
            }
        }

    }

    fun Home(email: String) {
        progressBar.visibility = View.INVISIBLE
        val intent = Intent(this, Home::class.java).apply {
            putExtra("email", email)
        }
        startActivity(intent)
    }

    fun ventanaMain() {
        btnIrLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun alert() {
        progressBar.visibility = View.INVISIBLE
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Campos incompletos")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun alertAutentication() {
        progressBar.visibility = View.INVISIBLE
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al autenticar el usuario, pruebe de nuevo mas tarde.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}