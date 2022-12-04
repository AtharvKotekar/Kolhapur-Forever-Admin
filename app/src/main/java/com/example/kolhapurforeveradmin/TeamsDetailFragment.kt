package com.example.kolhapurforeveradmin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kolhapurforeveradmin.databinding.FragmentAddTeamBinding
import com.example.kolhapurforeveradmin.databinding.FragmentTeamsDetailBinding
import com.google.firebase.database.FirebaseDatabase


class TeamsDetailFragment : Fragment() {
    private lateinit var temasList:ArrayList<Team>
    private lateinit var binding: FragmentTeamsDetailBinding
    lateinit var tournametId:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTeamsDetailBinding.inflate(inflater,container,false)



        tournametId = arguments?.getString("tournamentId").toString()

        temasList = ArrayList()



        binding.addNewTeamsBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("tournamentId",tournametId)
            findNavController().navigate(R.id.action_teamsDetailFragment_to_addTeamFragment,bundle)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getData()

    }

    fun getData(){
        FirebaseDatabase.getInstance().getReference("Sports")
            .child("Football")
            .child("Teams")
            .child(tournametId)
            .get()
            .addOnSuccessListener { snapshot ->
                if(snapshot.exists()){
                    temasList.clear()

                    for ( i in snapshot.children){
                        val team = i.getValue(Team::class.java)
                        temasList.add(team!!)
                    }




                    updateUI(temasList)
                }
            }
    }

    fun updateUI(itemList:ArrayList<Team>){
        val adaptor = TeamsAdaptor(this@TeamsDetailFragment,itemList,tournametId)
        binding.recyclerView.adapter = adaptor
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
    }

}