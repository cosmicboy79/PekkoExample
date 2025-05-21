/*
 * MIT License
 *
 * Copyright (c) 2025 Cristiano Silva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.pekko.sample.app.data.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.pekko.sample.app.data.definition.Transaction;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test class for singleton {@link TransactionProvider}.
 */
public class TestTransactionProvider {

  private static final int BIG_CHUNK = 5000;
  private static final int SMALL_CHUNK = 5;

  /**
   * GIVEN provider of transaction data WHEN trying to read a big chunk of data THEN all available
   * data in the provider will be returned
   */
  @Test
  public void testReadBigChunkAtOnce() {

    TransactionProvider provider = new TransactionProvider();

    List<Transaction> transactionsRead = provider.readTransactions(BIG_CHUNK);

    assertEquals(provider.sizeOfAvailableData(), transactionsRead.size());
  }

  /**
   * GIVEN provider of transaction data WHEN reading some of the available data AND trying to read a
   * bigger chunk of data THEN only the remaining data is returned
   */
  @Test
  public void testReadBigChuckAfterSomeRead() {

    TransactionProvider provider = new TransactionProvider();

    List<Transaction> transactionsRead = provider.readTransactions(SMALL_CHUNK);

    assertEquals(SMALL_CHUNK, transactionsRead.size());

    List<Transaction> moreTransactions = provider.readTransactions(BIG_CHUNK);

    assertEquals(provider.sizeOfAvailableData() - SMALL_CHUNK, moreTransactions.size());
  }

  /**
   * GIVEN provider of transaction data WHEN reading the data repeatedly in small chunks THEN all
   * data is finally read
   */
  @Test
  public void testReadAllDataInSmallChunks() {

    TransactionProvider provider = new TransactionProvider();

    List<Transaction> transactionsRead1 = provider.readTransactions(SMALL_CHUNK);
    List<Transaction> transactionsRead2 = provider.readTransactions(SMALL_CHUNK);
    List<Transaction> transactionsRead3 = provider.readTransactions(SMALL_CHUNK);
    List<Transaction> transactionsRead4 = provider.readTransactions(SMALL_CHUNK);

    assertEquals(SMALL_CHUNK, transactionsRead1.size());
    assertEquals(SMALL_CHUNK, transactionsRead2.size());
    assertNotEquals(SMALL_CHUNK, transactionsRead3.size());
    assertTrue(transactionsRead4.isEmpty());
    assertTrue(transactionsRead3.size() < SMALL_CHUNK);

    assertNotEquals(transactionsRead1, transactionsRead2);
    assertNotEquals(transactionsRead2, transactionsRead3);
    assertNotEquals(transactionsRead1, transactionsRead3);
  }

  /**
   * GIVEN provider of transaction data WHEN reading all available data AND trying to read more data
   * THEN no data is returned at the second time
   */
  @Test
  public void testReadAfterAllRead() {

    TransactionProvider provider = new TransactionProvider();

    List<Transaction> transactionsRead = provider.readTransactions(provider.sizeOfAvailableData());

    assertEquals(provider.sizeOfAvailableData(), transactionsRead.size());

    List<Transaction> moreTransactions = provider.readTransactions(4);

    assertTrue(moreTransactions.isEmpty());
  }
}