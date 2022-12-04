package com.example.kolhapurforeveradmin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kolhapurforeveradmin.databinding.FragmentTournamentDetailBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class TournamentDetail : Fragment() {

    private lateinit var binding:FragmentTournamentDetailBinding

    private val matchesList = ArrayList<Match>()

    lateinit var tournametId:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTournamentDetailBinding.inflate(inflater,container,false)

        tournametId = arguments?.getString("tournamentId").toString()



        binding.addMatchBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("tournamentId",tournametId)
            findNavController().navigate(R.id.action_tournamentDetail_to_addMatchFragment,bundle)
        }

        binding.addTeamBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("tournamentId",tournametId)
            findNavController().navigate(R.id.action_tournamentDetail_to_teamsDetailFragment,bundle)
        }

        return binding.root
    }

    private fun getData(){
        FirebaseDatabase.getInstance().getReference("Sports")
            .child("Football")
            .child("Matches")
            .child("Upcoming")
            .orderByChild("tournamentId").equalTo(tournametId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    matchesList.clear()
                    if(snapshot.exists()){
                        for (i in snapshot.children) {
                            val match = i.getValue(Match::class.java)
                            matchesList.add(match!!)
                        }

                        updateUI(matchesList)

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

    fun updateUI(iteamList:ArrayList<Match>){
        val adaptor = MatchAdaptor(this@TournamentDetail,iteamList)
        binding.recyclerView.adapter = adaptor
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)

    }

    override fun onResume() {
        super.onResume()
        getData()
    }
}