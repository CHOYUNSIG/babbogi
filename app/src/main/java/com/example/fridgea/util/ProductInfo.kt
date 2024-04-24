package com.example.fridgea.util

// 상품 정보
data class ProductInfo(
    // 등록 상품 정보
    val name: String,
    val barcode: String,
    // 영양 정보
    val nutrition: NutritionInfo?
)

// 상품의 영양 정보
data class NutritionInfo(
    val servingVolume: Float,
    val servingUnit: Float,
    val calorie: Float,
    val carbohydrate: Float,
    val protein: Float,
    val fat: Float,
    val sugar: Float,
    val salt: Float,
    val cholesterol: Float,
    val saturated_fatty_acids: Float,
    val trans_fat: Float,
) {
    override fun toString(): String = arrayOf(
        "Calorie: ${calorie}kcal",
        "Carbohydrate: ${carbohydrate}g",
        "Protein: ${protein}g",
        "Fat: ${fat}g",
        "Sugar: ${sugar}g",
        "Salt: ${salt}mg",
        "Cholesterol: ${cholesterol}mg",
        "Saturated Fatty Acids: ${saturated_fatty_acids}g",
        "Trans Fat: ${trans_fat}g"
    ).joinToString("\n")
}