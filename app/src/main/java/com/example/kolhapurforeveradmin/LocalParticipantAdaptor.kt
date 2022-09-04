package com.example.kolhapurforeveradmin

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LocalParticipantAdaptor(private var fragment: AddParticipantFragment, private var list: ArrayList<Participant>, private var competitionId:String) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById<TextView>(R.id.name_text_local)
        val image: ImageView = view.findViewById<ImageView>(R.id.imageview_local)
        val delete: ImageView = view.findViewById<ImageView>(R.id.delete_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.participant_iteam, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder){
            holder.name.text = model.name

            Glide
                .with(fragment.requireContext())
                .load(model.photoUrl)
                .into(holder.image)




            holder.delete.setOnClickListener {
                val dialog = MaterialAlertDialogBuilder(fragment.requireContext(),R.style.AppCompatAlertDialogStyle)
                dialog.setTitle("Remove From List")
                dialog.setMessage("Do you really want to remove ${model.name} from List?")
                dialog.setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                dialog.setPositiveButton("Remove") { dialog, which ->

                    if(fragment.compititionType.toString() == "Mandal"){
                        list.remove(model)

                        FirebaseDatabase.getInstance().getReference("Participants")
                            .child(competitionId)
                            .setValue(list)
                            .addOnSuccessListener {
                                fragment.getData()
                            }.addOnFailureListener {
                                fragment.getData()
                            }
                    }else{


                        FirebaseDatabase.getInstance().getReference("Mandal")
                            .child(model.id)
                            .removeValue()
                            .addOnSuccessListener {
                                list.remove(model)

                                FirebaseDatabase.getInstance().getReference("Participants")
                                    .child(competitionId)
                                    .setValue(list)
                                    .addOnSuccessListener {
                                        fragment.getData()



                                    }.addOnFailureListener {
                                        fragment.getData()
                                    }
                            }



                    }





                }
                dialog.show()
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

