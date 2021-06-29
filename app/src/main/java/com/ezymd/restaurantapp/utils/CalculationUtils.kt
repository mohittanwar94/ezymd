package com.ezymd.restaurantapp.utils

import android.content.Context
import android.text.TextUtils
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.itemdetail.model.Modifier

class CalculationUtils {
    fun processCartData(arrayList: ArrayList<ItemModel>): Pair<Int, Double> {
        var quantity = 0
        var total = 0.0
        for (itemModel in arrayList) {
            var tempPrice = getModifierPrice(itemModel.price, itemModel.listModifiers)
            tempPrice = ((itemModel.price + tempPrice) * itemModel.quantity)
            quantity += itemModel.quantity
            total += tempPrice
            itemModel.total = tempPrice
        }


        return Pair(quantity, total)

    }

    fun getModifierPrice(price: Double, listModifiers: ArrayList<Modifier>): Double {
        var tempPrice = 0.0
        for (mod in listModifiers) {
            when (mod.operator) {
                "+" -> tempPrice += mod.price
                "-" -> tempPrice -= mod.price
                "*" -> tempPrice *= mod.price
            }
        }
        return tempPrice

    }

    fun getModifierPrice(price: Double, mod: Modifier): Double {
        var tempPrice = price
        when (mod.operator) {
            "+" -> tempPrice += mod.price
            "-" -> tempPrice -= mod.price
            "*" -> tempPrice *= mod.price
        }
        return tempPrice

    }

    fun getPriceText(
        context: Context,
        quantityCount: Int,
        price: Double,
        disCount: Double
    ): CharSequence? {
        return TextUtils.concat(
            "" + quantityCount,
            " ",
            context.getString(R.string.items),
            " | ",
            context.getString(R.string.dollor),
            String.format("%.2f", (price-disCount))
        )
    }
}