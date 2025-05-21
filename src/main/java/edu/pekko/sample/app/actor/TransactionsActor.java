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
import edu.pekko.sample.app.data.definition.Customer;
import edu.pekko.sample.app.data.definition.Transaction;
import edu.pekko.sample.app.utils.CustomSystemOut;
import java.util.List;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.AbstractBehavior;
import org.apache.pekko.actor.typed.javadsl.ActorContext;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.actor.typed.javadsl.Receive;

/**
 * Actor that receives a list of transactions and sends each one of them to the respective child
 * Customer Actor for processing.
 */
public class TransactionsActor extends AbstractBehavior<Event> {

  private int numberOfTransactionsToProcess;
  private ActorRef<Event> parentActor;

  private TransactionsActor(ActorContext<Event> context) {

    super(context);
  }

  /**
   * @return Instance of {@link Behavior} for this actor
   */
  public static Behavior<Event> create() {

    return Behaviors.setup(TransactionsActor::new);
  }

  @Override
  public Receive<Event> createReceive() {

    return newReceiveBuilder().onMessage(TransactionsToProcess.class, this::sendToCustomers)
        .onMessage(TransactionProcessed.class, this::acknowledgeProcessedTransaction).build();
  }

  /**
   * Operation called when the Actor receives transactions to be processed. Given the customer
   * associated to the transaction, this operation creates or finds the related Customer Actor that
   * is responsible for processing it.
   *
   * @param transactionsToProcess Transactions to be processed
   */
  private Behavior<Event> sendToCustomers(TransactionsToProcess transactionsToProcess) {

    // saving the actor that has sent the message
    // so that it can be notified at the end of the processing
    parentActor = transactionsToProcess.replyTo();

    numberOfTransactionsToProcess = transactionsToProcess.transactions().size();

    CustomSystemOut.INSTANCE.yellow(
        "Number of received transactionsToProcess to process: " + numberOfTransactionsToProcess);

    transactionsToProcess.transactions().forEach(transaction -> {

      ActorRef<Transaction> customerActor = getActorRef(transaction.customer());

      CustomSystemOut.INSTANCE.printAsIs(
          "Sending message to actor for customer " + transaction.customer()
              .getColorfulCustomerId());
      customerActor.tell(transaction);
    });

    return this;
  }

  /**
   * Operation called when the Actor receives a message from the child Customer Actor signaling that
   * the transaction was processed.
   *
   * @param transactionProcessed Message about the processing of the transaction
   */
  private Behavior<Event> acknowledgeProcessedTransaction(
      TransactionProcessed transactionProcessed) {

    numberOfTransactionsToProcess--;

    if (numberOfTransactionsToProcess == 0) {

      CustomSystemOut.INSTANCE.yellow(
          "Informing the Parent Actor that all transactions were processed");

      parentActor.tell(new AllTransactionsProcessed());

      return this;
    }

    CustomSystemOut.INSTANCE.yellow("Still " + numberOfTransactionsToProcess + " to go...");

    return this;
  }

  /**
   * Finds or creates the reference to the child Actor associated with the given Customer.
   *
   * @param customer Customer
   * @return Actor reference for the given Customer
   */
  private ActorRef<Transaction> getActorRef(Customer customer) {

    String actorName = "customer-" + customer.getCustomerId();

    if (getContext().getChild(actorName).isEmpty()) {

      CustomSystemOut.INSTANCE.yellow("Actor for " + actorName + " is created");
      return getContext().spawn(CustomerActor.create(getContext().getSelf()), actorName);
    }

    CustomSystemOut.INSTANCE.yellow("Child actor for " + actorName + " is found");
    return getContext().getChild(actorName).get().unsafeUpcast();
  }

  /**
   * General definition of the type of messages this Actor will be able to handle.
   */
  public interface Event {

    // nothing to add here: simple message for Actors
  }

  /**
   * Represents the {@link Event} concerning the transactions to be processed.
   *
   * @param transactions Transactions that must be processed
   * @param replyTo      Reference to the caller Actor that has sent this message
   */
  public record TransactionsToProcess(List<Transaction> transactions,
                                      ActorRef<Event> replyTo) implements Event {

    // nothing to add here: simple message for Actors
  }

  /**
   * Represents the {@link Event} associated to the successful processing of a transaction.
   */
  public static final class TransactionProcessed implements Event {

    // nothing to add here: simple message for Actors
  }

  /**
   * Represents the {@link Event} related to the fact that all transactions received were
   * processed.
   */
  public static final class AllTransactionsProcessed implements Event {

    // nothing to add here: simple message for Actors
  }
}
