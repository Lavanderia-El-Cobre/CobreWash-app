package com.elcobre.lavanderiaelcobre.ui

import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * Busca el primer elemento que cumple [predicate] y lo reemplaza en su índice por el resultado de
 * [transform]; no realiza cambios si no hay coincidencia.
 *
 * Al mutar una única posición sobre modelos inmutables, [SnapshotStateList] notifica solo el índice
 * afectado y evita reconstruir toda la lista en Compose.
 */
internal inline fun <T> SnapshotStateList<T>.reemplazarPrimero(predicate: (T) -> Boolean, transform: (T) -> T) {
    val i = indexOfFirst(predicate)
    if (i >= 0) this[i] = transform(this[i])
}
