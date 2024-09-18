package br.com.faculdadeimpacta.postmonasynctask

import android.os.AsyncTask
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.faculdadeimpacta.postmonasynctask.databinding.ActivityMainBinding
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()

        binding.buttonConsultar.setOnClickListener {
            val consultarApi = PostmonApi()
            consultarApi.execute(binding.editTextCEP.text.toString())
        }
    }

    inner class PostmonApi : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg p0: String?): String {
            val cepDigitado = p0[0]!!

            val apiURL = URL("https://api.postmon.com.br/v1/cep/${cepDigitado}")

            (apiURL.openConnection() as HttpURLConnection).let { conexao ->
                conexao.requestMethod = "GET"
                conexao.connect()

                val stream = if (conexao.responseCode == HttpURLConnection.HTTP_OK) {
                    conexao.inputStream
                } else {
                    conexao.errorStream
                }

                return stream.reader().readText()
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            binding.textViewResposta.text = result!!
        }
    }
}