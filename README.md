###  Welcome to redchain-opencoin
The opencoin intermediate server is a Java implementation of the hyperledger-fabric protocol, which manage chaincode and allows it and maintain a connect and send/receive transactions without joined redchain(extend from blockchain) server
It comes with full documentation and some example apps showing how to use it.

### Technologies

* Java 8 for the core modules, Java 8 for everything else
* [Maven 3+](http://maven.apache.org) - for building the project
* [fabric-sdk-java reference](https://github.com/hyperledger/fabric-sdk-java) - encapsulation all function communication server

### redchain-opencoin is blockchain manager which proxy all chaincode request and response
```
  redchain-opencoin is serviced from terminal user who proxy for sdk,websocket user,ios user and android users to blockchain server ,it is equivalent to facade or gateway before access really blockchain server. it will do  function as fellows:
```
* channel,chaincode manager include install,instantiate,and sign;
* organization,chaincode and msp configuration before register and image;
* private key and public key which take part in blockchain logic is secret kepted;
* concurrent processing and logic verification.
  
### who care?
Now you are ready to [reference the guideline](https://www.tangkc.com/).