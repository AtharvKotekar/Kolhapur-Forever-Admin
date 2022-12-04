package com.example.kolhapurforeveradmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase

class TournamentAdaptor(private var fragment: Fragment, private var list: ArrayList<Tournament>) : RecyclerView.Adapter<TournamentAdaptor.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tournamentName = view.findViewById<TextView>(R.id.tournament_name)
        val tournamentLogo = view.findViewById<ImageView>(R.id.tournament_logo)
        val deleteBtn = view.findViewById<ImageView>(R.id.delete_btn_tournament)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tournament_iteam, parent, false)
        return TournamentAdaptor.MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mode = list[position]

        Glide
            .with(fragment.requireContext())
            .load(mode.tournamentLogo)
            .into(holder.tournamentLogo)

        holder.tournamentName.text = mode.tournamentName.toString()

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("tournamentId",mode.tournamentId)
            fragment.findNavController().navigate(R.id.action_footballFragment_to_tournamentDetail,bundle)
        }

        holder.deleteBtn.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(fragment.requireContext(),R.style.AppCompatAlertDialogStyle)
            dialog.setTitle("Remove From List")
            dialog.setMessage("Do you really want to remove ${mode.tournamentName} from List?")
            dialog.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            dialog.setPositiveButton("Remove") { dialog, which ->

                FirebaseDatabase.getInstance().getReference("Sports")
                    .child("Football")
                    .child("Tournament")
                    .child(mode.tournamentId)
                    .removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(fragment.requireContext(), "Done", Toast.LENGTH_SHORT).show()
                    }



            }
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return  list.size
    }
}