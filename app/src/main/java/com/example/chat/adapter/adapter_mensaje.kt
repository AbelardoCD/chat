package com.example.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.example.chat.clases.mensaje
import com.example.chat.clases.user
import com.example.chat.mensajes
import kotlinx.android.synthetic.main.item_amigo.view.*
import kotlinx.android.synthetic.main.item_mensaje.view.*
import kotlinx.android.synthetic.main.item_mensaje_emisor.view.*
import java.text.SimpleDateFormat

class adapter_mensaje(
    private val mcontext: Context,
    private val listaMensajes: List<mensaje>,
    private val idContacto: String,
    private val nombrContacto: String,
    private val nombreUsuarioLogeado: String,
    private val urlcontacto:String,
    private val urlImageUsuarioLogeado:String

) :
    ArrayAdapter<mensaje>(mcontext, 0, listaMensajes) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var layout: View
        val validarAdapter = listaMensajes[position]
        if (validarAdapter.idEmisor != idContacto) {
            layout = LayoutInflater.from(context).inflate(com.example.chat.R.layout.item_mensaje_emisor, parent, false)
            val mensaje = listaMensajes[position]


            val hora = mensaje.hora.split(" ")
            val fecha = hora[0]+hora[1]+hora[2]
            var dia =""


           if (fecha==getTime()){
               dia="Hoy"
           }else if(hora[1] ==getTimeDia()){
               dia ="Ayer"
           }else{
               dia=hora[1]
           }

            val horaSpliteada =  dia +" "+ hora[3] +" "+ hora[4] + hora[5]

                layout.horaemisor.text = horaSpliteada
                layout.nombreEmisor.text = nombreUsuarioLogeado
                layout.txtMensajeEmisor.text = mensaje.mensaje
                Glide.with(mcontext).load(urlImageUsuarioLogeado).circleCrop().into(layout.img_userLogeado)

        } else {
            layout = LayoutInflater.from(context).inflate(com.example.chat.R.layout.item_mensaje, parent, false)
            val mensaje = listaMensajes[position]
            val hora = mensaje.hora.split(" ")
            val fecha = hora[0]+hora[1]+hora[2]
            var dia =""
            if (fecha==getTime()){
                dia="Hoy"
            }else if(hora[1] ==getTimeDia()){
                dia ="Ayer"
            }else{
                dia=hora[1]
            }
            val horaSpliteada =  dia +" "+ hora[3] +" "+ hora[4] + hora[5]

            layout.hora.text = horaSpliteada
                layout.nombre.text = nombrContacto
                layout.txtmensaje.text = mensaje.mensaje
            Glide.with(mcontext).load(urlcontacto).circleCrop().into(layout.img)

        }




        //Glide.with(mcontext).load(user.).circleCrop().into(layout.contactoImg)
        return layout
    }


    private fun getTime():String{
        val date = System.currentTimeMillis()

        val sdf = SimpleDateFormat("MMddyyyy")
        val dateString = sdf.format(date)
        return dateString
    }

    private fun getTimeDia():String{
        val date = System.currentTimeMillis()

        val sdf = SimpleDateFormat("dd")
        val dateString = sdf.format(date)
        val dia = dateString.toInt() -1
        if (dia< 10){
            return 0.toString() + dia.toString()
        }else{
            return dia.toString()
        }
    }
}