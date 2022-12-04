package com.example.kolhapurforeveradmin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kolhapurforeveradmin.databinding.FragmentFootballBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FootballFragment : Fragment() {

    private lateinit var binding:FragmentFootballBinding
    private lateinit var tournamentsList:ArrayList<Tournament>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFootballBinding.inflate(inflater,container,false)


        tournamentsList = ArrayList()

        getData()

        binding.addTournamentBtn.setOnClickListener {
            findNavController().navigate(R.id.action_footballFragment_to_addTournamentFragment)
        }

        return binding.root
    }

    fun getData(){
        FirebaseDatabase.getInstance().getReference("Sports")
            .child("Football")
            .child("Tournament")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        tournamentsList.clear()
                        for (i in snapshot.children){
                            val tournament = i.getValue(Tournament::class.java)
                            tournamentsList.add(tournament!!)
                        }
                        updateUI(tournamentsList)
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

    fun updateUI(itemList:ArrayList<Tournament>){
        val  adaptor = TournamentAdaptor(this@FootballFragment,itemList)
        binding.recyclerView.adapter = adaptor
        binding.recyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
    }


}