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
        var item1List = mutableListOf("banana", "orange", "apple", "apple", "apple")
        var item2List = mutableListOf("apple", "")
        assertEquals(2.05, store.parseOrder(item1List))
        assertEquals(0.6, store.parseOrder(item2List))
    }
}