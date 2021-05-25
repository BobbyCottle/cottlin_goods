import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class CottlinStoreTest {
    var store: CottlinStore = CottlinStore()

    @BeforeEach
    fun setUp() {
        store.start()
    }

    @Test
    fun testParseOrder() {
        var item1List = mutableListOf("banana", "orange", "orange", "apple")
        var item2List = mutableListOf("apple", "")
        assertEquals(0.5 + 0.6, store.parseOrder(item1List))
        assertEquals(0.6, store.parseOrder(item2List))
    }

    @Test
    fun testParseOrderWithSpecials() {
        // 3 oranges, 4 apples
        var item1List = mutableListOf("orange", "orange", "orange", "apple", "apple", "apple", "apple")
        assertEquals(0.5 + 1.2, store.parseOrder(item1List))

        // 5 oranges, 3 apples
        var item2List = mutableListOf("orange", "orange", "orange", "orange", "orange", "apple", "apple", "apple")
        assertEquals(1.0 + 1.2, store.parseOrder(item2List))
    }
}