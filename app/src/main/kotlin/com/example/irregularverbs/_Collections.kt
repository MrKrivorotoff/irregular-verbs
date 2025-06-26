package com.example.irregularverbs

inline fun <T> Collection<T>.mapToIntArray(transform: (T) -> Int): IntArray {
    val result = IntArray(size)
    var index = 0
    for (element in this)
        result[index++] = transform(element)
    return result
}

inline fun <reified T> List<T>.copyAllExceptIndexToArray(index: Int): Array<T> =
    Array(size - 1) { i -> if (i < index) get(i) else get(i + 1) }