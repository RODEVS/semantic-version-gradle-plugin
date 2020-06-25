package com.semanticversion.gradle.plugin.common

import com.semanticversion.gradle.plugin.commons.PropertyResolver

class FakePropertyResolver(private val properties: Map<String, String> = emptyMap()) : PropertyResolver {

    override fun getStringProp(propertyName: String, defaultValue: String?): String? {
        return properties.getOrDefault(propertyName, defaultValue)
    }

    override fun getRequiredStringProp(propertyName: String): String {
        return properties[propertyName].toString()
    }

    override fun getRequiredStringProp(propertyName: String, defaultValue: String): String {
        return properties.getOrDefault(propertyName, defaultValue)
    }

    override fun getRequiredBooleanProp(propertyName: String, defaultValue: Boolean): Boolean {
        return properties.getOrDefault(propertyName, defaultValue).toString().toBoolean()
    }

    override fun getRequiredIntegerProp(propertyName: String): Int {
        return properties[propertyName].toString().toInt()
    }

    override fun getRequiredIntegerProp(propertyName: String, defaultValue: Int): Int {
        return properties.getOrDefault(propertyName, defaultValue).toString().toInt()
    }

    override fun getIntegerProp(propertyName: String, defaultValue: Int?): Int? {
        return properties.getOrDefault(propertyName, defaultValue).toString().toIntOrNull()
    }

    override fun getDoubleProp(propertyName: String, defaultValue: Double?): Double? {
        return properties.getOrDefault(propertyName, defaultValue).toString().toDoubleOrNull()
    }

    override fun getStringListProp(propertyName: String, defaultValue: List<String>?): List<String>? {
        return defaultValue
    }
}
