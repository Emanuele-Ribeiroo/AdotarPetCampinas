package com.adotarpet.campinas

import com.adotarpet.campinas.util.MaskTransformation
import com.adotarpet.campinas.util.Validators
import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {

    @Test
    fun cpf_valido_e_invalido() {
        assertTrue(Validators.isValidCpf("529.982.247-25"))
        assertTrue(Validators.isValidCpf("52998224725"))
        assertFalse(Validators.isValidCpf("529.982.247-24"))
        assertFalse(Validators.isValidCpf("111.111.111-11"))
        assertFalse(Validators.isValidCpf("123"))
    }

    @Test
    fun telefone() {
        assertTrue(Validators.isValidPhone("(19) 91234-5678"))
        assertTrue(Validators.isValidPhone("(19) 3232-0000"))
        assertFalse(Validators.isValidPhone("(19) 81234-5678"))
        assertFalse(Validators.isValidPhone("1234"))
    }

    @Test
    fun cep_e_email() {
        assertTrue(Validators.isValidCep("13000-000"))
        assertFalse(Validators.isValidCep("1300-000"))
        assertTrue(Validators.isValidEmail("ana@email.com"))
        assertFalse(Validators.isValidEmail("ana@email"))
        assertFalse(Validators.isValidEmail("ana.email.com"))
    }

    @Test
    fun mascaras() {
        assertEquals("529.982.247-25", Validators.maskCpf("52998224725"))
        assertEquals("(19) 91234-5678", Validators.maskPhone("19912345678"))
        assertEquals("(19) 3232-0000", Validators.maskPhone("1932320000"))
        assertEquals("13000-000", Validators.maskCep("13000000"))
        assertEquals("13000", Validators.maskCep("13000"))
    }

    @Test
    fun mapeamento_do_cursor_na_mascara() {
        val result = MaskTransformation(Validators::maskPhone).filter(AnnotatedString("1991"))
        assertEquals("(19) 91", result.text.text)
        // cursor depois do 4º dígito fica no fim do texto formatado
        assertEquals(7, result.offsetMapping.originalToTransformed(4))
        assertEquals(4, result.offsetMapping.transformedToOriginal(7))
    }
}
