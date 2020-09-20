package com.example.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        callbackManager = CallbackManager.Factory.create()


        login()

    }


    fun login() {
        var id:String =""
        Login.setOnClickListener {
            if (txtNombreLogin.text.isNotEmpty() && txtEmailLogin.text.isNotEmpty() && txtpasswordLogin.text.isNotEmpty()) {
                progressBar2.visibility = View.VISIBLE
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    txtEmailLogin.text.toString(),
                    txtpasswordLogin.text.toString()
                ).addOnCompleteListener {
                    try {
                        if (it.isComplete) {
                            val user = FirebaseAuth.getInstance().currentUser

                            val userDb =
                                user?.uid?.let { it1 ->
                                    id=it1
                                    Firebase.database.getReference().child("Users").child(it1)
                                }
                            println("id "  + id)
                            userDb?.child("idUser")?.setValue(id)
                            userDb?.child("Email")?.setValue(it.result?.user?.email)
                            userDb?.child("Nombre")?.setValue(txtNombreLogin.text.toString())

                            progressBar2.visibility = View.INVISIBLE

                            val intent = Intent(this,Home::class.java).apply {
                                putExtra("email",it.result?.user?.email)
                            }
                            startActivity(intent)
                        }else{
                            alertAutentication()
                        }
                    } catch (error: IOException) {
                        error.printStackTrace()
                    }

                }
            }else{
                alert()
            }
        }
    }




    fun alert(){
        progressBar2.visibility = View.VISIBLE
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Campos incompletos")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun alertAutentication(){
        progressBar2.visibility = View.VISIBLE
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al autenticar el usuario, pruebe de nuevo mas tarde.")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    fun loginFacebook() {

        Login.setOnClickListener {
            println("ENTRAMOS")
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))

            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {

                    override fun onSuccess(result: LoginResult?) {

                        result?.let {
                            val token = it.accessToken
                            val credencial = FacebookAuthProvider.getCredential(token.token)
                            FirebaseAuth.getInstance().signInWithCredential(credencial)
                                .addOnCompleteListener {


                                    if (it.isSuccessful) {

                                    }
                                }

                        }
                    }

                    override fun onCancel() {

                        // ...
                    }

                    override fun onError(error: FacebookException) {

                    }
                })
        }


    }
}