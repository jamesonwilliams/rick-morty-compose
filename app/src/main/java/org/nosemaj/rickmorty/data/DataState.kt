package org.nosemaj.rickmorty.data

sealed class DataState<T> {
    data class Content<T>(val data: T) : DataState<T>()

    data class Error<T>(val error: Throwable) : DataState<T>()
}
