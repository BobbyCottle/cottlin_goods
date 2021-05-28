import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import java.text.NumberFormat

class CottlinStore(_channel: SendChannel<ChannelMessage>) {
    private val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance()
    private val scope = CoroutineScope(Job())
    private val channel = _channel

    var goods: MutableMap<String, Double> = mutableMapOf()

    class SpecialOffer(var itemsBought: Int, var itemsCharged: Int)

    var specials: MutableMap<String, SpecialOffer> = mutableMapOf()

    init {
        println("Welcome to the Cottlin Orders System!")
        goods["apple"] = 0.60
        goods["orange"] = 0.25
        specials["apple"] = SpecialOffer(2, 1)
        specials["orange"] = SpecialOffer(3, 2)
    }

    fun start() {
        println("Store: starting shop...")
        while (!channel.isClosedForSend) {
            var order = takeOrder()
            if (order.isEmpty()) {
                channel.close()
                break
            }
            processOrder(order)
        }
        println("Store: The Cottlin Orders System is now closed.")
    }

    private fun takeOrder(): List<String> {
        println("We are selling these items:")
        for ((item, price) in goods) {
            print("   $item: ${currencyFormatter.format(price)}   ")
            val special = specials.getOrDefault(item, null)
            special?.let {
                print(" (${special.itemsBought} for ${special.itemsCharged})")
            }
            println()
        }
        println("Please enter a space separated list of goods to order from this list:")

        // ensure no null values
        val orderLine = readLine() ?: ""
        if (orderLine.isBlank()) {
            return emptyList()
        }
        return orderLine.split(' ')
    }

    private fun processOrder(order: List<String>) {
        val (total, badItem) = parseOrder(order)

        scope.launch {
            if (total < 0.0) {
                sendEvent("FAILED", total, "The order contained invalid item [$badItem].")
            }
            else {
                println("Your total comes to ${currencyFormatter.format(total)}.")
                sendEvent("SUCCESS", total, null)
            }
        }
    }

    /**
     * Parse the order for valid items and calculate costs, including special offers.
     *
     * If any items are invalid, reject order.
     */
    fun parseOrder(order: List<String>): Pair<Double, String> {
        val itemCounts: MutableMap<String, Int> = mutableMapOf()
        var total = 0.0

        // count amount of each item purchased
        order.forEach {
            val count = itemCounts.getOrDefault(it, 0) + 1
            itemCounts[it] = count
        }
        // apply discounts where possible, then add any items at full price that were not on special or that weren't enough for a given special
        for ((item, count) in itemCounts) {
            // invalid items will void entire order
            val itemPrice = goods.getOrDefault(item, 0.0)
            if (itemPrice == 0.0) {
                return Pair(-1.0, item)
            }
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
        return Pair(total, "OK")
    }

    private suspend fun sendEvent(type: String, total: Double, msg: String?) {
        if (channel.isClosedForSend) {
            return
        }
        val cm = ChannelMessage(type, total, msg)
        channel.send(cm)
    }
}
