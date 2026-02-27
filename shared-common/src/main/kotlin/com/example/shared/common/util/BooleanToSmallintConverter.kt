package com.example.shared.common.util

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class BooleanToSmallintConverter : AttributeConverter<Boolean, Short> {

  override fun convertToDatabaseColumn(attribute: Boolean?): Short? {
    return attribute?.let { if (it) 1.toShort() else 0.toShort() }
  }

  override fun convertToEntityAttribute(dbData: Short?): Boolean? {
    return dbData?.let { it.toInt() == 1 }
  }
}