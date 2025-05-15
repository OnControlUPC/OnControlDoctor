package oncontroldoctor.upc.edu.pe.data.model

import oncontroldoctor.upc.edu.pe.domain.model.Doctor

data class ProfileResponse(
    val user: UserResponse,
    val email: String?,
)

data class UserResponse(
    val id: Int?,
    val typeUser: String?,
    val name: String?,
    val lastname: String?,
    val dni: String?
) {
    fun toDoctor(): Doctor {
        return Doctor(
            fullName = "$name $lastname",
            email = "",
            dni = dni ?: ""
        )
    }
}