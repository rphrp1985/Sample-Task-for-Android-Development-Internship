package com.prianshuprasad.videoplayer

import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class searchHelper {

    private val db = FirebaseFirestore.getInstance().collection("search")

    fun getDB() = db;

    fun addData(title:String,tags:ArrayList<String>,docID:String){


        tags.add(title)
        for(tagi in tags) {

            if(tagi==" " || tagi=="")
                continue;

            val tag= tagi.lowercase()

            val oldData = db.document(tag).get()
            oldData.addOnCompleteListener {
                val array:ArrayList<String> = ArrayList()
                array.add(docID)
                if(it.getResult().exists()){

                    val arr= it.getResult().toObject(searchData::class.java)!!.arrayVideoID

                  array.addAll(arr)

                    GlobalScope.launch {
                        db.document(tag).set(searchData(array))
                    }

                }else
                {
                    GlobalScope.launch {
                        db.document(tag).set(searchData(array))
                    }


                }
            }
        }
    }








}