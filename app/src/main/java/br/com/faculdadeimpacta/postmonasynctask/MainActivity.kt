package br.com.faculdadeimpacta.postmonasynctask

import android.os.AsyncTask
import android.os.Bundle
import android.util.JsonReader
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.faculdadeimpacta.postmonasynctask.data.model.CEP
import br.com.faculdadeimpacta.postmonasynctask.data.model.CidadeInfo
import br.com.faculdadeimpacta.postmonasynctask.data.model.EstadoInfo
import br.com.faculdadeimpacta.postmonasynctask.databinding.ActivityMainBinding
import org.json.JSONObject
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
        private var cep: CEP? = null
        override fun doInBackground(vararg p0: String?): String {
            val cepDigitado = p0[0]!!

            val apiURL = URL("https://api.postmon.com.br/v1/cep/${cepDigitado}")

            (apiURL.openConnection() as HttpURLConnection).let { conexao ->
                conexao.requestMethod = "GET"
                conexao.connect()

                if (conexao.responseCode != HttpURLConnection.HTTP_OK) {
                    return conexao.errorStream.reader().readText()
                }

                val respostaString = conexao.inputStream.reader().readText()

                val jsonResposta = JSONObject(respostaString)

                cep = CEP(
                    bairro = jsonResposta.getString("bairro"),
                    cep = jsonResposta.getString("cep"),
                    cidade = jsonResposta.getString("cidade"),
                    cidade_info = CidadeInfo(
                        area_km2 = jsonResposta.getJSONObject("cidade_info").getString("area_km2"),
                        codigo_ibge = jsonResposta.getJSONObject("cidade_info")
                            .getString("codigo_ibge")
                    ),
                    complemento = if (jsonResposta.has("complemento")) jsonResposta.getString("complemento") else null,
                    estado = jsonResposta.getString("estado"),
                    estado_info = EstadoInfo(
                        area_km2 = jsonResposta.getJSONObject("estado_info").getString("area_km2"),
                        codigo_ibge = jsonResposta.getJSONObject("estado_info")
                            .getString("codigo_ibge"),
                        nome = jsonResposta.getJSONObject("estado_info").getString("nome")
                    ),
                    logradouro = jsonResposta.getString("logradouro")
                )
            }
            return cep.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            binding.textViewResposta.text = result!!
        }
    }
}