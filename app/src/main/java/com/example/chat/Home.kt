package com.example.chat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.example.chat.adapter.userAdapter
import com.example.chat.clases.user
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.item_user.*
import kotlinx.android.synthetic.main.item_user.view.*
import java.io.IOException

class Home : AppCompatActivity() {
    lateinit var database: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var idUsuario: String


    lateinit var filpath: Uri
    private var foto: Int = 1
    lateinit var firebaseStorage: StorageReference
    lateinit var storage: FirebaseStorage


    //variable que nos servira para guardar el url de la foto de perfil actual y en caso de actualizar la foto esta direccion
    // se usara para borrar la foto y posterior a eso subir la otra
    lateinit var urlImagDelete: String

    /*Variables para listar los usuarios*/
    lateinit var lista: MutableList<user>
    lateinit var view: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        /*Variables firebase*/
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("Users")

        firebaseStorage = FirebaseStorage.getInstance().getReference()
        storage = Firebase.storage
        urlImagDelete = ""

        /****************************************/
        val valores = intent.extras
        var email = valores?.getString("email")
        idUsuario = ""

        getUserName(email.toString())
        getGaleriatelefono()
        /**********variables lista usuarios**********/
        lista = mutableListOf()
        view = findViewById(R.id.listViewUsuarios)

        getUsuarios()


    }

    private fun getUserName(email: String) {
        println("desde getUser")
        val query: Query = reference.orderByChild("Email").equalTo(email)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                println("desde snap:  " + snapshot)
                for (dato in snapshot.children) {
                    val nombre = dato.getValue(user::class.java)
                    userName.text = nombre?.Nombre
                    idUsuario = nombre?.idUser.toString()

                    println("idUsuario : " + idUsuario)
                    urlImagDelete = nombre?.urlFoto!!
                    //validamos que la url no este basia
                    if (nombre?.urlFoto!!.isNotEmpty()) {
                        Glide.with(this@Home).load(nombre.urlFoto).circleCrop().into(btnImg)

                    }

                }
            }
        })
    }

    private fun getGaleriatelefono() {
        btnImg.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "selecciona una imagen"), foto)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == foto && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filpath = data.data!!
            try {
                val bitmapImg: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filpath)
                btnImg.setImageBitmap(bitmapImg)
                guardarObra()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun guardarObra() {
        progresBarImage.visibility = View.VISIBLE
        println("Entramos a guardar foto")
        try {
            val refer = firebaseStorage.child("fotosUsuarios").child(filpath.lastPathSegment!!)
            val uploadTask = refer.putFile(filpath)

            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                refer.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    println("la url ...." + downloadUri + "    " + idUsuario)
                    /*Si existe alguna foto de perfil la borramos y posteriormente asignamos la url nueva al usuario*/
                    println("urlimgborrar " + urlImagDelete)
                    if (urlImagDelete.isNotEmpty()) {
                        deleteImgFromStorage(urlImagDelete)

                    }
                    guardarUrlUsuarioCorrespondiente(downloadUri!!)
                } else {
                    Toast.makeText(
                        this,
                        "Ocurrio un error al cargar la imagen de perfil, intentelo de nuevo mas tarde",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun guardarUrlUsuarioCorrespondiente(url: Uri) {


        reference.child(idUsuario.toString()).child("urlFoto").setValue(url.toString())
            .addOnCompleteListener {
                Toast.makeText(this, "Foto actualizada correctamente", Toast.LENGTH_LONG).show()
                getUrlFotoById()
                progresBarImage.visibility = View.INVISIBLE


            }
    }

    fun getUrlFotoById() {
        val query: Query = reference.orderByChild("idUser").equalTo(idUsuario)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (dato in snapshot.children) {
                    val usuario = dato.getValue(user::class.java)
                    urlImagDelete = usuario?.urlFoto!!
                    //validamos que la url no este basia
                    if (usuario?.urlFoto!!.isEmpty()) {

                    } else {
                        Glide.with(this@Home).load(usuario.urlFoto).circleCrop().into(btnImg)
                    }
                }

            }

        })
    }

    fun deleteImgFromStorage(url: String) {
        val ref = storage.getReferenceFromUrl(url)
        ref.delete().addOnCompleteListener {
            println("Eliminada")
            // Toast.makeText(this,"Eliminada",Toast.LENGTH_LONG).show()
        }
    }

    private fun getUsuarios() {

        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot!!.exists()) {
                    lista.clear()
                    for (data in snapshot.children) {
                        val objectUser = data.getValue(user::class.java)
                        if (objectUser?.idUser.equals(idUsuario)) {
                        } else {
                            lista.add(objectUser!!)
                        }
                    }
                    val adp = userAdapter(this@Home, lista, idUsuario)
                    view.adapter = adp

                }
            }
        })

    }




}