##A dead-simple runtime command system, in the spirit of Unix

###What is it?
Mandate provides an API for using annotated method definitions to parse user input as *commands*, similar to GNU's getopt except with integrated functionality for defining, searching for, executing, and linking the commands to which that input is presented.
This is perfectly suited for an environment in which user input has to be gathered from outside the CLI, possibly many numbers of times, and interpreted from within an already running program.

Using Mandate can eliminate boilerplate code and speed up development with integrated platforms, messaging services, games, and more!

###How does it work?

The first thing you'll need to start using Mandate is a **CommandManager** to use as your combined classpath and executor. The preferred method of getting this is

```java
//get a new CommandManager instance
CommandManager manager = Mandate.newManager();
```
---
From there, you'll need to make your first command method somewhere in a class

```java
//either define a class or add methods to an old one
class MyClassWithCommandMethods {
    @Executes
    @Syntax(syntax = "greet")
    public ExitCode greetMe(IODescriptor io, String greeting) {
        io.out().write(greeting);
        return ExitCode.SUCCESS;
    }
}
```
To break down the semantics of a command definition:
  * **@Executes** marks a method as being *command-executable*, meaming that it is valid for registration to the command classpath. This annotation is also used to define further required elements in a command syntax tree besides the name or alias of a command through the annotation's *tree* element
  * **@Syntax** defines the usable base name and aliases for a command. This can be applied either directly to a method as shown in the example above, or to a class in which command-executable methods exist. The second option might be preferable when you have multiple commands that start with the same base name that have sub-syntax defined in the *Executes* annotation, such as "select direction right" or "select fruit orange". A command-executable method with a *Syntax* annotation will always override whatever class-level syntax may exist
  * **ExitCode** is the return type that all *command-executable* methods must have . This is an enumeration with the constants SUCCESS, FAILURE, TERMINATED, and INVALID, any of which can be used to report the termination state of the running command.
  * **IODescriptor** must be the type of the first parameter of any *command-executable* method. This serves a similar purpose to Unix file descriptors, giving commands knowledge of their input, output, and error streams. This allows developers to define very robust systems of supplying, consuming, and piping input to, from, and between commands.

---

You can add any collection of commands to the classpath of your *CommandManager* by calling the **register** method on an instance of any class with *command-executable* methods in it

```java
//register the commands in that class
manager.register(new MyClassWithCommandMethods());
```
If multiple classes register methods with similar command syntax, the *CommandManager* will try to resolve the differences as best it can. Registration of a correctly-formatted *command-executable* method can fail if it has the exact same minimum and maximum number of parameters of previously-registered command with the same syntax tree.

---

Finally, commands can be invoked with the **execute** method of your *CommandManager* instance

```java
Execution exec = manager.execute("greet Greetings!");   //execute the command...
System.out.println(exec.result());  //should print SUCCESS
```
This method will return a result of type **Execution**, representing a possibly parallel command execution. Executions define similar methods to *Future* objects, letting you block on, poll, and cancel any pending execution.

You may notice there is no reference to the string "Greetings!" anywhere here. That's because the output of that greeting was left to the output stream assigned to the execution through the *IODescriptor* argument. By default, any CommandManager will have a defualt **CommandInput** reading from *System.in*, and default **CommandOutput**s printing to *System.out*. This, however, is totally configurable.

###Dependencies
Mandate requires ObjectWeb ASM 3.0+, which Maven users can integrate with this dependency:
```xml
<dependency>
    <groupId>org.ow2.asm</groupId>
    <artifactId>asm</artifactId>
    <version>5.1</version>
</dependency>
```

Mandate also depends on the Parsor library, a lightweight utility for parsing Strings and other values to concrete types, which can be found at [this link](https://github.com/foundry27/Parsor). Unfortunately, no Maven dependency is available at this time.
