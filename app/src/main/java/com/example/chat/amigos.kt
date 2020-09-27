package com.example.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.bumptech.glide.Glide
import com.example.chat.adapter.adapter_user_solicitud
import com.example.chat.adapter.amigo_adapter
import com.example.chat.clases.user
import com.example.chat.conecccionBD.firebaseInstance
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_amigos.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.btnModuloHomeH
import kotlinx.android.synthetic.main.activity_home.btnModuloNotificacionH
import kotlinx.android.synthetic.main.activity_home.userName
import kotlinx.android.synthetic.main.activity_notifications.*
import kotlinx.android.synthetic.main.item_mensaje.*

class amigos : AppCompatActivity() {
    lateinit var name: String
    lateinit var urlImage: String
    lateinit var idUser: String
    lateinit var email: String
    lateinit var imgUser: ImageButton

    lateinit var lista: MutableList<user>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amigos)
        imgUser = findViewById(R.id.btnImg)
        lista = mutableListOf()
        getIntents()
        accionBotonesModulos()
        getFriends()
        onClicItem()
    }


    private fun getFriends() {
        println("getFriends")

        val coneccion: firebaseInstance = firebaseInstance()
        val referencia = coneccion.getConection("Users")

        referencia.child(idUser).child("amigos").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    for (data in snapshot.children) {
                        val id = data.key
                        println("id..  " + id)
                        referencia.child(id.toString())
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    println(snapshot)
                                    val amigos = snapshot.getValue(user::class.java)
                                    lista.add(amigos!!)
                                    val adp = amigo_adapter(this@amigos, lista)
                                    listViewAmigos.adapter = adp
                                    onClicItem()
                                }

                            })
                    }
                }
            }
        })

    }
    fun onClicItem() {


        listViewAmigos.setOnItemClickListener { adapterView, view, i, l ->
            println("clic" + lista[i].Nombre)
            val intent = Intent(this, mensajes::class.java).apply {
                putExtra("idUser", lista[i].idUser)
                putExtra("nombre", lista[i].Nombre)
                putExtra("url", lista[i].urlFoto)
                putExtra("idUsuarioLogeado", idUser)
                putExtra("nombreUsuarioLogeado",name)
                putExtra("urlImagenUserLogeado",urlImage)
            }
            startActivity(intent)

        }


    }
    fun getIntents() {
        val datosEnviadosEntreVentanas = intent.extras
        name = datosEnviadosEntreVentanas?.getString("name").toString()
        urlImage = datosEnviadosEntreVentanas?.getString("urlImage").toString()
        idUser = datosEnviadosEntreVentanas?.getString("idUser").toString()
        email = datosEnviadosEntreVentanas?.getString("email").toString()

        userName.text = name

        Glide.with(this).load(urlImage).circleCrop().into(imgUser)
    }

    private fun accionBotonesModulos() {

        btnModuloNotificacionH.setOnClickListener {
            val intent = Intent(this, notifications::class.java).apply {
                putExtra("email", email)
                putExtra("name", name)
                putExtra("urlImage", urlImage)
                putExtra("idUser", idUser)
            }
            startActivity(intent)
            onBackPressed()
        }
        btnModuloHomeH.setOnClickListener {
            val intent = Intent(this, Home::class.java).apply {
                putExtra("email", email)
                putExtra("name", name)
                putExtra("urlImage", urlImage)
                putExtra("idUser", idUser)
            }
            startActivity(intent)
            onBackPressed()
        }


    }
}