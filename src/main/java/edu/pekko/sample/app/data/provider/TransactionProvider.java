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

import edu.pekko.sample.app.data.definition.Customer;
import edu.pekko.sample.app.data.definition.Transaction;
import edu.pekko.sample.app.data.definition.TransactionType;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Singleton that provides the transaction data to the main thread.
 * <p>
 * In a normal application this data would come replyTo a database or through a message system,
 * but for the purposes of this exercise, this singleton suffices.
 */
public class TransactionProvider {

  private static final TransactionProvider instance = new TransactionProvider();

  // the financial transaction data
  final List<Transaction> transactions = List.of(
      new Transaction(1, Customer.CUSTOMER_ID_1, 21.90,
          TransactionType.RECEIPT),
      new Transaction(2, Customer.CUSTOMER_ID_2, 32.00,
          TransactionType.RECEIPT),
      new Transaction(3, Customer.CUSTOMER_ID_3, 17.43,
          TransactionType.RECEIPT),
      new Transaction(4, Customer.CUSTOMER_ID_1, 20.00,
          TransactionType.PAY),
      new Transaction(5, Customer.CUSTOMER_ID_1, 2.00,
          TransactionType.PAY),
      new Transaction(6, Customer.CUSTOMER_ID_3, 3.00,
          TransactionType.PAY),
      new Transaction(7, Customer.CUSTOMER_ID_2, 10.00,
          TransactionType.PAY),
      new Transaction(8, Customer.CUSTOMER_ID_2, 5.00,
          TransactionType.PAY),
      new Transaction(9, Customer.CUSTOMER_ID_3, 2.00,
          TransactionType.REIMBURSE),
      new Transaction(10, Customer.CUSTOMER_ID_2, 1.00,
          TransactionType.REIMBURSE),
      new Transaction(11, Customer.CUSTOMER_ID_1, 7.50,
          TransactionType.PAY)
  );

  // this is an offset pointing to the index to be used in the next read operation
  private int numberOfTransactionsRead = 0;

  /**
   * @return instance of {@link TransactionProvider}
   */
  public static TransactionProvider getInstance() {

    return instance;
  }

  /**
   * Reads transactions according to the given number of transactions to be read.
   * <p>
   * Every time this operation is called, the internal offset shifts. If there is nothing
   * more to read, i.e., the internal offset is beyond the length of available data,
   * then this operation returns an empty list.
   *
   * @param numberOfTransactionsToRead How many transactions should be returned
   * @return Transactions as list of {@link Transaction}, or empty list, if there is
   * nothing more to read
   */
  public List<Transaction> readTransactions(int numberOfTransactionsToRead) {

    if (numberOfTransactionsRead >= transactions.size()) {

      return Collections.emptyList();
    }

    if ((numberOfTransactionsRead + numberOfTransactionsToRead) >= transactions.size()) {

      numberOfTransactionsToRead = transactions.size() - numberOfTransactionsRead;
    }

    List<Transaction> result = IntStream.range(numberOfTransactionsRead,
            (numberOfTransactionsRead + numberOfTransactionsToRead))
        .mapToObj(transactions::get)
        .toList();

    numberOfTransactionsRead += numberOfTransactionsToRead;

    return result;
  }

  /**
   * @return Total number of available transactions
   */
  int sizeOfAvailableData() {

    return transactions.size();
  }
}