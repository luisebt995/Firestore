package com.example.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text

class Login : AppCompatActivity() {
    //Variables para firebase
    private lateinit var txtUser: EditText
    private lateinit var txtPassword: EditText
    private lateinit var btnGoogle: Button
    private lateinit var progressBarLogin: ProgressBar

    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        txtUser = findViewById(R.id.txtUser)
        txtPassword = findViewById(R.id.txtPassword)
        btnGoogle = findViewById(R.id.btn_google_login)

        progressBarLogin = findViewById(R.id.progressBarLogin)
        auth = FirebaseAuth.getInstance()

        // Configuramos opciones de inicio de sesión de Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.app_id))
                .requestEmail()
                .build()
        // Configuramos el cliente de inicio de sesión de Google
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        btnGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }
    //Función para llamar al login de Google y que el usuario intente logearse
    // luego veremos si se ha podido logear con pnActivityResult
    private fun signInWithGoogle(){
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //Si ha seleccionado cuenta de Google y se ha logeado con éxito, agregamos usuario Google autenticado
    //a nuestro proyecto de Firebase
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            //intentamos logearnos con Google, pero no manejamos la tarea, sino que capturamos
            // la excepción. Luego vemos lo que hacemos en la siguiente funcion
            try {
                // Autenticar con Firebase con la cuenta de Google obtenida
                val account = task.getResult(ApiException::class.java)!!
                //En idToken, tenemos el token que necesitamos para tratar de logearnos en Firebase.
                firebaseAuthWithGoogle(account.idToken!!)
            }
            catch (e: ApiException) {
                // Error al iniciar sesión con Google
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Función para obtener las credenciales del usuario de Google
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) {
            task ->
            if (task.isSuccessful) {
                // Autenticación con Google exitosa, el usuario está conectado
                Toast.makeText(this, "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,MainActivity::class.java))
                }
            else {
                // Fallo en la autenticación con Google
                Toast.makeText(this, "Error en la autenticación con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun forgotPassword(view:View) {
        startActivity(Intent(this,ForgotPass::class.java))
    }
    fun registrar(view:View) {
        startActivity(Intent(this,Registrar::class.java))
    }
    fun login(view:View) {
        loginUser()
    }
    private fun loginUser(){
        val user:String = txtUser.text.toString()
        val password:String = txtPassword.text.toString()
        if(!TextUtils.isEmpty(user) && !TextUtils.isEmpty(password)){
            progressBarLogin.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(user,password).addOnCompleteListener(this){
                task->
                if (task.isSuccessful) {
                    action()
                }else{
                    Toast.makeText(this,"Error en la autenticación",Toast.LENGTH_SHORT).show()
                    progressBarLogin.visibility = View.GONE
                    txtPassword.text.clear()
                }
            }
        }
    }
    private fun action(){
        startActivity(Intent(this,MainActivity::class.java))
    }
}