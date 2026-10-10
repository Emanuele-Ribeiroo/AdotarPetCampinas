package com.adotarpet.campinas.util

/** Máscaras (formatam enquanto o usuário digita) e validações dos formulários. */
object Validators {

    private fun digits(text: String, max: Int) = text.filter { it.isDigit() }.take(max)

    fun maskCpf(text: String): String {
        val d = digits(text, 11)
        return buildString {
            d.forEachIndexed { i, c ->
                if (i == 3 || i == 6) append('.')
                if (i == 9) append('-')
                append(c)
            }
        }
    }

    fun maskPhone(text: String): String {
        val d = digits(text, 11)
        return buildString {
            d.forEachIndexed { i, c ->
                if (i == 0) append('(')
                if (i == 2) append(") ")
                if ((d.length == 11 && i == 7) || (d.length < 11 && i == 6)) append('-')
                append(c)
            }
        }
    }

    fun maskCep(text: String): String {
        val d = digits(text, 8)
        return if (d.length > 5) d.substring(0, 5) + "-" + d.substring(5) else d
    }

    fun isValidCpf(text: String): Boolean {
        val d = text.filter { it.isDigit() }
        if (d.length != 11 || d.all { it == d[0] }) return false
        fun check(len: Int): Int {
            val sum = (0 until len).sumOf { (d[it] - '0') * (len + 1 - it) }
            val r = sum * 10 % 11
            return if (r == 10) 0 else r
        }
        return check(9) == d[9] - '0' && check(10) == d[10] - '0'
    }

    fun isValidPhone(text: String): Boolean {
        val d = text.filter { it.isDigit() }
        return d.length == 11 && d[2] == '9' || d.length == 10
    }

    fun isValidCep(text: String) = text.count { it.isDigit() } == 8

    fun isValidEmail(text: String) = EMAIL_REGEX.matches(text.trim())

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")

    /** Mantém só os dígitos digitados (o que o estado guarda; a máscara é só visual). */
    fun onlyCpf(text: String) = digits(text, 11)
    fun onlyPhone(text: String) = digits(text, 11)
    fun onlyCep(text: String) = digits(text, 8)

    val cpfTransformation = MaskTransformation(::maskCpf)
    val phoneTransformation = MaskTransformation(::maskPhone)
    val cepTransformation = MaskTransformation(::maskCep)
}
