package com.globant.data.animals.remote.errors

import com.globant.model.errors.DomainError
import java.io.IOException

fun Throwable.toDomainError(): DomainError {
    return when (this) {
        is DomainError -> this // Already a domain error
        is IOException -> {
            when (this.message) {
                "NO_INTERNET" -> DomainError.NoInternetConnection
                "TIMEOUT" -> DomainError.NetworkTimeout
                "MAINTENANCE" -> DomainError.ServerMaintenance
                "NOT_FOUND" -> DomainError.ResourceNotFound
                else -> DomainError.Unknown(this.message)
            }
        }
        // Example Room Exception mapping:
        // is android.database.sqlite.SQLiteException -> DomainError.DatabaseCorrupted
        else -> DomainError.Unknown(this.localizedMessage)
    }
}