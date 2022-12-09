package com.kolhapurforever.android.Adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kolhapurforeveradmin.Goals
import com.example.kolhapurforeveradmin.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import java.util.ArrayList

class GoalsAdadptor(private var fragment: Fragment, private var list: ArrayList<Goals>,private var matchId:String,private var team1Id:String,private var team2Id:String) : RecyclerView.Adapter<GoalsAdadptor.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val goalTeamLogo = view.findViewById<ImageView>(R.id.goal_team_logo)
        val goalTimestamp = view.findViewById<TextView>(R.id.goal_timestamp)
        val goalPlayerName = view.findViewById<TextView>(R.id.goal_player_name)
        val deleteBtn = view.findViewById<ImageView>(R.id.delete_btn_goal)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GoalsAdadptor.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.goal_item, parent, false)
        return GoalsAdadptor.MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalsAdadptor.MyViewHolder, position: Int) {
        val model = list[position]

        Glide
            .with(fragment.requireContext())
            .load(model.teamlogo)
            .into(holder.goalTeamLogo)

        holder.goalTimestamp.text = "${model.timestamp}`"

        holder.goalPlayerName.text = model.playerName + "  :  "+model.playerJerseyNumber.toString()

        holder.deleteBtn.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(fragment.requireContext(),R.style.AppCompatAlertDialogStyle)
            dialog.setTitle("Remove From List")
            dialog.setMessage("Do you really want to remove ${model.playerName} Goal in ${model.timestamp} from List?")
            dialog.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            dialog.setPositiveButton("Delete") { dialog, which ->

                FirebaseDatabase.getInstance().getReference("Sports")
                    .child("Football")
                    .child("Goals")
                    .child(matchId)
                    .child(model.goalId)
                    .removeValue()
                    .addOnSuccessListener {

                        if(model.team == "team1"){
                            FirebaseDatabase.getInstance().getReference("Sports")
                                .child("Football")
                                .child("Matches")
                                .child("Upcoming")
                                .child(matchId)
                                .child("team1Score")
                                .get()
                                .addOnSuccessListener {
                                    val team1Goals = it.getValue().toString().toInt()

                                    FirebaseDatabase.getInstance().getReference("Sports")
                                        .child("Football")
                                        .child("Matches")
                                        .child("Upcoming")
                                        .child(matchId)
                                        .child("team1Score")
                                        .setValue(team1Goals-1)
                                        .addOnSuccessListener {

                                            dialog.dismiss()
                                            Toast.makeText(
                                                fragment.requireContext(),
                                                "Donee..",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            fragment.onResume()

                                        }

                                }

                        }else if(model.team == "team2"){
                            FirebaseDatabase.getInstance().getReference("Sports")
                                .child("Football")
                                .child("Matches")
                                .child("Upcoming")
                                .child(matchId)
                                .child("team2Score")
                                .get()
                                .addOnSuccessListener {
                                    val team2Goals = it.getValue().toString().toInt()

                                    FirebaseDatabase.getInstance().getReference("Sports")
                                        .child("Football")
                                        .child("Matches")
                                        .child("Upcoming")
                                        .child(matchId)
                                        .child("team2Score")
                                        .setValue(team2Goals-1)
                                        .addOnSuccessListener {

                                            dialog.dismiss()
                                            Toast.makeText(
                                                fragment.requireContext(),
                                                "Donee..",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            fragment.onResume()

                                        }

                                }
                        }
                    }




            }
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}