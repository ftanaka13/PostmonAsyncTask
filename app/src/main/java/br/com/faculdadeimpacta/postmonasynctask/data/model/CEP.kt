package br.com.faculdadeimpacta.postmonasynctask.data.model

data class CEP(
    val bairro: String,
    val cep: String,
    val cidade: String,
    val cidade_info: CidadeInfo,
    val complemento: String?,
    val estado: String,
    val estado_info: EstadoInfo,
    val logradouro: String
)