package com.example.communityeventmanagement.util

object InputValidation {

    fun isEmailValid(email: String): Boolean {
        val pattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return email.trim().matches(pattern)
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    fun isPhoneValid(phone: String): Boolean {
        val digits = phone.filter { it.isDigit() }
        return digits.length in 8..15
    }

    fun isNameValid(name: String): Boolean {
        return name.trim().length >= 2
    }

    sealed class ValidationResult {
        data object Valid : ValidationResult()
        data class Invalid(val message: String) : ValidationResult()
    }

    fun validateRegisterForm(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): ValidationResult {
        if (!isNameValid(name)) return ValidationResult.Invalid("Nama minimal 2 karakter.")
        if (!isEmailValid(email)) return ValidationResult.Invalid("Format email tidak valid.")
        if (!isPasswordValid(password)) return ValidationResult.Invalid("Password minimal 6 karakter.")
        if (password != confirmPassword) return ValidationResult.Invalid("Konfirmasi password tidak cocok.")
        return ValidationResult.Valid
    }
}