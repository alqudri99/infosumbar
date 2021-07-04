package com.daniel.infosumbar.`interface`

interface DialogListener {
    fun onPositiveButtonClick(email: String = "", password: String = "")
    fun onNegativeButtonClick()
    fun onCloseButtonClick()
    fun onSignUpButtonClick()
}

interface SignLinestener{
    fun onPositiveButtonClick(email: String = "", password: String = "")
    fun onNegativeButtonClick()
    fun onCloseButtonClick()
    fun onSignInButtonClick()
}