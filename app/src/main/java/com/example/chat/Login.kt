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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

      ventanaMain()
        login()
    }


    fun login(){

        LoginLog.setOnClickListener {
            if(txtEmailLoginLog.text.isNotEmpty() && txtpasswordLoginLog.text.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE;

                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    txtEmailLoginLog.text.toString(),
                    txtpasswordLoginLog.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Home(it?.result?.user?.email.toString())
                    } else {
                        alertAutentication()
                    }
                }
            }else{
                alert()
            }
        }

    }

    fun Home(email:String){
        progressBar.visibility =View.INVISIBLE
        val intent = Intent(this,Home::class.java).apply {
            putExtra("email",email)
        }
        startActivity(intent)
    }

    fun ventanaMain(){
        btnIrLogin.setOnClickListener {
            val intent =Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun alert(){
        progressBar.visibility =View.INVISIBLE
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Campos incompletos")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun alertAutentication(){
        progressBar.visibility =View.INVISIBLE
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al autenticar el usuario, pruebe de nuevo mas tarde.")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}