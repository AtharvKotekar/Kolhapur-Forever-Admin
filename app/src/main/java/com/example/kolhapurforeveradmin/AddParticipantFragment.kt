package com.example.kolhapurforeveradmin

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kolhapurforeveradmin.databinding.FragmentAddParticipantBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddParticipantFragment : Fragment() {

    lateinit var compititionId:String
    lateinit var compititionName:String
    lateinit var compititionType:String

    private lateinit var binding:FragmentAddParticipantBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddParticipantBinding.inflate(inflater,container,false)


        compititionId = arguments?.getString("comp id").toString()
        compititionName = arguments?.getString("comp name").toString()
        compititionType = arguments?.getString("comp type").toString()



        getData()



        binding.button.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("comp id",compititionId)
            bundle.putString("comp type",compititionType)
            findNavController().navigate(R.id.action_addParticipantFragment_to_addSpecificFragment,bundle)
        }

        return binding.root
    }

    fun updateUi(itemlist:ArrayList<Participant>){
        val adaptor = LocalParticipantAdaptor(this@AddParticipantFragment,itemlist,compititionId)
        binding.recyclerView.adapter = adaptor
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context,LinearLayoutManager.VERTICAL,false)
    }

    fun getData(){
        val participantList = ArrayList<Participant>()
        FirebaseDatabase.getInstance().getReference("Participants")
            .child(compititionId)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        participantList.clear()
                        for (i in snapshot.children){
                            val participant = i.getValue(Participant::class.java)
                            participantList.add(participant!!)
                        }
                        updateUi(participantList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message.toString(), Toast.LENGTH_LONG).show()
                }

            })

    }

}