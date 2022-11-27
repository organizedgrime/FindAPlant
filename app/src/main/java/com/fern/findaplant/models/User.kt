package com.fern.findaplant.models

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val id: String? = null,
    val name: String? = null,
    val userName: String? = null,
    val profilePicture: String? = null
)
