package com.example.kolhapurforeveradmin

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.kolhapurforeveradmin.databinding.FragmentEditSpecificCompititionBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates


class EditSpecificCompititionFragment : Fragment() {
    private lateinit var name:String
    private lateinit var id:String
    private lateinit var firstPrize:String
    private lateinit var secondPrize:String
    private lateinit var thirdPrize:String
    private lateinit var type:String
    var startDate:String = ""
    var endtDate:String = ""


    lateinit var dialog: Dialog

    private lateinit var binding:FragmentEditSpecificCompititionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditSpecificCompititionBinding.inflate(inflater,container,false)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        name = arguments?.getString("name").toString()
        id = arguments?.getString("id").toString()
        firstPrize = arguments?.getString("firstPrize").toString()
        secondPrize = arguments?.getString("secondPrize").toString()
        thirdPrize = arguments?.getString("thirdPrize").toString()
        startDate = arguments?.getString("startDate").toString()
        endtDate = arguments?.getString("endDate").toString()
        type = arguments?.getString("type").toString()

        binding.etvCpmName.text = name.toEditable()
        binding.etvFirstPrize.text = firstPrize.toEditable()
        binding.etvSecondPrize.text = secondPrize.toEditable()
        binding.etvThirdPrize.text = thirdPrize.toEditable()
        binding.startDateBtn.text = "Start Date - $startDate"
        binding.endDateBtn.text = "End Date - $endtDate"

        binding.startDateBtn.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(requireContext(),
                DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                startDate = "$i3.${i2.toString().toInt()+1}.$i, 00:00:00"
                binding.startDateBtn.text = "Start date  - " + startDate
            },now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        binding.endDateBtn.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(requireContext(),
                DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                endtDate = "$i3.${i2.toString().toInt()+1}.$i, 23:59:59"
                binding.endDateBtn.text = "End date  - " + endtDate
            },now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        binding.deleteBtn.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(requireContext(),R.style.AppCompatAlertDialogStyle)
            dialog.setTitle("Remove From List")
            dialog.setMessage("Do you really want to remove ${name} from List?")
            dialog.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            dialog.setPositiveButton("Remove") { dialog, which ->

               FirebaseDatabase.getInstance().getReference("Competition")
                   .child(id)
                   .removeValue()
                   .addOnSuccessListener {

                       activity?.onBackPressed()
                       Toast.makeText(requireContext(), "Done...", Toast.LENGTH_SHORT).show()

                       FirebaseDatabase.getInstance().getReference("Participants")
                           .child(id)
                           .removeValue()
                           .addOnSuccessListener {
                               FirebaseDatabase.getInstance().getReference("Mandal")
                                   .child(id)
                                   .removeValue()

                               FirebaseDatabase.getInstance().getReference("Votes")
                                   .child(id)
                                   .removeValue()

                           }
                   }



            }
            dialog.show()
        }

        binding.previewBtn.setOnClickListener {

            if(binding.etvCpmName.text.toString().replace(" ","")  == ""){
                Toast.makeText(requireContext(), "Please Enter Competition Name", Toast.LENGTH_SHORT).show()
            }else{
                    if(binding.etvFirstPrize.text.toString().replace(" ","")  == ""){
                        Toast.makeText(requireContext(), "Please Enter First Prize", Toast.LENGTH_SHORT).show()
                    }else{
                        if(binding.etvSecondPrize.text.toString().replace(" ","")  == ""){
                            Toast.makeText(requireContext(), "Please Enter Second Prize", Toast.LENGTH_SHORT).show()
                        }else{
                            if(binding.etvThirdPrize.text.toString().replace(" ","")  == ""){
                                Toast.makeText(requireContext(), "Please Enter Third Prize", Toast.LENGTH_SHORT).show()
                            }else{
                                if(startDate.toString() == ""){
                                    Toast.makeText(requireContext(), "Please Select Start Date", Toast.LENGTH_SHORT).show()
                                }else{
                                    if(endtDate == ""){
                                        Toast.makeText(requireContext(), "Please Select End Date", Toast.LENGTH_SHORT).show()
                                    }else{
                                        binding.titleText.text = binding.etvCpmName.text
                                        var spfStart = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss")
                                        val newStartDate: Date = spfStart.parse(startDate) as Date
                                        spfStart = SimpleDateFormat("dd/MM/yyyy")
                                        binding.startDateText.text = spfStart.format(newStartDate)

                                        var spfEnd = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss")
                                        val newEndDate: Date = spfEnd.parse(endtDate) as Date
                                        spfEnd = SimpleDateFormat("dd/MM/yyyy")

                                        binding.endDateText.text = spfEnd.format(newEndDate)

                                        binding.previewCard.visibility = View.VISIBLE
                                        binding.addComp.visibility = View.VISIBLE


                                        binding.addComp.setOnClickListener{
                                            if(newStartDate.before(newEndDate)){

                                                val timestamp = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss").parse(endtDate)
                                                
                                                val compitition = Compitition(id,binding.etvCpmName.text.toString(),startDate,endtDate,binding.etvFirstPrize.text.toString()
                                                    ,binding.etvSecondPrize.text.toString(),binding.etvThirdPrize.text.toString(),type,timestamp.time)

                                                FirebaseDatabase.getInstance().getReference("Competition")
                                                    .child(id)
                                                    .setValue(compitition)
                                                    .addOnCompleteListener {
                                                        activity?.onBackPressed()
                                                        Toast.makeText(
                                                            requireContext(),
                                                            "Done....",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                            .show()
                                                    }


                                            }else{
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Check all dates again.",
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




        return binding.root
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

}