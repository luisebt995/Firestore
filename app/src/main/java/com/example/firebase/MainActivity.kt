package com.example.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds

//Monetizacion a traves de Ads
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    private final var TAG = "MainActivity"

    private lateinit var anuncioPop : Anuncios

    private lateinit var editNombre : EditText
    private lateinit var editApellido : EditText
    private lateinit var editEmail : EditText
    private lateinit var btnFirestoreSave : Button
    private lateinit var btnFirestoreRead : Button
    private lateinit var btnFirestoreReadEmail : Button
    private lateinit var btnFirestoreReadApellido : Button
    private lateinit var btnFirestoreDelete : Button
    private lateinit var txtResults : TextView

    private val dbf = Firebase.firestore
    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {}

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mAdView.adListener = object: AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d(TAG, "Ad was closed.")
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
                Log.d(TAG, "Ad failed to load.")
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d(TAG, "Ad was loaded.")
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d(TAG, "Ad was opened.")
            }
        }

        editNombre = findViewById(R.id.editTextNombre)
        editApellido = findViewById(R.id.editTextApellido)
        editEmail = findViewById(R.id.editTextEmail)
        btnFirestoreSave = findViewById(R.id.btnFirestoreSave)
        btnFirestoreRead = findViewById(R.id.btnFirestoreRead)
        btnFirestoreReadEmail = findViewById(R.id.btnFirestoreReadEmail)
        btnFirestoreReadApellido = findViewById(R.id.btnFirestoreReadApellido)
        btnFirestoreDelete = findViewById(R.id.btnFirestoreDelete)
        txtResults = findViewById(R.id.txtResults)

        anuncioPop = Anuncios(this,TAG)

        btnFirestoreSave.setOnClickListener {
            saveFirestore()
        }

        btnFirestoreRead.setOnClickListener {
            readFirestore()
        }

        btnFirestoreReadEmail.setOnClickListener {
            readFirestoreEmail()
        }

        btnFirestoreReadApellido.setOnClickListener {
            readFirestoreApellido()
        }

        btnFirestoreDelete.setOnClickListener {
            deleteFirestore()
        }
    }

    fun loadAd(view: View) {
        anuncioPop.loadAd()
    }

    fun showAd(view: View) {
        anuncioPop.mostrarAd()
    }

    private fun saveFirestore() {
        val user = hashMapOf(
            "Nombre" to editNombre.text.toString(),
            "Apellido" to editApellido.text.toString()
        )
        dbf.collection("Usuarios")
            .document(editEmail.text.toString())
            .set(user)
            .addOnSuccessListener {
                //Log.d(TAG, "DocumentSnapshot added with Email: ${editEmail.text.toString()}")
                Toast.makeText(this, "DocumentSnapshot added with Email: ${editEmail.text.toString()}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                //Log.w(TAG, "Error adding document", e)
                Toast.makeText(this, "Error adding document", Toast.LENGTH_SHORT).show()
            }
    }

    private fun readFirestore () {
        txtResults.text=""
        dbf.collection("Usuarios")
            .get()
            .addOnSuccessListener { result ->
                for (name in result) {
                    txtResults.text = "${txtResults.text} ${name.id} => ${name.data}\n"
                    //Log.d(TAG, "${name.id} => ${name.data}")
                }
            }
    }

    private fun readFirestoreEmail() {
        val email = editEmail.text.toString().trim()
        if (email.isNotEmpty())
        //Muestro en los mismos EditText, el nombre y la provincia del email que introduzca
            dbf.collection("Usuarios")
                .document(email)
                .get()
                .addOnSuccessListener {
                    editNombre.setText(it.get("Nombre") as String?)
                    editApellido.setText(it.get("Apellido") as String?)
            }
    }

    private fun readFirestoreApellido() {
        txtResults.text=""
        val apellido = editApellido.text.toString().trim()

        if(apellido.isNotEmpty()) {
            dbf.collection("Usuarios")
                .whereEqualTo("Apellido", apellido)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val nm = document.getString("Nombre") ?: ""
                        val ap = document.getString("Apellido") ?: ""
                        val em = document.id //Para consultar el ID único, en este caso, el email.

                        txtResults.text = "${txtResults.text} NOMBRE: $nm -- APELLIDO: $ap -- EMAIL: $em \n"
                    }
                }
        }
    }

    private fun deleteFirestore() {
        dbf.collection("Usuarios")
            .document(editEmail.text.toString().trim())
            .delete()
    }
}