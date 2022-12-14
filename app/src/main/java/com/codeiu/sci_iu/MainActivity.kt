package com.codeiu.sci_iu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.codeiu.sci_iu.databinding.ActivityMainBinding
import com.codeiu.sci_iu.extras.GobalVariables
import com.codeiu.sci_iu.extras.Models.*
import com.codeiu.sci_iu.activities.HomeActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        Picasso
            .get()
            .load("https://carta.v-card.es/assets/images/user/login.png")
            .fit()
            .into(binding.imgLogin)

        binding.btnLogin.setOnClickListener{
            validateCredentials()
        }
    }

    private fun validateCredentials(){
        val tfEmail = binding.tfEmail.editText?.text.toString()
        val tfPass = binding.tfPass.editText?.text.toString()

        if (tfEmail.equals("") || tfPass.equals("")){
            Toast.makeText(this@MainActivity, "Contesta todos los campos", Toast.LENGTH_SHORT).show()
        } else {
            println("tfEmail"+tfEmail)
            validateAccess()
        }
    }

    private fun validateAccess() {
        val url = GobalVariables.url_login
        var formBody: RequestBody = FormBody.Builder()
            .add("email", binding.tfEmail.editText?.text.toString())
            .add("password", binding.tfPass.editText?.text.toString())
            .build()

        val req = Request.Builder().url(url).post(formBody).build()
        val client = OkHttpClient()
        var gson = Gson()

        client.newCall(req).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(this@MainActivity, "Error desconocido", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                val resp = response.body?.string()
                var objResp = gson.fromJson<LoginResponse>(resp, LoginResponse::class.java)

                if (objResp.success == true){
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, objResp.message, Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@MainActivity,HomeActivity::class.java)
                        intent.putExtra("username", objResp.data?.name)
                        startActivity(intent)

                    }
                }
                else {
                    runOnUiThread{
                        Toast.makeText(this@MainActivity, objResp.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

    }
}