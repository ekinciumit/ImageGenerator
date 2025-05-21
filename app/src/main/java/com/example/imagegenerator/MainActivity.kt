package com.example.imagegenerator

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imagegenerator.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Formatter
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var imageAdapter: ImageAdapter
    private val imageUrls = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imageAdapter = ImageAdapter(imageUrls)
        binding.recyclerView.adapter = imageAdapter

        binding.button.setOnClickListener {
            promtCall()
        }
    }

    private fun promtCall() {
        // Unix time veya rastgele bir integer değer
        val plainNonce = System.currentTimeMillis()

        // HMAC-SHA256 hesaplama
        val signingKey = SecretKeySpec(YOUR_API_KEY.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(signingKey)

        val bytes = mac.doFinal((YOUR_API_SECRET + plainNonce).toByteArray())
        val formatter = Formatter()
        bytes.forEach { formatter.format("%02x", it) }
        val signatureHex = formatter.toString().lowercase()

        // Retrofit ve Logging Interceptor
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.wiro.ai")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PromptAPI::class.java)

        val prompt = binding.editText.text.toString()
        val randomSeed = Random.nextInt(0, Int.MAX_VALUE)  // 0 ile Int.MAX_VALUE arasında rastgele bir sayı oluştur

        val request = PromptRequest(
            prompt,
            "blurry, low quality, distorted, artifacts, worst quality, low resolution, bad anatomy, deformed, extra limbs, mutated, fused faces, disfigured",
            "4",
            "50",
            "10",
            randomSeed.toString(),
            "0",
            "1024",
            "1024",
            "EulerAncestralDiscreteScheduler"
        )

        val call: Call<PromptResponse> = retrofit.getPromptData(
            "application/json",
            YOUR_API_KEY,
            plainNonce.toString(),
            signatureHex,
            request
        )

        call.enqueue(object : Callback<PromptResponse> {
            override fun onResponse(
                call: Call<PromptResponse>,
                response: Response<PromptResponse>,
            ) {
                if (response.isSuccessful) {
                    val responseApi = response.body()
                    Log.d("API_RESPONSE", "İstek başarılı. Cevap: $responseApi")
                    Toast.makeText(
                        this@MainActivity,
                        "İstek Gönderildi, Cevap Bekleniyor...",
                        Toast.LENGTH_LONG
                    ).show()

                    binding.progressBar.visibility = View.VISIBLE

                    handler = Handler(Looper.getMainLooper())

                    runnable = Runnable {
                        detailCall(responseApi?.taskid.toString())
                        handler.postDelayed(runnable, 2000)
                    }

                    handler.post(runnable)

                } else {
                    Log.e("API_ERROR", "Hata kodu: ${response.code()}, Hata mesajı: ${response.errorBody()?.string()}")
                    Toast.makeText(
                        this@MainActivity,
                        "API isteği başarısız. Kod: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<PromptResponse>, t: Throwable) {
                Log.e("API_ERROR", "İstek başarısız: ${t.message}")
                Toast.makeText(
                    this@MainActivity,
                    "İstek başarısız. Hata: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    private fun detailCall(task: String) {
        val plainNonce = System.currentTimeMillis()

        val signingKey = SecretKeySpec(YOUR_API_KEY.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(signingKey)

        val bytes = mac.doFinal((YOUR_API_SECRET + plainNonce).toByteArray())
        val formatter = Formatter()
        bytes.forEach { formatter.format("%02x", it) }
        val signatureHex = formatter.toString().lowercase()

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.wiro.ai")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DetailAPI::class.java)

        val call: Call<TaskListResponse> = retrofit.getDetailData(
            "application/json",
            YOUR_API_KEY,
            plainNonce.toString(),
            signatureHex,
            DetailRequest(taskid = task)
        )

        call.enqueue(object : Callback<TaskListResponse> {
            override fun onResponse(
                call: Call<TaskListResponse>,
                response: Response<TaskListResponse>,
            ) {
                if (response.isSuccessful) {
                    val responseApi = response.body()
                    Log.d("DETAIL_API_RESPONSE", "Başarılı cevap: $responseApi")
                    val newImageUrls = mutableListOf<String>() // Yeni URL'ler için bir liste oluştur

                    for (task in responseApi?.tasklist!!) {
                        for (a in task.outputs) {
                            Log.d("URL_DEBUG", "URL: ${a.url}")
                            if (a.url.isNotEmpty()) {
                                newImageUrls.add(a.url) // Yeni URL'yi listeye ekle
                            }
                        }
                    }

                    // Yeni resimler geldiyse, imageUrls listesini güncelle
                    if (newImageUrls.isNotEmpty()) {
                        imageAdapter.updateImages(newImageUrls) // Adapter'ı güncelle
                        binding.progressBar.visibility = View.GONE
                    }
                } else {
                    Log.e("DETAIL_API_ERROR", "Hata kodu: ${response.code()}, Hata mesajı: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<TaskListResponse>, t: Throwable) {
                Log.e("DETAIL_API_ERROR", "İstek başarısız: ${t.message}")
            }
        })
    }
}
