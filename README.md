# PekkoExample

Sample application using [Apache Pekko](https://pekko.apache.org) in Java. It basically is
the Akka sample application I created in my project
[AkkaExample](https://github.com/cosmicboy79/AkkaExample) but now using Apache Pekko.

This sample application's main entry is class
[SampleApp](https://github.com/cosmicboy79/PekkoExample/blob/main/src/main/java/edu/pekko/sample/app/SampleApp.java).
The data to be processed is provided via class
[TransactionProvider](https://github.com/cosmicboy79/PekkoExample/blob/main/src/main/java/edu/pekko/sample/app/data/provider/TransactionProvider.java)
and sent to the Actor System, as follows:

1. [TransactionsActor](https://github.com/cosmicboy79/PekkoExample/blob/main/src/main/java/edu/pekko/sample/app/actor/TransactionsActor.java) - Actor
   that receives a list of transactions to be processed. Based on the customer associated to each transaction, it will either find or create a related
   child Actor (see next point).
2. [CustomerActor](https://github.com/cosmicboy79/PekkoExample/blob/main/src/main/java/edu/pekko/sample/app/actor/CustomerActor.java) - child Actor
   (see previous point) that process the transaction for a customer.

This project was developed with Java 21 and is built via Maven. I used
[Apache Pekko Tutorial](https://pekko.apache.org/docs/pekko/current//typed/guide/tutorial.html) as a basis for
this development. My intention is to use this repository as a "coding laboratory" to explore this framework more.

I recommend to simply import this project and run it with any preferred IDE.