package com.programmersbox.loged

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.annotation.IntRange
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlin.random.Random


/**
 * Finds similarities between two lists based on a predicate
 */
fun <T, U> Collection<T>.intersect(uList: List<U>, filterPredicate: (T, U) -> Boolean) = filter { m -> uList.any { filterPredicate(m, it) } }

/**
 * Returns [this] if not null, otherwise return [nullBlock]
 */
inline fun <T> T?.otherWise(nullBlock: () -> T) = this ?: nullBlock()

/**
 * Returns [this] if not null, else return [item]
 */
fun <T : Any> T?.orElse(item: T) = this ?: item

/**
 * converts [this] to a Json string
 */
fun Any?.toJson(): String = Gson().toJson(this)

/**
 * converts [this] to a Json string but its formatted nicely
 */
fun Any?.toPrettyJson(): String = GsonBuilder().setPrettyPrinting().create().toJson(this)

/**
 * Takes [this] and coverts it to an object
 */
inline fun <reified T> String?.fromJson(): T? = try {
    Gson().fromJson(this, object : TypeToken<T>() {}.type)
} catch (e: Exception) {
    null
}

fun <T> SharedPreferences.Editor.putObject(key: String, value: T): SharedPreferences.Editor = putString(key, Gson().toJson(value))

inline fun <reified T> SharedPreferences.getObject(key: String, defaultValue: T? = null): T? = try {
    Gson().fromJson(getString(key, null), T::class.java) ?: defaultValue
} catch (e: Exception) {
    defaultValue
}

inline fun <reified T> SharedPreferences.getCollection(key: String, defaultValue: T? = null): T? = try {
    Gson().fromJson(getString(key, null), object : TypeToken<T>() {}.type) ?: defaultValue
} catch (e: Exception) {
    defaultValue
}

fun <T> Intent.putExtra(key: String, value: T): Intent = putExtra(key, Gson().toJson(value))

inline fun <reified T> Intent.getObjectExtra(key: String, defaultValue: T? = null): T? = try {
    Gson().fromJson(getStringExtra(key), T::class.java) ?: defaultValue
} catch (e: Exception) {
    defaultValue
}

inline fun <reified T> Intent.getCollectionExtra(key: String, defaultValue: T): T = try {
    Gson().fromJson(getStringExtra(key), object : TypeToken<T>() {}.type) ?: defaultValue
} catch (e: Exception) {
    defaultValue
}

/**
 * returns a random color
 */
fun Random.nextColor(
    @IntRange(from = 0, to = 255) alpha: Int = nextInt(0, 255),
    @IntRange(from = 0, to = 255) red: Int = nextInt(0, 255),
    @IntRange(from = 0, to = 255) green: Int = nextInt(0, 255),
    @IntRange(from = 0, to = 255) blue: Int = nextInt(0, 255)
): Int = Color.argb(alpha, red, green, blue)

data class DeviceInfo(val board: String = Build.BOARD,
                      val brand: String = Build.BRAND,
                      val device: String = Build.DEVICE,
                      val manufacturer: String = Build.MANUFACTURER,
                      val model: String = Build.MODEL,
                      val product: String = Build.PRODUCT,
                      val sdkInt: Int = Build.VERSION.SDK_INT,
                      val versionCode: String = Build.VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name,
                      val versionNumber: String = Build.VERSION.RELEASE)
