package com.example.irregularverbs

inline fun <T> Collection<T>.mapToIntArray(transform: (T) -> Int): IntArray {
    val result = IntArray(size)
    var index = 0
    for (element in this)
        result[index++] = transform(element)
    return result
}