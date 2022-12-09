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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    lateinit var mdialog: Dialog

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

        mdialog = Dialog(requireContext())
        mdialog.setContentView(R.layout.loading_dialog)
        mdialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        mdialog.setCancelable(false)

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

                            mdialog.show()

                            val goalsId = getRandomString(25)

                            val goal = Goals(goalsId,team1Id,team1Logo,goalPlayer.text.toString(),goalPlayerJerseyNumber.text.toString().toInt(),goalTimeStamp.text.toString().toInt(),"team1")



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
                                                    mdialog.dismiss()
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

                            mdialog.show()

                            val goalsId = getRandomString(25)

                            val goal = Goals(goalsId,team2Id,team2Logo,goalPlayer.text.toString(),goalPlayerJerseyNumber.text.toString().toInt(),goalTimeStamp.text.toString().toInt(),"team2")



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
                                                    mdialog.dismiss()
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

        binding.endMatchBtn.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(requireContext(),R.style.AppCompatAlertDialogStyle)
            dialog.setTitle("End this match")
            dialog.setMessage("Do you really want to end this match ${team1Name} vs ${team2Name} ")
            dialog.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            dialog.setPositiveButton("End") { dialog, which ->

                mdialog.show()
                FirebaseDatabase.getInstance().getReference("Sports")
                    .child("Football")
                    .child("Matches")
                    .child("Upcoming")
                    .child(matchId)
                    .get()
                    .addOnSuccessListener {
                        if (it.exists()){
                            val match = it.getValue(Match::class.java)


                            if(match!!.team1Score > match.team2Score){

                                FirebaseDatabase.getInstance().getReference("Sports")
                                    .child("Football")
                                    .child("Teams")
                                    .child(tournamentId)
                                    .child(team1Id)
                                    .get()
                                    .addOnSuccessListener { snapshot ->
                                        if(snapshot.exists()){
                                            val oldTeam1 = snapshot.getValue(Team::class.java)

                                            Toast.makeText(
                                                requireContext(),
                                                oldTeam1!!.name,
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            val newTeam = Team(oldTeam1!!.teamId,oldTeam1.name,oldTeam1.logo,oldTeam1.matchPlayed+1,oldTeam1.matchWon+1,oldTeam1.matchLoss,oldTeam1.matchDraw,oldTeam1.totalPoints+2,oldTeam1.goalsGF+match.team1Score,oldTeam1.goalsGA+match.team2Score)

                                            FirebaseDatabase.getInstance().getReference("Sports")
                                                .child("Football")
                                                .child("Teams")
                                                .child(tournamentId)
                                                .child(team1Id)
                                                .setValue(newTeam)
                                                .addOnSuccessListener {

                                                    Toast.makeText(requireContext(),
                                                        "Reaching .."
                                                    ,Toast.LENGTH_SHORT).show()

                                                    FirebaseDatabase.getInstance().getReference("Sports")
                                                        .child("Football")
                                                        .child("Teams")
                                                        .child(tournamentId)
                                                        .child(team2Id)
                                                        .get()
                                                        .addOnSuccessListener { snapshot2 ->
                                                            if(snapshot2.exists()){
                                                                val oldTeam2 = snapshot2.getValue(Team::class.java)

                                                                Toast.makeText(requireContext(),
                                                                    oldTeam2!!.teamId
                                                                    ,Toast.LENGTH_SHORT).show()

                                                                val newTeam2 = Team(oldTeam2!!.teamId,oldTeam2.name,oldTeam2.logo,oldTeam2.matchPlayed+1,oldTeam2.matchWon,oldTeam2.matchLoss+1,oldTeam2.matchDraw,oldTeam2.totalPoints,oldTeam2.goalsGF+match.team2Score,oldTeam2.goalsGA+match.team1Score)

                                                                FirebaseDatabase.getInstance().getReference("Sports")
                                                                    .child("Football")
                                                                    .child("Teams")
                                                                    .child(tournamentId)
                                                                    .child(team2Id)
                                                                    .setValue(newTeam2)
                                                                    .addOnSuccessListener {
                                                                        switchmatch(match)
                                                                        Toast.makeText(
                                                                            requireContext(),
                                                                            "Done",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }


                                                                    }



                                                                    }



                                                }
                                        }
                                    }
                            }else if(match!!.team1Score < match.team2Score){
                                FirebaseDatabase.getInstance().getReference("Sports")
                                    .child("Football")
                                    .child("Teams")
                                    .child(tournamentId)
                                    .child(team1Id)
                                    .get()
                                    .addOnSuccessListener { snapshot ->
                                        if(snapshot.exists()){
                                            val oldTeam1 = snapshot.getValue(Team::class.java)

                                            Toast.makeText(
                                                requireContext(),
                                                oldTeam1!!.name,
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            val newTeam = Team(oldTeam1!!.teamId,oldTeam1.name,oldTeam1.logo,oldTeam1.matchPlayed+1,oldTeam1.matchWon,oldTeam1.matchLoss+1,oldTeam1.matchDraw,oldTeam1.totalPoints,oldTeam1.goalsGF+match.team1Score,oldTeam1.goalsGA+match.team2Score)

                                            FirebaseDatabase.getInstance().getReference("Sports")
                                                .child("Football")
                                                .child("Teams")
                                                .child(tournamentId)
                                                .child(team1Id)
                                                .setValue(newTeam)
                                                .addOnSuccessListener {

                                                    Toast.makeText(requireContext(),
                                                        "Reaching .."
                                                        ,Toast.LENGTH_SHORT).show()

                                                    FirebaseDatabase.getInstance().getReference("Sports")
                                                        .child("Football")
                                                        .child("Teams")
                                                        .child(tournamentId)
                                                        .child(team2Id)
                                                        .get()
                                                        .addOnSuccessListener { snapshot2 ->
                                                            if(snapshot2.exists()){
                                                                val oldTeam2 = snapshot2.getValue(Team::class.java)

                                                                Toast.makeText(requireContext(),
                                                                    oldTeam2!!.teamId
                                                                    ,Toast.LENGTH_SHORT).show()

                                                                val newTeam2 = Team(oldTeam2!!.teamId,oldTeam2.name,oldTeam2.logo,oldTeam2.matchPlayed+1,oldTeam2.matchWon+1,oldTeam2.matchLoss,oldTeam2.matchDraw,oldTeam2.totalPoints+2,oldTeam2.goalsGF+match.team2Score,oldTeam2.goalsGA+match.team1Score)

                                                                FirebaseDatabase.getInstance().getReference("Sports")
                                                                    .child("Football")
                                                                    .child("Teams")
                                                                    .child(tournamentId)
                                                                    .child(team2Id)
                                                                    .setValue(newTeam2)
                                                                    .addOnSuccessListener {
                                                                        switchmatch(match)
                                                                        Toast.makeText(
                                                                            requireContext(),
                                                                            "Done",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }


                                                            }



                                                        }



                                                }
                                        }
                                    }
                            }else if(match!!.team1Score == match.team2Score){
                                FirebaseDatabase.getInstance().getReference("Sports")
                                    .child("Football")
                                    .child("Teams")
                                    .child(tournamentId)
                                    .child(team1Id)
                                    .get()
                                    .addOnSuccessListener { snapshot ->
                                        if(snapshot.exists()){
                                            val oldTeam1 = snapshot.getValue(Team::class.java)

                                            Toast.makeText(
                                                requireContext(),
                                                oldTeam1!!.name,
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            val newTeam = Team(oldTeam1!!.teamId,oldTeam1.name,oldTeam1.logo,oldTeam1.matchPlayed+1,oldTeam1.matchWon,oldTeam1.matchLoss,oldTeam1.matchDraw+1,oldTeam1.totalPoints+1,oldTeam1.goalsGF+match.team1Score,oldTeam1.goalsGA+match.team2Score)

                                            FirebaseDatabase.getInstance().getReference("Sports")
                                                .child("Football")
                                                .child("Teams")
                                                .child(tournamentId)
                                                .child(team1Id)
                                                .setValue(newTeam)
                                                .addOnSuccessListener {

                                                    Toast.makeText(requireContext(),
                                                        "Reaching .."
                                                        ,Toast.LENGTH_SHORT).show()

                                                    FirebaseDatabase.getInstance().getReference("Sports")
                                                        .child("Football")
                                                        .child("Teams")
                                                        .child(tournamentId)
                                                        .child(team2Id)
                                                        .get()
                                                        .addOnSuccessListener { snapshot2 ->
                                                            if(snapshot2.exists()){
                                                                val oldTeam2 = snapshot2.getValue(Team::class.java)

                                                                Toast.makeText(requireContext(),
                                                                    oldTeam2!!.teamId
                                                                    ,Toast.LENGTH_SHORT).show()

                                                                val newTeam2 = Team(oldTeam2!!.teamId,oldTeam2.name,oldTeam2.logo,oldTeam2.matchPlayed+1,oldTeam2.matchWon,oldTeam2.matchLoss,oldTeam2.matchDraw+1,oldTeam2.totalPoints+1,oldTeam2.goalsGF+match.team2Score,oldTeam2.goalsGA+match.team1Score)

                                                                FirebaseDatabase.getInstance().getReference("Sports")
                                                                    .child("Football")
                                                                    .child("Teams")
                                                                    .child(tournamentId)
                                                                    .child(team2Id)
                                                                    .setValue(newTeam2)
                                                                    .addOnSuccessListener {
                                                                        switchmatch(match)
                                                                        Toast.makeText(
                                                                            requireContext(),
                                                                            "Done",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }


                                                            }



                                                        }



                                                }
                                        }
                                    }
                            }




                            
                        }
                    }

            }
            dialog.show()
        }




        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getGoalsData()
    }

    fun switchmatch(match:Match){
        FirebaseDatabase.getInstance().getReference("Sports")
            .child("Football")
            .child("Matches")
            .child("Completed")
            .child(matchId)
            .setValue(match)
            .addOnSuccessListener {

                FirebaseDatabase.getInstance().getReference("Sports")
                    .child("Football")
                    .child("Matches")
                    .child("Upcoming")
                    .child(matchId)
                    .removeValue()
                    .addOnSuccessListener {
                        mdialog.dismiss()
                        onResume()
                        Toast.makeText(
                            requireContext(),
                            "Done..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


            }
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