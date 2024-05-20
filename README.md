# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

[LINK]([https://sequencediagram.org/index.html#initialData=C4S2BsFMAIBUAsQGdrOgQ2qCkBQuBBcEAY0gFoA+AIQHsAjALgGFiSBrDAOwBNoeATugDmWWtBIDI6YDExSAjgFdISYNFoCAOl2ABPAA4wwqLlngwktJQLIYpmYOOAXoUAGbBCbSAB5y5HRMgiJi0AC2tABueFy0shoxAtBBADQAItb0UBJsnE7QkDwmsgAewCx50AASkOAGEZBcStDumtAAVB0gXGoCSiSgtL1d3HxdkKXo4QZQSF24QVQAFADMAJSZStkwJFUFRSWTFXFc5D1q6LpXkNYo4apIIpBAA](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIB4GcQC9IC4BMAGAfLAxgPbgEBOqAxAA4CuJlU2ACgBYCG8M6q0Aws5PHjQAypBIA3MdAAiAkAHMAdnAD0hYiVwrEKAFC7WeYKV7gQkRcF2VWJUHhA3LIsZJLXb9x62cAJHwAmUO42diAOTsAuEuGQHmERPlHSrMCsAIJ4eALwugGprABG7DABhfoWwCQAnvA2Dory0JgAdACcuvIkBNSU0OSKrOLV0ABKkPIg8FWpIATK5ADuzGBxPGaV0AC02NFu3ADajADywgAqALrQKtQcJAA6igDe0ABEt2KDALaQr6ivbwANG8bIJFqQAn8Aa9ga9IF9WCBwFC3tAAL66UQSKQ7aD+RRBMTcF7vO7fX7-IEg9jwcEkSGUmFveGI5GM9G6fGEkjbXZY8SxbgkCZTYBiAAU40m0zE4wAjtQBMAAJSY1yxXkyAqZbKCbjySDAACqd3FHxI5NVKTSOpymrK3GEAFEADJOnhnaDm8nQABm3S+XruumtGSydq2uP5gugimo4HAapi2U1odteugeGFqUgJol3tYP2BoNpEOBLKRVu14cE9sK3AAkgA5Z2jT0FovQEt0gLlhFI6DNs7HINiJMClO4tM1+DcLOQHPpajAZhmsmFyBVm0zuuNltOtuji0b4GsZfMM4EADWFkHTeH0DPK5D1d1QkjfPV2W4T4v14s44ah+eKBME3BSqKsoCPGVhcsE2xRq4RLQCSHa-NA3DQrCv6XjeigAphqIYvyOK4us5iWNwWCYI8qHrj8BGokyrw4f++EYUxHIWAE+hdD0fTkKQPgGtALoEJMCzLKsujkZsiHYmQRynJc1wcIIcyKLRbxoSizHdhC7LEUhPK4nByFPKSnwbrpsL6fSukYmZJmfsmaDQMQEnimJEnyoq0yqtGk67NOb76oaeYkGuVk-FuYZvruIiuu6np2TxIURvJE5ualgFBXsMZuCAvrVN5ICKFFx6dqlAVfjAU6vjkYXAEuK4VU2G6xem767A6iVuh6j7nrht4AGKjMcACyg3PultbAYFbmsXhuV1aZoHIUtAFOQhLn7ChbybexhHMTpHHQhyJEmWRGyUdA1FaSxQ1sYxWHafR6HHVxBK8d0vT9F0kC3t5PRREsKxijJN1RJlyEHNISVnE6VzaDkGmPId444rsTnEgdT14QCjnrc5+Xfu54kg+Kh01a5moLdw3GiOp8wrTtIEEmBsbxom23zcZ3DivA1A7sKdTzBwyr7a8hOY1duyybd93PG8MvcT9-H9MKASiaK0AAOIbkIYPSQr0O7bDetOipKjyBu6P4wBl2ajjUuHTLvO7TGZjTAbPzwFTDuKDTWWpg1Gbe8AvsCAHK7DUHL7bvFuK9c6-WerbfvQGNk3QBnAjQAA6g2Zy+NNf7LbN76ZTGee5AtbMu3nLqipyxNs5duOvLXfzQAcFl5w20g9wAjOgADMAAsgKvFJYoReSUIwo8ryFOAhhXvP1n-MxeftT829ohcRkKWzptUZgNHK13hs93318-IPI-j1PM-g7m72L4Cy+r+vm8MdvsJd4bgPkfXQatOi-QEjQOgUBeDZjFPrDc-RZ5rChnTfmvcTjnGRnne2sc2K0SAT8Y+bhnbE07m7QBG497oSJhzLGpM3LzhzFHGO5cLDAiIZuVm1cyYIhvFHQebC46cOoR1HhwUw6zlzoaVhh1REP2kJ1Hcyd6x9WSrnJB2cpq10LsXUuGNK7t1qtwOMCYJFakTo1TM8DICsIHtIU8gcFGQBocopOPU1HNlbOnDcg8nH4Lwi4mhd4Hy1wTnFDKnsyYOJ4WtehZBNGKNbgk4xClO4OKfpPC6xlT5Q3PpfEkmTuCj2yRicBfE-rkGqJABMBBFjQAAFIEDKogn4-Qf54CvJDCiZs9iw0YEaa2uDBiB1ovQVgNSSA8CIKQFxg8SEMJdiSShII15TJmRoHefih4cg9ow1AAArFpig5HOK7OssQmy5lJMgIPYOQFJFWIzAaSOG5hFsXmUoiJXUEqpw0XnPRJcy5x2gOkJs0hbmDx+So3hbk84WMrtwXo+QxRnMCRwi5kyrmzJIF89xEZPHcCNIwaQ6REbim8QeM4AB+SWptrk8jKsYW5QKDGBzBRCqF0gYVJzhfqDccTsbkNuSk7kaS9okhlk7YCZ87oXweqrb6ECNbkC6JM0wC4eSsEoPQcIsx5jILfj0uS5tElwwRkja4ZRZZkISXOKAthxSqn2fTTMjrIoPLyki6AwovgEEkOkBMEV-YErmkSsYToJrHAAGpOkfAmI8uQjF81cqY7miKpFCnhAGyAQbwBR1Dbywl0BeqjCjbG+NrBE3hJTfyrm5j671WedIv1ub80tXYYoItKaI3lujXGhN4AQVsWTVIiVMYzGJibdEtAeAPXCDSMAW4zqhXs25Om8xvMYaJKlTkk+sr8nysKSrDkFRvpAA)) to my personal sequenceDiagram for this project (phase 2)

