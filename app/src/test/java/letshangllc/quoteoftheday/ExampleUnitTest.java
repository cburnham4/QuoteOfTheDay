package letshangllc.quoteoftheday;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void localQuotes_areAvailableOffline() {
        assertTrue(LocalQuotes.size() > 0);
        String quote = LocalQuotes.randomQuote();
        assertNotNull(quote);
        assertFalse(quote.trim().isEmpty());
    }
}
