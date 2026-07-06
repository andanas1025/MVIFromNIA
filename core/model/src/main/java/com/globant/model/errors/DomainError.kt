package com.globant.model.errors

sealed class DomainError : Throwable() {
    data object NetworkTimeout : DomainError()
    data object NoInternetConnection : DomainError()
    data object ServerMaintenance : DomainError()
    data object ResourceNotFound : DomainError()
    data class Unknown(val originalMessage: String?) : DomainError()
}