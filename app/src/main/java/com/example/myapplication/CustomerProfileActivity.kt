package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.concurrent.thread

class CustomerProfileActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var customerId: Int = -1
    private var currentMobile: String = ""
    private var currentCustomerName: String = ""
    private var selectedGarment: String = "Shirt"
    
    // UI References - Display
    private lateinit var tvCustomerId: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvName: TextView
    private lateinit var tvMobile: TextView
    private lateinit var tvInitials: TextView
    
    private lateinit var tvValue1: TextView
    private lateinit var tvValue2: TextView
    private lateinit var tvValue3: TextView
    private lateinit var tvValue4: TextView
    private lateinit var tvValue5: TextView
    private lateinit var tvValue6: TextView
    private lateinit var tvValue7: TextView
    private lateinit var tvValue8: TextView
    
    private lateinit var tvLabel1: TextView
    private lateinit var tvLabel2: TextView
    private lateinit var tvLabel3: TextView
    private lateinit var tvLabel4: TextView
    private lateinit var tvLabel5: TextView
    private lateinit var tvLabel6: TextView
    private lateinit var tvLabel7: TextView
    private lateinit var tvLabel8: TextView
    
    private lateinit var container1: View
    private lateinit var container2: View
    private lateinit var container3: View
    private lateinit var container4: View
    private lateinit var container5: View
    private lateinit var container6: View
    private lateinit var container7: View
    private lateinit var container8: View
    
    private lateinit var tvNotesValue: TextView
    private lateinit var tvMeasurementTitle: TextView
    
    private lateinit var cardMeasurementDisplay: CardView
    private lateinit var cardGarmentInput: CardView

    // UI References - Input Fields
    private lateinit var et1: TextInputEditText
    private lateinit var et2: TextInputEditText
    private lateinit var et3: TextInputEditText
    private lateinit var et4: TextInputEditText
    private lateinit var et5: TextInputEditText
    private lateinit var et6: TextInputEditText
    private lateinit var et7: TextInputEditText
    private lateinit var et8: TextInputEditText
    
    private lateinit var til1: TextInputLayout
    private lateinit var til2: TextInputLayout
    private lateinit var til3: TextInputLayout
    private lateinit var til4: TextInputLayout
    private lateinit var til5: TextInputLayout
    private lateinit var til6: TextInputLayout
    private lateinit var til7: TextInputLayout
    private lateinit var til8: TextInputLayout
    
    private lateinit var btnSaveGarment: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.customer_profile)

        dbHelper = DatabaseHelper(this)

        // Initialize Display Views
        tvName = findViewById(R.id.tvName)
        tvMobile = findViewById(R.id.tvMobile)
        tvCustomerId = findViewById(R.id.tvCustomerId)
        tvStatus = findViewById(R.id.tvStatus)
        tvInitials = findViewById(R.id.tvInitials)
        
        tvValue1 = findViewById(R.id.tvValue1)
        tvValue2 = findViewById(R.id.tvValue2)
        tvValue3 = findViewById(R.id.tvValue3)
        tvValue4 = findViewById(R.id.tvValue4)
        tvValue5 = findViewById(R.id.tvValue5)
        tvValue6 = findViewById(R.id.tvValue6)
        tvValue7 = findViewById(R.id.tvValue7)
        tvValue8 = findViewById(R.id.tvValue8)
        
        tvLabel1 = findViewById(R.id.tvLabel1)
        tvLabel2 = findViewById(R.id.tvLabel2)
        tvLabel3 = findViewById(R.id.tvLabel3)
        tvLabel4 = findViewById(R.id.tvLabel4)
        tvLabel5 = findViewById(R.id.tvLabel5)
        tvLabel6 = findViewById(R.id.tvLabel6)
        tvLabel7 = findViewById(R.id.tvLabel7)
        tvLabel8 = findViewById(R.id.tvLabel8)
        
        container1 = findViewById(R.id.container1)
        container2 = findViewById(R.id.container2)
        container3 = findViewById(R.id.container3)
        container4 = findViewById(R.id.container4)
        container5 = findViewById(R.id.container5)
        container6 = findViewById(R.id.container6)
        container7 = findViewById(R.id.container7)
        container8 = findViewById(R.id.container8)

        tvNotesValue = findViewById(R.id.tvNotesValue)
        tvMeasurementTitle = findViewById(R.id.tvMeasurementTitle)

        cardMeasurementDisplay = findViewById(R.id.cardMeasurementDisplay)
        cardGarmentInput = findViewById(R.id.cardGarmentInput)

        // Initialize Input Fields
        et1 = findViewById(R.id.et1)
        et2 = findViewById(R.id.et2)
        et3 = findViewById(R.id.et3)
        et4 = findViewById(R.id.et4)
        et5 = findViewById(R.id.et5)
        et6 = findViewById(R.id.et6)
        et7 = findViewById(R.id.et7)
        et8 = findViewById(R.id.et8)
        
        til1 = findViewById(R.id.til1)
        til2 = findViewById(R.id.til2)
        til3 = findViewById(R.id.til3)
        til4 = findViewById(R.id.til4)
        til5 = findViewById(R.id.til5)
        til6 = findViewById(R.id.til6)
        til7 = findViewById(R.id.til7)
        til8 = findViewById(R.id.til8)
        
        btnSaveGarment = findViewById(R.id.btnSaveGarment)

        // Data from intent
        currentCustomerName = intent.getStringExtra("CUSTOMER_NAME") ?: "Unknown"
        currentMobile = intent.getStringExtra("CUSTOMER_MOBILE") ?: ""

        tvName.text = currentCustomerName
        tvMobile.text = currentMobile
        
        val initials = currentCustomerName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("").take(2).uppercase()
        tvInitials.text = if (initials.isNotEmpty()) initials else "C"

        fetchCustomerData(currentMobile)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        // Setup All Garment Buttons
        val garmentButtons = mapOf(
            R.id.btnShirtProfile to "Shirt",
            R.id.btnPantProfile to "Pant",
            R.id.btnKotiProfile to "Koti",
            R.id.btnSuitProfile to "Suit",
            R.id.btnJabbhoProfile to "Jabbho",
            R.id.btnLehnghoProfile to "Lehngho",
            R.id.btnSafariProfile to "Safari",
            R.id.btnJodhpuriProfile to "Jodhpuri"
        )

        garmentButtons.forEach { (id, type) ->
            findViewById<MaterialButton>(id).setOnClickListener {
                selectedGarment = type
                checkAndShowUI(type)
            }
        }

        btnSaveGarment.setOnClickListener { saveMeasurements() }
        
        findViewById<TextView>(R.id.btnEditSize).setOnClickListener {
            val intent = Intent(this, AddMeasurementsActivity::class.java)
            intent.putExtra("CUSTOMER_NAME", currentCustomerName)
            intent.putExtra("CUSTOMER_MOBILE", currentMobile)
            intent.putExtra("CUSTOMER_ID", customerId)
            intent.putExtra("IS_EDIT_MODE", true)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnNewOrder).setOnClickListener {
            val intent = Intent(this, AddMeasurementsActivity::class.java)
            intent.putExtra("CUSTOMER_NAME", currentCustomerName)
            intent.putExtra("CUSTOMER_MOBILE", currentMobile)
            intent.putExtra("CUSTOMER_ID", customerId)
            intent.putExtra("IS_EDIT_MODE", false)
            startActivity(intent)
        }
    }

    private fun checkAndShowUI(type: String) {
        thread {
            val cursor = dbHelper.getLatestMeasurement(customerId, type)
            val hasData = cursor.moveToFirst()
            cursor.close()

            runOnUiThread {
                if (hasData) {
                    showMeasurementDisplay(type)
                } else {
                    showInputForm(type)
                }
            }
        }
    }

    private fun showInputForm(type: String) {
        cardMeasurementDisplay.visibility = View.GONE
        cardGarmentInput.visibility = View.VISIBLE
        
        val tils = listOf(til1, til2, til3, til4, til5, til6, til7, til8)
        tils.forEach { it.visibility = View.GONE }
        
        et1.text = null; et2.text = null; et3.text = null; et4.text = null
        et5.text = null; et6.text = null; et7.text = null; et8.text = null

        when (type) {
            "Pant" -> {
                setupInputField(til1, "Waist (Natural Line)")
                setupInputField(til2, "Hip (Widest Part)")
                setupInputField(til3, "Inseam (Crotch to Ankle)")
                setupInputField(til4, "Outseam (Waist to Floor)")
                setupInputField(til5, "Rise (Crotch Depth)")
            }
            "Shirt" -> {
                setupInputField(til1, "Length")
                setupInputField(til2, "Chest")
                setupInputField(til3, "Waist")
                setupInputField(til4, "Collar")
                setupInputField(til5, "Shoulder")
                setupInputField(til6, "Sleeve")
            }
            "Koti" -> {
                setupInputField(til1, "Length of Koti")
                setupInputField(til2, "Chest (Fullest Part)")
                setupInputField(til3, "Waist / Stomach")
                setupInputField(til4, "Shoulder (Across Back)")
                setupInputField(til5, "Armhole")
                setupInputField(til6, "Neckline (Collar)")
            }
            "Suit" -> {
                setupInputField(til1, "Jacket Length")
                setupInputField(til2, "Chest")
                setupInputField(til3, "Waist")
                setupInputField(til4, "Shoulder Width")
                setupInputField(til5, "Sleeve Length")
                setupInputField(til6, "Inseam")
            }
            "Jabbho" -> {
                setupInputField(til1, "Length")
                setupInputField(til2, "Chest (Bust)")
                setupInputField(til3, "Shoulder Width")
                setupInputField(til4, "Sleeve Length")
                setupInputField(til5, "Neck Circumference")
            }
            "Lehngho" -> {
                setupInputField(til1, "Waist")
                setupInputField(til2, "Hip")
                setupInputField(til3, "Inseam")
                setupInputField(til4, "Outseam")
                setupInputField(til5, "Rise")
            }
            "Safari" -> {
                setupInputField(til1, "Total Length")
                setupInputField(til2, "Chest")
                setupInputField(til3, "Waist")
                setupInputField(til4, "Shoulder Width")
                setupInputField(til5, "Arm Length")
                setupInputField(til6, "Neck")
                setupInputField(til7, "Hips / Seat")
                setupInputField(til8, "Inside Leg (Inseam)")
            }
            "Jodhpuri" -> {
                setupInputField(til1, "Jacket Length")
                setupInputField(til2, "Chest")
                setupInputField(til3, "Stomach / Waist")
                setupInputField(til4, "Shoulder Width")
                setupInputField(til5, "Sleeve Length")
                setupInputField(til6, "Neck")
                setupInputField(til7, "Hip")
            }
        }
        findViewById<TextView>(R.id.tvInputTitle).text = "$type Measurements Input"
        btnSaveGarment.text = "Save $type Measurements"
    }

    private fun setupInputField(til: TextInputLayout, hint: String) {
        til.visibility = View.VISIBLE
        til.hint = hint
    }

    private fun showMeasurementDisplay(type: String) {
        cardGarmentInput.visibility = View.GONE
        cardMeasurementDisplay.visibility = View.VISIBLE
        loadLatestMeasurements(type)
    }

    private fun saveMeasurements() {
        val v1 = et1.text.toString().trim()
        val v2 = et2.text.toString().trim()
        val v3 = et3.text.toString().trim()
        val v4 = et4.text.toString().trim()
        val v5 = et5.text.toString().trim()
        val v6 = et6.text.toString().trim()
        val v7 = et7.text.toString().trim()
        val v8 = et8.text.toString().trim()

        if (v1.isEmpty()) {
            Toast.makeText(this, "First field is required", Toast.LENGTH_SHORT).show()
            return
        }

        thread {
            try {
                // For simplicity, mapping the 8 fields into existing DB columns
                // You might need to adjust DatabaseHelper to handle these better
                dbHelper.addMeasurement(customerId, selectedGarment, v1, v2, v3, v4, v5, v6, v7, v8, "Added via Profile", "Pending")
                runOnUiThread {
                    Toast.makeText(this, "$selectedGarment saved successfully", Toast.LENGTH_SHORT).show()
                    showMeasurementDisplay(selectedGarment)
                }
            } catch (e: Exception) {
                Log.e("PROFILE_DEBUG", "Save failed", e)
            }
        }
    }

    private fun fetchCustomerData(mobile: String) {
        thread {
            try {
                val cursor = dbHelper.getAllCustomers()
                if (cursor.moveToFirst()) {
                    do {
                        val mobileCol = cursor.getColumnIndex("mobile_number")
                        if (mobileCol != -1 && cursor.getString(mobileCol) == mobile) {
                            customerId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                            runOnUiThread {
                                tvCustomerId.text = "Customer ID: #%03d".format(customerId)
                                // Initial Check for Shirt
                                checkAndShowUI("Shirt")
                            }
                            break
                        }
                    } while (cursor.moveToNext())
                }
                cursor.close()
            } catch (e: Exception) { Log.e("PROFILE_DEBUG", "Error", e) }
        }
    }

    private fun loadLatestMeasurements(type: String) {
        if (customerId == -1) return
        thread {
            try {
                val cursor = dbHelper.getLatestMeasurement(customerId, type)
                if (cursor.moveToFirst()) {
                    val m1 = cursor.getString(cursor.getColumnIndexOrThrow("length"))
                    val m2 = cursor.getString(cursor.getColumnIndexOrThrow("chest"))
                    val m3 = cursor.getString(cursor.getColumnIndexOrThrow("waist"))
                    val m4 = cursor.getString(cursor.getColumnIndexOrThrow("collar"))
                    val m5 = cursor.getString(cursor.getColumnIndexOrThrow("shoulder"))
                    val m6 = cursor.getString(cursor.getColumnIndexOrThrow("sleeve"))
                    val m7 = cursor.getString(cursor.getColumnIndexOrThrow("hip"))
                    val m8 = cursor.getString(cursor.getColumnIndexOrThrow("rise"))
                    val notes = cursor.getString(cursor.getColumnIndexOrThrow("notes"))
                    val status = cursor.getString(cursor.getColumnIndexOrThrow("status"))

                    runOnUiThread {
                        tvMeasurementTitle.text = "$type Measurements"
                        tvStatus.text = status
                        tvNotesValue.text = if (notes.isNullOrEmpty()) "None" else notes
                        updateDisplayLabelsAndValues(type, m1, m2, m3, m4, m5, m6, m7, m8)
                    }
                } else {
                    runOnUiThread {
                        tvMeasurementTitle.text = "$type (No Data)"
                        hideAllContainers()
                        tvNotesValue.text = "None"
                        tvStatus.text = "None"
                    }
                }
                cursor.close()
            } catch (e: Exception) { Log.e("PROFILE_DEBUG", "Load error", e) }
        }
    }

    private fun updateDisplayLabelsAndValues(type: String, m1: String, m2: String, m3: String, m4: String, m5: String, m6: String, m7: String, m8: String) {
        hideAllContainers()
        when (type) {
            "Pant" -> {
                setDisplayField(1, "Waist", m1); setDisplayField(2, "Hip", m2)
                setDisplayField(3, "Inseam", m3); setDisplayField(4, "Outseam", m4)
                setDisplayField(5, "Rise", m5)
            }
            "Shirt" -> {
                setDisplayField(1, "Length", m1); setDisplayField(2, "Chest", m2)
                setDisplayField(3, "Waist", m3); setDisplayField(4, "Collar", m4)
                setDisplayField(5, "Shoulder", m5); setDisplayField(6, "Sleeve", m6)
            }
            "Koti" -> {
                setDisplayField(1, "Length", m1); setDisplayField(2, "Chest", m2)
                setDisplayField(3, "Waist", m3); setDisplayField(4, "Shoulder", m4)
                setDisplayField(5, "Armhole", m5); setDisplayField(6, "Neckline", m6)
            }
            "Suit" -> {
                setDisplayField(1, "Length", m1); setDisplayField(2, "Chest", m2)
                setDisplayField(3, "Waist", m3); setDisplayField(4, "Shoulder", m4)
                setDisplayField(5, "Sleeve", m5); setDisplayField(6, "Inseam", m6)
            }
            "Jabbho" -> {
                setDisplayField(1, "Length", m1); setDisplayField(2, "Chest", m2)
                setDisplayField(3, "Shoulder", m3); setDisplayField(4, "Sleeve", m4)
                setDisplayField(5, "Neck", m5)
            }
            "Lehngho" -> {
                setDisplayField(1, "Waist", m1); setDisplayField(2, "Hip", m2)
                setDisplayField(3, "Inseam", m3); setDisplayField(4, "Outseam", m4)
                setDisplayField(5, "Rise", m5)
            }
            "Safari" -> {
                setDisplayField(1, "Length", m1); setDisplayField(2, "Chest", m2)
                setDisplayField(3, "Waist", m3); setDisplayField(4, "Shoulder", m4)
                setDisplayField(5, "Sleeve", m5); setDisplayField(6, "Neck", m6)
                setDisplayField(7, "Hip", m7); setDisplayField(8, "Inseam", m8)
            }
            "Jodhpuri" -> {
                setDisplayField(1, "Length", m1); setDisplayField(2, "Chest", m2)
                setDisplayField(3, "Waist", m3); setDisplayField(4, "Shoulder", m4)
                setDisplayField(5, "Sleeve", m5); setDisplayField(6, "Neck", m6)
                setDisplayField(7, "Hip", m7)
            }
        }
    }

    private fun setDisplayField(index: Int, label: String, value: String) {
        val container = when(index) {
            1 -> container1; 2 -> container2; 3 -> container3; 4 -> container4
            5 -> container5; 6 -> container6; 7 -> container7; 8 -> container8
            else -> container1
        }
        val labelTv = when(index) {
            1 -> tvLabel1; 2 -> tvLabel2; 3 -> tvLabel3; 4 -> tvLabel4
            5 -> tvLabel5; 6 -> tvLabel6; 7 -> tvLabel7; 8 -> tvLabel8
            else -> tvLabel1
        }
        val valueTv = when(index) {
            1 -> tvValue1; 2 -> tvValue2; 3 -> tvValue3; 4 -> tvValue4
            5 -> tvValue5; 6 -> tvValue6; 7 -> tvValue7; 8 -> tvValue8
            else -> tvValue1
        }
        container.visibility = View.VISIBLE
        labelTv.text = label
        valueTv.text = if (value.isEmpty()) "-" else "$value in"
    }

    private fun hideAllContainers() {
        val containers = listOf(container1, container2, container3, container4, container5, container6, container7, container8)
        containers.forEach { it.visibility = View.GONE }
    }
}