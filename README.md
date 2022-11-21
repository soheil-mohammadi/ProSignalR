<img src="https://miro.medium.com/max/720/0*ILbItnzDfSZhZwSn.png" alt="SignalR logo" title="SignalR" align="right" height="60" />

# ProSignalR

A Powerful library for Making SignalR Connection Over Android Platform :)

:star: Star us on GitHub — it motivates us a lot!

## Table of content

- [Installation](#installation)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Contribution](#contribution-and-issues)
- [Links](#links)

## Installation

Please add this into your build.gradle file (app module) :

```
implementation 'com.enefce.libraries.prosignalr:1.0.0'
```

## Prerequisites

You should have a SignalR server which was powered by asp .net core.


## Getting Started

You can follow all of these next steps from sample app :)

* The first thing that we need to do is to initialize the library so add this line of code to your main application class of project :
```java
 @Override
   public void onCreate() {
      super.onCreate();
      instance = this;
      SuperSignalR.init(this);
   }
   ```


## Contribution and Issues

If you would like to participate in this project please create issue or use [Links](#links) section.


## Links

* [Contact](https://t.me/soheil_4ever)
* [Issue tracker](https://github.com/soheil-mohammadi/ProSignalR/issues)
* [Source code](https://github.com/soheil-mohammadi/ProSignalR)