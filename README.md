# PekkoExample

Sample application using [Apache Pekko](https://pekko.apache.org) in Java. It basically is
the Akka sample application I created in my project
[AkkaExample](https://github.com/cosmicboy79/AkkaExample) but now using Apache Pekko.

I used [Apache Pekko Tutorial](https://pekko.apache.org/docs/pekko/current//typed/guide/tutorial.html) to guide
me in the implementation of this sample. My intention is to use this repository as a "coding laboratory"
to learn a bit more about this framework in the near future.

This sample application's main entry point is class
[ProcessTransactions](https://github.com/cosmicboy79/PekkoExample/blob/main/src/main/java/edu/pekko/sample/app/ProcessTransactions.java).
The data to be processed is provided via class
[TransactionProvider](https://github.com/cosmicboy79/PekkoExample/blob/main/src/main/java/edu/pekko/sample/app/data/provider/TransactionProvider.java)
and sent to the Actor System, as follows:

1. [TransactionsActor](https://github.com/cosmicboy79/PekkoExample/blob/main/src/main/java/edu/pekko/sample/app/actor/TransactionsActor.java) - Actor
   that receives a list of transactions to be processed. Based on the customer associated to each transaction, it will either find or create a related
   child Actor (see next point).
2. [CustomerActor](https://github.com/cosmicboy79/PekkoExample/blob/main/src/main/java/edu/pekko/sample/app/actor/CustomerActor.java) - child Actor
   (see previous point) that process the transaction for a customer.

This project can be built with either Maven or Gradle, and it was developed with Java 21.

I recommend to simply import this project and run it with any preferred IDE.