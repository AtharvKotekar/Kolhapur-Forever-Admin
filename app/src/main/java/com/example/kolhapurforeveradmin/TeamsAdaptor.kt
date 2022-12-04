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

class TeamsAdaptor(private var fragment: Fragment, private var list: ArrayList<Team>,private var tournamentId:String) : RecyclerView.Adapter<TeamsAdaptor.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val teamName = view.findViewById<TextView>(R.id.team_name)
        val teamLogo = view.findViewById<ImageView>(R.id.team_logo)
        val deleteBtn = view.findViewById<ImageView>(R.id.team_delete_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.team_item, parent, false)
        return TeamsAdaptor.MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        Glide
            .with(fragment.requireContext())
            .load(model.logo)
            .into(holder.teamLogo)

        holder.teamName.text = model.name.toString()



        holder.deleteBtn.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(fragment.requireContext(),R.style.AppCompatAlertDialogStyle)
            dialog.setTitle("Remove From List")
            dialog.setMessage("Do you really want to remove ${model.name} from List?")
            dialog.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            dialog.setPositiveButton("Remove") { dialog, which ->

            val newTeamList = ArrayList<Team>()
                for (i in list){
                    newTeamList.add(i)
                }

                newTeamList.remove(model)

                FirebaseDatabase.getInstance().getReference("Sports")
                    .child("Football")
                    .child("Tournament")
                    .child(tournamentId)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        if(snapshot.exists()){
                            val tournament = snapshot.getValue(Tournament::class.java)

                            val newTournament = Tournament(tournament!!.tournamentId,tournament.tournamentName,tournament.tournamentLogo,
                                newTeamList!!
                            )

                            FirebaseDatabase.getInstance().getReference("Sports")
                                .child("Football")
                                .child("Tournament")
                                .child(tournamentId)
                                .setValue(newTournament)
                                .addOnSuccessListener {

                                    dialog.dismiss()
                                    Toast.makeText(
                                        fragment.requireActivity(),
                                        "Done..",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    fragment.onResume()


                                }




                        }
                    }




            }
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return  list.size
    }
}