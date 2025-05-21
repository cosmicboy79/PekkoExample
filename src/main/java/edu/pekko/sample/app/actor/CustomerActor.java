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

package edu.pekko.sample.app.actor;

import edu.pekko.sample.app.actor.TransactionsActor.Event;
import edu.pekko.sample.app.actor.TransactionsActor.TransactionProcessed;
import edu.pekko.sample.app.data.definition.Customer;
import edu.pekko.sample.app.data.definition.Transaction;
import edu.pekko.sample.app.utils.CustomSystemOut;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.AbstractBehavior;
import org.apache.pekko.actor.typed.javadsl.ActorContext;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.actor.typed.javadsl.Receive;

/**
 * Actor that process the {@link Transaction} associated to a {@link Customer}.
 */
public class CustomerActor extends AbstractBehavior<Transaction> {

  private final ActorRef<Event> parentActor;

  public CustomerActor(ActorContext<Transaction> context, ActorRef<Event> parentActor) {

    super(context);

    this.parentActor = parentActor;
  }

  static Behavior<Transaction> create(ActorRef<Event> replyTo) {

    return Behaviors.setup(context -> new CustomerActor(context, replyTo));
  }

  @Override
  public Receive<Transaction> createReceive() {

    return newReceiveBuilder().onMessage(Transaction.class, this::processTransaction)
        .build();
  }

  private Behavior<Transaction> processTransaction(Transaction transaction) {

    CustomSystemOut.INSTANCE.printAsIs(getInfoMessage(transaction));

    CustomSystemOut.INSTANCE.printAsIs(
        "Processing done for " + transaction.customer()
            .getColorfulCustomerId());

    // informing the parent/sender actor about the processing of the transaction
    parentActor.tell(new TransactionProcessed());

    return this;
  }

  private String getInfoMessage(Transaction transaction) {

    return "Processing message " + transaction.id() + " for " + transaction.transactionType()
        + " of amount " + transaction.amount()
        + " for " + transaction.customer().getColorfulCustomerId();
  }
}
