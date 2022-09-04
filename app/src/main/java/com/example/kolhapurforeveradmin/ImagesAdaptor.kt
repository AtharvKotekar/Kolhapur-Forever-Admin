package com.example.kolhapurforeveradmin

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class ImagesAdaptor (private var fragment: Fragment, private var list: ArrayList<Uri?>) : RecyclerView.Adapter<ImagesAdaptor.MyViewHolder>(){

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val image = view.findViewById<ImageView>(R.id.image)
        val btn = view.findViewById<Button>(R.id.delete_image_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.images_iteam, parent, false)
        return ImagesAdaptor.MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        holder.image.setImageURI(model)
        holder.btn.setOnClickListener {
            when(fragment){
                is AddSponsorFragment -> {
                    (fragment as AddSponsorFragment).images.remove(model)
                    (fragment as AddSponsorFragment).adaptor.notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}