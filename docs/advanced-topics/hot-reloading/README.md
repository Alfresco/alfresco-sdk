---
Title: Hot reloading
Added: v3.0.0
Last reviewed: 2019-10-18
---
# Hot reloading

Hot reloading in a Java project is the ability to avoid the infamous _change > restart and wait > check_ development lifecycle. This allows you to modify 
your application's code, and view the changes without having to restart Alfresco Content Services / Alfresco Share. You can potentially gain significant 
savings in development time that would otherwise be wasted rebuilding the Docker images and restarting the Docker containers.

Hot reloading is a well known behaviour in several other languages (C# for example), and the most practical and fast lifecycle like Save&Reload should be 
possible. Hot reloading is the key to enabling [Rapid Application Development (RAD)](https://en.wikipedia.org/wiki/Rapid_application_development) and 
[Test Driven Development (TDD)](https://en.wikipedia.org/wiki/Test-driven_development).

Since the Java 1.4 JVM, the Debugger API allowed debuggers to update class bytecode in place, using the same class identity. This meant that all objects 
could refer to an updated class and execute new code when their methods were called, preventing the need to reload a container whenever class bytecode was 
changed. All modern IDEs support it, including Eclipse, IntelliJ IDEA, and NetBeans. Since Java 5, this functionality has also been available directly to 
Java applications through the [Instrumentation API](http://docs.oracle.com/javase/6/docs/technotes/guides/instrumentation/index.html).

In the Alfresco development lifecycle hot reloading is possible as in every other Java project (and with the same limitations). You can manage a project 
created with the Alfresco SDK using hot reloading through two different tools:
* [JRebel](jrebel.md)
* [HotSwapAgent](hotswap-agent.md)

Both have advantages and disadvantages, so it's up to you to make the right choice for your needs. [JRebel](https://zeroturnaround.com/software/jrebel/) is a 
commercial product while [HotSwapAgent](http://hotswapagent.org/index.html) is open source. Both products can reload classes and web resources. However, 
JRebel is more powerful than HotSwapAgent and can also reload changes to the Spring XML context files, for example.
