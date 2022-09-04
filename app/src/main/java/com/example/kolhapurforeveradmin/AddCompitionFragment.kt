package com.example.kolhapurforeveradmin

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.kolhapurforeveradmin.databinding.FragmentAddCompitionBinding
import com.example.kolhapurforeveradmin.databinding.FragmentEditSponsorBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class AddCompitionFragment : Fragment() {

    private lateinit var binding: FragmentAddCompitionBinding
    var startDate:String = ""
    var endtDate:String = ""
    lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCompitionBinding.inflate(inflater,container,false)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        binding.startDateBtn.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(requireContext(),DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                startDate = "$i3.${i2.toString().toInt()+1}.$i, 00:00:00"
                binding.startDateBtn.text = "Start date  - " + startDate
            },now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        binding.endDateBtn.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(requireContext(),DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                endtDate = "$i3.${i2.toString().toInt()+1}.$i, 23:59:59"
                binding.endDateBtn.text = "End date  - " + endtDate
            },now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        binding.previewBtn.setOnClickListener {

            if(binding.etvCpmName.text.toString().replace(" ","")  == ""){
                Toast.makeText(requireContext(), "Please Enter Competition Name", Toast.LENGTH_SHORT).show()
            }else{
                if(binding.etvType.text.toString().replace(" ","")  == ""){
                    Toast.makeText(requireContext(), "Please Enter Competition Type", Toast.LENGTH_SHORT).show()
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
                                                dialog.show()

                                                val timestamp = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss").parse(endtDate)

                                                val id = getRandomString(25)
                                                
                                                val compitition = Compitition(id,binding.etvCpmName.text.toString(),startDate,endtDate,binding.etvFirstPrize.text.toString()
                                                ,binding.etvSecondPrize.text.toString(),binding.etvThirdPrize.text.toString(),binding.etvType.text.toString(),timestamp.time)

                                                FirebaseDatabase.getInstance().getReference("Competition")
                                                    .child(id)
                                                    .setValue(compitition)
                                                    .addOnCompleteListener {
                                                        binding.etvCpmName.text = "".toEditable()
                                                        binding.etvType.text = "".toEditable()
                                                        binding.etvFirstPrize.text = "".toEditable()
                                                        binding.etvSecondPrize.text = "".toEditable()
                                                        binding.etvThirdPrize.text = "".toEditable()
                                                        startDate = ""
                                                        endtDate = ""
                                                        binding.startDateBtn.text = "Select Start Date"
                                                        binding.endDateBtn.text = "Select End Date"
                                                        binding.previewCard.visibility = View.GONE
                                                        binding.voteBtn.visibility = View.GONE

                                                        Toast.makeText(
                                                            requireContext(),
                                                            "Done.......",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                        dialog.dismiss()
                                                    }


                                            }else{

                                                dialog.dismiss()
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



        }

        return binding.root
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    override fun onResume() {
        super.onResume()
        val type = resources.getStringArray(R.array.type)
        val arrayAdaptor = ArrayAdapter(requireContext(),R.layout.dropdown_item,type)
        binding.etvType.setAdapter(arrayAdaptor)

        if(startDate == ""){
            binding.startDateBtn.text = "Select Start Date"
        }else{
            binding.startDateBtn.text = "Start Date - "+startDate
        }

        if(endtDate == ""){
            binding.endDateBtn.text = "Select End Date"
        }else{
            binding.endDateBtn.text = "End Date - "+endtDate
        }
    }
}