package com.adotarpet.campinas.model

import android.net.Uri

/** Foto ou vídeo escolhido pelo usuário em um formulário. */
data class Attachment(val uri: Uri, val name: String)
