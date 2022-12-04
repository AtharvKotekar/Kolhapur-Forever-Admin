package com.example.kolhapurforeveradmin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.kolhapurforeveradmin.databinding.FragmentAddMatchBinding
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add_specific.*

class AddMatchFragment : Fragment() {

    private lateinit var binding:FragmentAddMatchBinding

    private lateinit var teamList: HashMap<String,String>
    lateinit var sponsorlist:HashMap<String,String>
    lateinit var tournamentId:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMatchBinding.inflate(inflater,container,false)

        tournamentId = arguments?.getString("tournamentId").toString()

        teamList = HashMap()
        sponsorlist = HashMap()

        FirebaseDatabase.getInstance().getReference("Sports")
            .child("Football")
            .child("Tournament")
            .child(tournamentId)
            .child("teams")
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
        return binding.root
    }

}