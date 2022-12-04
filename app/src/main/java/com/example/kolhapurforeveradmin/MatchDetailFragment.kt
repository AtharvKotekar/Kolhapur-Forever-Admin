package com.example.kolhapurforeveradmin

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kolhapurforeveradmin.databinding.FragmentMatchDetailBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kolhapurforever.android.Adaptors.GoalsAdadptor
import kotlinx.android.synthetic.main.goal_item.*

class MatchDetailFragment : Fragment() {

    private lateinit var binding:FragmentMatchDetailBinding
    lateinit var tournamentId:String
    lateinit var tournamentName:String
    lateinit var matchId:String
    lateinit var team1Id:String
    lateinit var team2Id:String
    lateinit var team1Name:String
    lateinit var team2Name:String
    lateinit var team1Logo:String
    lateinit var team2Logo:String
    lateinit var sponsorId:String
    lateinit var goalsList:ArrayList<Goals>
    lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatchDetailBinding.inflate(inflater,container,false)

        tournamentId = arguments?.getString("tournamentId").toString()
        tournamentName = arguments?.getString("tournamentName").toString()
        matchId = arguments?.getString("matchId").toString()
        team1Id = arguments?.getString("team1Id").toString()
        team2Id = arguments?.getString("team2Id").toString()
        team1Name = arguments?.getString("team1Name").toString()
        team2Name = arguments?.getString("team2Name").toString()
        team1Logo = arguments?.getString("team1Logo").toString()
        team2Logo = arguments?.getString("team2Logo").toString()
        sponsorId = arguments?.getString("sponsorID").toString()

        binding.tournamentNameDetail.text = tournamentName
        binding.team1Name.text = team1Name
        binding.team2Name.text = team2Name

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        goalsList = ArrayList()

        Glide
            .with(requireContext())
            .load(team1Logo)
            .into(binding.team1Logo)

        Glide
            .with(requireContext())
            .load(team2Logo)
            .into(binding.team2Logo)

        binding.goalTeam1Btn.setOnClickListener {
            val goalDialog = Dialog(requireContext())
            goalDialog.setContentView(R.layout.add_goal_dialog)
            goalDialog.setCancelable(false)
            goalDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


            val goalTeamlogo = goalDialog.findViewById<ImageView>(R.id.goal_team_logo)
            val goalTeamName = goalDialog.findViewById<TextView>(R.id.goal_team_name)
            val goalPlayer = goalDialog.findViewById<EditText>(R.id.goal_player_name_etv)
            val goalPlayerJerseyNumber = goalDialog.findViewById<EditText>(R.id.goal_player_jersynumber_etv)
            val goalTimeStamp = goalDialog.findViewById<EditText>(R.id.goal_timestamp_etv)
            val addGoalBtn = goalDialog.findViewById<Button>(R.id.add_goal_button)
            val cancel = goalDialog.findViewById<Button>(R.id.cancel_goal_btn)

            Glide
                .with(requireContext())
                .load(team1Logo)
                .into(goalTeamlogo)

            goalTeamName.text = team1Name

            goalDialog.show()

            cancel.setOnClickListener {
                goalDialog.dismiss()
            }
            
            addGoalBtn.setOnClickListener { 
                if(goalPlayer.text.toString() == ""){
                    Toast.makeText(requireContext(), "Enter Player Name", Toast.LENGTH_SHORT).show()
                }else{
                    if(goalPlayerJerseyNumber.text.toString() == ""){
                        Toast.makeText(requireContext(), "Enter Player Jersey Number", Toast.LENGTH_SHORT).show()
                    }else{
                        if(goalTimeStamp.text.toString() == ""){
                            Toast.makeText(requireContext(), "Enter Goal TimeStamp", Toast.LENGTH_SHORT).show()
                        }else{

                            dialog.show()

                            val goalsId = getRandomString(25)

                            val goal = Goals(goalsId,team1Id,team1Logo,goalPlayer.text.toString(),goalPlayerJerseyNumber.text.toString().toInt(),goalTimeStamp.text.toString().toInt())



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
                                        .setValue(team1Goals+1)
                                        .addOnSuccessListener {

                                            FirebaseDatabase.getInstance().getReference("Sports")
                                                .child("Football")
                                                .child("Goals")
                                                .child(matchId)
                                                .child(goalsId)
                                                .setValue(goal)
                                                .addOnSuccessListener {
                                                    dialog.dismiss()
                                                    getGoalsData()
                                                    goalDialog.dismiss()
                                                    Toast.makeText(requireContext(), "Done..", Toast.LENGTH_SHORT).show()

                                                }

                                        }

                                }



                        }
                    }
                }
            }
        }


        binding.goalTeam2Btn.setOnClickListener {
            val goalDialog = Dialog(requireContext())
            goalDialog.setContentView(R.layout.add_goal_dialog)
            goalDialog.setCancelable(false)
            goalDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


            val goalTeamlogo = goalDialog.findViewById<ImageView>(R.id.goal_team_logo)
            val goalTeamName = goalDialog.findViewById<TextView>(R.id.goal_team_name)
            val goalPlayer = goalDialog.findViewById<EditText>(R.id.goal_player_name_etv)
            val goalPlayerJerseyNumber = goalDialog.findViewById<EditText>(R.id.goal_player_jersynumber_etv)
            val goalTimeStamp = goalDialog.findViewById<EditText>(R.id.goal_timestamp_etv)
            val addGoalBtn = goalDialog.findViewById<Button>(R.id.add_goal_button)
            val cancel = goalDialog.findViewById<Button>(R.id.cancel_goal_btn)

            Glide
                .with(requireContext())
                .load(team2Logo)
                .into(goalTeamlogo)

            goalTeamName.text = team2Name

            goalDialog.show()

            cancel.setOnClickListener {
                goalDialog.dismiss()
            }

            addGoalBtn.setOnClickListener {
                if(goalPlayer.text.toString() == ""){
                    Toast.makeText(requireContext(), "Enter Player Name", Toast.LENGTH_SHORT).show()
                }else{
                    if(goalPlayerJerseyNumber.text.toString() == ""){
                        Toast.makeText(requireContext(), "Enter Player Jersey Number", Toast.LENGTH_SHORT).show()
                    }else{
                        if(goalTimeStamp.text.toString() == ""){
                            Toast.makeText(requireContext(), "Enter Goal TimeStamp", Toast.LENGTH_SHORT).show()
                        }else{

                            dialog.show()

                            val goalsId = getRandomString(25)

                            val goal = Goals(goalsId,team2Id,team2Logo,goalPlayer.text.toString(),goalPlayerJerseyNumber.text.toString().toInt(),goalTimeStamp.text.toString().toInt())



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
                                        .setValue(team2Goals+1)
                                        .addOnSuccessListener {

                                            FirebaseDatabase.getInstance().getReference("Sports")
                                                .child("Football")
                                                .child("Goals")
                                                .child(matchId)
                                                .child(goalsId)
                                                .setValue(goal)
                                                .addOnSuccessListener {
                                                    dialog.dismiss()
                                                    getGoalsData()
                                                    goalDialog.dismiss()
                                                    Toast.makeText(requireContext(), "Done..", Toast.LENGTH_SHORT).show()

                                                }

                                        }

                                }



                        }
                    }
                }
            }
        }




        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getGoalsData()
    }

    fun getGoalsData(){
        FirebaseDatabase.getInstance().getReference("Sports")
            .child("Football")
            .child("Goals")
            .child(matchId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    goalsList.clear()
                    if(snapshot.exists()){



                        for (i in snapshot.children){
                            val goal = i.getValue(Goals::class.java)
                            goalsList.add(goal!!)
                        }

                        if(goalsList.size == 1){
                            updateUI(goalsList)
                        }else{
                            val sortedList = goalsList.sortedWith(compareBy { it.timestamp }).toList() as kotlin.collections.ArrayList<Goals>
                            updateUI(sortedList)
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    val snackBar = Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        error.message,
                        Snackbar.LENGTH_LONG
                    )
                    snackBar.setBackgroundTint(resources.getColor(android.R.color.holo_red_dark))
                    snackBar.setTextColor(resources.getColor(R.color.white))
                    snackBar.show()
                }

            })
    }

    fun updateUI(iteamList:kotlin.collections.ArrayList<Goals>){

        if(iteamList.isEmpty()){
            binding.recyclerView.visibility = View.INVISIBLE
            binding.recyclerView.visibility = View.VISIBLE
        }else {
            binding.recyclerView.visibility = View.VISIBLE
            val adaptor = GoalsAdadptor(this@MatchDetailFragment, iteamList,matchId,team1Id,team2Id)
            binding.recyclerView.adapter = adaptor
            binding.recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }


}