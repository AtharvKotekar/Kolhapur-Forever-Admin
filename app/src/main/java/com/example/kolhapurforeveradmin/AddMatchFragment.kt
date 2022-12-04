package com.example.kolhapurforeveradmin

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.kolhapurforeveradmin.databinding.FragmentAddMatchBinding
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add_specific.*
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddMatchFragment : Fragment() {

    private lateinit var binding:FragmentAddMatchBinding

    private lateinit var teamList: HashMap<String,String>
    lateinit var sponsorlist:HashMap<String,String>
    lateinit var tournamentId:String
    lateinit var startDate:String
    lateinit var dialog: Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMatchBinding.inflate(inflater,container,false)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        tournamentId = arguments?.getString("tournamentId").toString()

        teamList = HashMap()
        sponsorlist = HashMap()

        startDate = ""

        FirebaseDatabase.getInstance().getReference("Sports")
            .child("Football")
            .child("Teams")
            .child(tournamentId)
            .get()
            .addOnSuccessListener {
                if(it.exists()){
                    teamList.clear()
                    teamList.put("hint","Select Teams")


                    for (i in it.children){
                        val team = i.getValue(Team::class.java)
                        teamList.put(team?.teamId!!,team.name)
                    }

                    val teamNameList = ArrayList<String>()
                    for (i in teamList.values){
                        teamNameList.add(i)
                    }



                    val adaptor = ArrayAdapter(this.requireContext(),R.layout.dropdown_item,teamNameList)
                    binding.team1Spinner.adapter = adaptor
                    binding.team2Spinner.adapter = adaptor

                    binding.team1Spinner.setSelection(teamNameList.size - 2)
                    binding.team2Spinner.setSelection(teamNameList.size - 2)

                }
            }

        FirebaseDatabase.getInstance().getReference("Sponsors")
            .get()
            .addOnSuccessListener {
                if(it.exists()){
                    sponsorlist.clear()
                    sponsorlist.put("hint","Select Sponsor")
                    sponsorlist.put("","Google Ads")


                    for (i in it.children){
                        val sponsor = i.getValue(Sponser::class.java)
                        sponsorlist.put(sponsor?.id!!,sponsor.name)
                    }

                    val sponsorNameList = ArrayList<String>()
                    for (i in sponsorlist.values){
                        sponsorNameList.add(i)
                    }



                    val adaptor = ArrayAdapter(this.requireContext(),R.layout.dropdown_item,sponsorNameList)
                    binding.sponsorSpinner.adapter = adaptor
                    binding.sponsorSpinner.setSelection(sponsorNameList.size - 2)

                }
            }

        binding.matchDateBtn.setOnClickListener {
            val currentDateTime = Calendar.getInstance()
            val startYear = currentDateTime.get(Calendar.YEAR)
            val startMonth = currentDateTime.get(Calendar.MONTH)
            val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
            val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
            val startMinute = currentDateTime.get(Calendar.MINUTE)

            DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, day ->
                TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    val pickedDateTime = Calendar.getInstance()
                    pickedDateTime.set(year,month,day,hour,minute,0)
                    val formatter = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss")
                    val formatedDate = formatter.format(pickedDateTime.time)
                    startDate  = formatedDate
                    binding.matchDateBtn.text = startDate
                }, startHour, startMinute, false).show()
            }, startYear, startMonth, startDay).show()
        }


        binding.createMatchBtn.setOnClickListener {
            if (binding.team1Spinner.selectedItem.toString() == "Select Teams") {
                Toast.makeText(requireContext(), "Please Select Team 1", Toast.LENGTH_SHORT).show()
            } else {
                if (binding.team2Spinner.selectedItem.toString() == "Select Teams") {
                    Toast.makeText(requireContext(), "Please Select Team 2", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (startDate == "") {
                        Toast.makeText(
                            requireContext(),
                            "Please Select Match Date",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (binding.team1Spinner.selectedItem.toString() == binding.team2Spinner.selectedItem.toString()) {
                            Toast.makeText(
                                requireContext(),
                                "Please select two different teams.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (binding.sponsorSpinner.selectedItem.toString() == "Select Sponsor") {
                                Toast.makeText(
                                    requireContext(),
                                    "Please Select The Sponsor",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                dialog.show()

                                val team1Id =
                                    teamList.entries.find { it.value == binding.team1Spinner.selectedItem.toString()}?.key.toString()
                                val team2Id =
                                    teamList.entries.find { it.value == binding.team2Spinner.selectedItem.toString()}?.key.toString()
                                val sponsorId =
                                    sponsorlist.entries.find { it.value == binding.sponsorSpinner.selectedItem.toString()}?.key.toString()

                                var tournamentName = ""

                                FirebaseDatabase.getInstance().getReference("Sports")
                                    .child("Football")
                                    .child("Tournament")
                                    .child(tournamentId)
                                    .get()
                                    .addOnSuccessListener {
                                        if(it.exists()){
                                            val tournament = it.getValue(Tournament::class.java)

                                            tournamentName = tournament!!.tournamentName

                                            FirebaseDatabase.getInstance().getReference("Sports")
                                                .child("Football")
                                                .child("Teams")
                                                .child(tournamentId)
                                                .child(team1Id)
                                                .get()
                                                .addOnSuccessListener { snapshot ->

                                                    if (snapshot.exists()) {
                                                        val team1 = snapshot.getValue(Team::class.java)

                                                        FirebaseDatabase.getInstance().getReference("Sports")
                                                            .child("Football")
                                                            .child("Teams")
                                                            .child(tournamentId)
                                                            .child(team2Id)
                                                            .get()
                                                            .addOnSuccessListener { snapshot ->

                                                                if (snapshot.exists()) {
                                                                    val team2 = snapshot.getValue(Team::class.java)

                                                                    val matchId = getRandomString(25)

                                                                    val timestamp = convertDateToLong(startDate)

                                                                    val match = Match(tournamentId,tournamentName,matchId,team1Id,team2Id,team1!!.name,team2!!.name,team1.logo,team2.logo,sponsorId,0,0,startDate,timestamp)


                                                                    FirebaseDatabase.getInstance().getReference("Sports")
                                                                        .child("Football")
                                                                        .child("Matches")
                                                                        .child("Upcoming")
                                                                        .child(matchId)
                                                                        .setValue(match)
                                                                        .addOnSuccessListener {
                                                                            dialog.dismiss()
                                                                            Toast.makeText(
                                                                                requireContext(),
                                                                                "Done..",
                                                                                Toast.LENGTH_SHORT
                                                                            ).show()
                                                                            activity?.onBackPressed()
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
                }
            }
        }


        return binding.root
    }



}