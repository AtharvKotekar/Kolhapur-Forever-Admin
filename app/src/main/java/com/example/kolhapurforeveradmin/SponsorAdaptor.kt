package com.example.kolhapurforeveradmin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.android.awaitFrame

class SponsorAdaptor(private var fragment: EditSponsorFragment, private var list: ArrayList<Sponser>) : RecyclerView.Adapter<SponsorAdaptor.MyViewHolder>()   {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val name = view.findViewById<TextView>(R.id.name_text)
        val offer = view.findViewById<TextView>(R.id.offer_text)
        val logo = view.findViewById<ImageView>(R.id.logo_image)
        val deletebtn = view.findViewById<ImageButton>(R.id.delete_btn_sponsor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sponsor_iteam, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        Glide
            .with(fragment.requireContext())
            .load(model.logo)
            .placeholder(R.drawable.ic_baseline_image_24)
            .into(holder.logo)

        holder.name.text = model.name
        holder.offer.text = model.offer

        holder.deletebtn.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(fragment.requireContext(),R.style.AppCompatAlertDialogStyle)
            dialog.setTitle("Remove From List")
            dialog.setMessage("Do you really want to remove ${model.name} from List?")
            dialog.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            dialog.setPositiveButton("Remove") { dialog, which ->
                fragment.dialog.show()
                                FirebaseDatabase.getInstance().getReference("Sponsors")
                                    .child(model.id)
                                    .removeValue()
                                    .addOnCompleteListener { task ->
                                        if(task.isSuccessful){
                                            fragment.sponserList.remove(model)
                                            fragment.getData()
                                        }
                                    }


            }.show()
        }
    }

    override fun getItemCount(): Int {
        return  list.size
    }
}