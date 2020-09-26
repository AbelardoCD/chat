package com.example.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.example.chat.clases.mensaje
import com.example.chat.clases.user
import kotlinx.android.synthetic.main.item_amigo.view.*
import kotlinx.android.synthetic.main.item_mensaje.view.*
import kotlinx.android.synthetic.main.item_mensaje_emisor.view.*

class adapter_mensaje(
    private val mcontext: Context,
    private val listaMensajes: List<mensaje>,
    private val idContacto: String,
    private val nombrContacto: String,
    private val nombreUsuarioLogeado: String

) :
    ArrayAdapter<mensaje>(mcontext, 0, listaMensajes) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var layout: View
        val validarAdapter = listaMensajes[position]
        if (validarAdapter.idEmisor != idContacto) {
            layout = LayoutInflater.from(context).inflate(com.example.chat.R.layout.item_mensaje_emisor, parent, false)
            val mensaje = listaMensajes[position]

                layout.nombreEmisor.text = nombreUsuarioLogeado
                layout.txtMensajeEmisor.text = mensaje.mensaje

        } else {
            layout = LayoutInflater.from(context).inflate(com.example.chat.R.layout.item_mensaje, parent, false)
            val mensaje = listaMensajes[position]

                layout.nombre.text = nombrContacto
                layout.txtmensaje.text = mensaje.mensaje

        }




        //Glide.with(mcontext).load(user.).circleCrop().into(layout.contactoImg)
        return layout
    }
}