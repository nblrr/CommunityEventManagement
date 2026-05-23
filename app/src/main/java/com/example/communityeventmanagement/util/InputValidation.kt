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
        data class Invalid(val messageRes: Int) : ValidationResult()
    }

    fun validateRegisterForm(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): ValidationResult {
        if (!isNameValid(name)) return ValidationResult.Invalid(com.example.communityeventmanagement.R.string.error_name_too_short)
        if (!isEmailValid(email)) return ValidationResult.Invalid(com.example.communityeventmanagement.R.string.error_invalid_email)
        if (!isPasswordValid(password)) return ValidationResult.Invalid(com.example.communityeventmanagement.R.string.error_password_too_short)
        if (password != confirmPassword) return ValidationResult.Invalid(com.example.communityeventmanagement.R.string.error_passwords_dont_match)
        return ValidationResult.Valid
    }

    fun validateLoginForm(
        email: String,
        password: String
    ): ValidationResult {
        if (!isEmailValid(email)) return ValidationResult.Invalid(com.example.communityeventmanagement.R.string.error_invalid_email)
        if (password.isBlank()) return ValidationResult.Invalid(com.example.communityeventmanagement.R.string.error_invalid_credentials)
        return ValidationResult.Valid
    }
}
