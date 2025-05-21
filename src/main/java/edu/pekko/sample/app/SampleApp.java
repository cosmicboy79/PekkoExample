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

package edu.pekko.sample.app;

import static scala.concurrent.duration.Duration.Inf;

import edu.pekko.sample.app.actor.TransactionsActor;
import edu.pekko.sample.app.actor.TransactionsActor.AllTransactionsProcessed;
import edu.pekko.sample.app.actor.TransactionsActor.Event;
import edu.pekko.sample.app.actor.TransactionsActor.TransactionsToProcess;
import edu.pekko.sample.app.data.definition.Transaction;
import edu.pekko.sample.app.data.provider.TransactionProvider;
import edu.pekko.sample.app.utils.CustomSystemOut;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.actor.typed.javadsl.AskPattern;
import scala.concurrent.Await;

/**
 * Main Application: it reads financial transactions repeatedly and sends them for processing to a
 * Bulk Actor via Actor System.
 */
public class SampleApp {

  private static final int NUMBER_OF_TRANSACTIONS_TO_READ = 5;

  public static void main(String[] args)
      throws InterruptedException, TimeoutException, ExecutionException {

    // getting the Actor System for this application
    ActorSystem<Event> actorSystem = ActorSystem.create(TransactionsActor.create(),
        "PekkoSampleApp");

    // system is also the ActorRef to the guardian actor
    // as per https://pekko.apache.org/docs/pekko/current/typed/interaction-patterns.html#fire-and-forget
    ActorRef<Event> transactionsActor = actorSystem;

    // reading first chunk of financial data
    List<Transaction> transactions = TransactionProvider.getInstance()
        .readTransactions(NUMBER_OF_TRANSACTIONS_TO_READ);

    int count = 0;

    while (!transactions.isEmpty()) {

      CustomSystemOut.INSTANCE.blankLine();
      CustomSystemOut.INSTANCE.blueBackground(
          "- Sending batch of transactions no. " + ++count + " for processing -");

      // ask pattern is used
      // according to https://pekko.apache.org/docs/pekko/current//typed/interaction-patterns.html#request-response-with-ask-from-outside-an-actor
      // in this case, a future is returned, and the main thread will wait for its completion
      // or timeout, if nothing is received...
      List<Transaction> finalTransactions = transactions;

      CompletionStage<Event> asyncProcessing = AskPattern.ask(transactionsActor,
          (replyTo) -> new TransactionsToProcess(finalTransactions, replyTo),
          Duration.ofMinutes(5), actorSystem.scheduler());

      // getting the future, which is a blocking call
      Event result = asyncProcessing.toCompletableFuture().get();

      if (!(result instanceof AllTransactionsProcessed)) {

        CustomSystemOut.INSTANCE.red("Something really bad has happened here! I will terminate...");
        break;
      }

      // messages were processed
      CustomSystemOut.INSTANCE.blueBackground(
          "- Batch of transactions no. " + count + " processed -");
      CustomSystemOut.INSTANCE.blueBackground("- Trying to read more now... -");

      // trying to read more transactions
      transactions = TransactionProvider.getInstance()
          .readTransactions(NUMBER_OF_TRANSACTIONS_TO_READ);

      if (transactions.isEmpty()) {

        // nothing more!
        CustomSystemOut.INSTANCE.blankLine();
        CustomSystemOut.INSTANCE.blueBackground("- Nothing more to process -");
        CustomSystemOut.INSTANCE.blueBackground("- Goodbye! -");
      }
    }

    // shutting things down
    actorSystem.terminate();
    Await.ready(actorSystem.whenTerminated(), Inf());
  }
}