import java.text.NumberFormat

class CottlinStore {
    val format: NumberFormat = NumberFormat.getCurrencyInstance()
    var goods: MutableMap<String, Double> = mutableMapOf()

    class SpecialOffer(var itemsBought: Int, var itemsCharged: Int)

    var specials: MutableMap<String, SpecialOffer> = mutableMapOf()

    fun start(): Unit {
        println("Welcome to the Cottlin Orders System!")
        goods["apple"] = 0.60
        goods["orange"] = 0.25
        specials["apple"] = SpecialOffer(2, 1)
        specials["orange"] = SpecialOffer(3, 2)
    }

    fun takeOrder(): Unit {
        println("We are selling these items:")
        for ((item, price) in goods) {
            print("   $item: ${format.format(price)}   ")
            var special = specials.getOrDefault(item, null)
            special?.let {
                print(" (${special.itemsBought} for ${special.itemsCharged})")
            }
            println()
        }
        println("Please enter a space separated list of goods to order from this list:")
        val order = readLine()!!.split(' ').toMutableList()
        val total = parseOrder(order)

        println("Your total comes to ${format.format(total)}.")
    }

    fun parseOrder(order: MutableList<String>): Double {
        var itemCounts : MutableMap<String, Int> = mutableMapOf()

        var total: Double = 0.0

        // count amount of each item purchased
        order.forEach {
            val count = itemCounts.getOrDefault(it, 0) + 1
            itemCounts[it] = count
        }
        // apply discounts where possible, then add any items at full price that were not on special or that weren't enough for a given special
        itemCounts.forEach {
            item, count ->
            val itemPrice = goods.getOrDefault(item, 0.0)
            val special = specials.getOrDefault(item, null)
            if (special == null) {
                total += itemPrice * count
            }
            else {
                // how many full sets purchased at discount
                val disCount = count / special.itemsBought
                total += disCount * special.itemsCharged * itemPrice

                // how many left over at full price
                val fullCount = count % special.itemsBought
                total += fullCount * itemPrice
            }
        }
        return total
    }
}
