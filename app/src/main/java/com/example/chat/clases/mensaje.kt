package com.example.chat.clases

class mensaje {
    lateinit var mensaje:String
    lateinit var idEmisor:String
    constructor(){}
    constructor( mensaje:String, idEmisor:String){
        this.mensaje =mensaje
        this.idEmisor =idEmisor
    }

}