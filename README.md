##A fluent command processing, in the spirit of POSIX

[![Build Status](https://travis-ci.org/foundry27/Mandate.svg?branch=master)](https://travis-ci.org/foundry27/Mandate)

###What is it?
Mandate provides an API for using annotated method definitions to parse user input as *commands*, similar to GNU's getopt except with integrated functionality for defining, searching for, executing, and linking the commands to which that input is presented.
This is perfectly suited for an environment in which user input has to be gathered from outside the CLI, possibly many numbers of times, and interpreted from within an already running program.

Using Mandate can eliminate boilerplate code and speed up development with integrated platforms, messaging services, games, and more!


###How does it work?

The first thing you'll need to start using Mandate is a **CommandContext** to use as your combined classpath and executor. The preferred method of getting this is

```java
//get a new CommandContext instance
CommandContext context = Mandate.newContext();
```
---
..which will give you a CommandContext instance with reasonable defaults for all configurable options.
From there, you'll need to make your first command method somewhere in a class

```java
//either define a class or add methods to an old one
class MyClassWithCommandMethods {
    @Executes
    @Syntax(tree = "greet")
    public ExitCode greetMe(@Implicit IODescriptor io, String greeting) {
        io.out().write(greeting);
        return ExitCode.SUCCESS;
    }
}
```
To break down the semantics of a command definition:
  * **@Executes** marks a method as being *command-executable*, meaming that it is valid for registration to the command classpath. This annotation is also used to define further required elements in a command syntax tree besides the name or alias of a command through the annotation's *tree* element
  * **@Syntax** defines the usable base name and aliases for a command. This can be applied either directly to a method as shown in the example above, or to a class in which command-executable methods exist. The second option might be preferable when you have multiple commands that start with the same base name that have sub-syntax defined in the *Executes* annotation, such as "select direction right" or "select fruit orange". A *command-executable* method with a *Syntax* annotation will always override whatever class-level syntax may exist
  * **ExitCode** is the return type that all *command-executable* methods must have . This is an enumeration with the constants SUCCESS, FAILURE, TERMINATED, and INVALID, any of which can be used to report the termination state of the running command.
  * **IODescriptor** serves a similar purpose to Unix file descriptors, giving commands knowledge of their input, output, and error streams. This allows developers to define robust systems of supplying, consuming, and piping data to, from, and between commands.
  * **@Implicit** is a way of saying that the annotated parameter does not represent something that the user has to provide input to reify, and instead will have a matching object provided for it by the CommandContext. You can create custom implicit value providers yourself, but more on that later.

---

You can add any collection of commands to the classpath of your *CommandContext* by calling the **register** method on an instance of any class with *command-executable* methods in it

```java
//register the commands in that class
context.register(new MyClassWithCommandMethods());
```
If multiple classes register methods with similar command syntax, the *CommandContext* will try to resolve the differences as best it can. Registration of a correctly-formatted *command-executable* method can fail if it has the exact same minimum and maximum number of parameters of a previously registered command with the same syntax tree leading up to the command.

---

Finally, commands can be invoked with the **execute** method of your *CommandContext* instance

```java
Execution exec = context.execute("greet Greetings!");   //execute the command...
System.out.println(exec.result());  //should print SUCCESS
```
This method will return a result of type **Execution**, representing a possibly parallel command execution. Executions define similar methods to *Future* objects, letting you block on, poll, and cancel any pending execution.

You may notice there is no reference to the string "Greetings!" anywhere here. That's because the outputs of the *greet* command were left to the output stream assigned to the execution through the *IODescriptor* argument. By default, any *CommandContext* will have a **CommandInput** reading from *System.in* for input, and **CommandOutput**s printing to *System.out* for output and error messages. This, however, is totally configurable.


###Argument Types
Mandate commands can define four types of arguments, those being **Flags**, **Enforced**, **Optional**, and **Implicit** arguments.

```java
@Executes
@Syntax(tree = "greet")
public ExitCode greetMe(@Implicit IODescriptor io,
                        @AutoFlag(flag = {"c", "-caps"}) boolean useCaps,
                        @UserFlag(flag = {"r", "-repetitions"}, elsedef = "1") int repetitions,
                        String greeting,
                        Optional<String> recipient) {

    io.out().write("to " + recipient.orElse("someone") + ": "
            + String.join(" ", Collections.nCopies(repetitions, (useCaps ? greeting.toUpperCase() : greeting))));
    return ExitCode.SUCCESS;
}
```
To describe the different kinds of arguments:
  * **@AutoFlag** decribes an flag that does not require an operand. These flags are configurable with the *ifdef* and *elsedef* elements in the *AutoFlag* annotation to define what the input to parse for these parameters should be if the flag is present or missing, but special behavior exists for boolean types (which only have two possible values).
  *  **@UserFlag** describes a flag that requires an operand to be provided by the user. In most cases, *UserFlag*s and *Optional* arguments can be used interchangably as they both serve the same purpose. However. *UserFlag*s have access to their *elsedef* element describing specifically what to parse for the parameter if the flag is not present.

  *Notes: the definitions for flags always implicitly have a '-' prepended to them, so passing the example flag for having a message in capitals as described above can be done as "-c" or "--caps".
  Furthermore, all flags have an "xor" element that can descibe flags that cannot be present along with the flag declaring the xor*
  * **Enforced** arguments are arguments that are not annotated as flags or wrapped as *Optional*s. All arguments of this type must be provided for the argument parser to consider the invocation valid.
  
  *  **Optional** arguments are arguments that are not annotated as flags, and explicitly wrapped as *Optional*s. Any given number of optional arguments can be provided to a command accepting them, up to the total number of optional arguments defined in the command signature.
  
  *  **Implicit** arguments were covered above, but in case you missed it, these are arguments that are passed implicitly by the CommandContext.

---

**The general rule for defining parameters in a command signature is that parameters should follow the pattern**
> Implicits -> Flags (any kind) -> Enforced Arguments -> Optional Arguments

Once registered, invoking the command descibed above might look like

```java
// will print "to Robert: YO! YO! YO! YO! YO! YO!"
context.execute("greet --repetitions 6 yo! Robert -c");
```
As you can see, *Flags* can be present anywhere in the input provided by the user. *Enforced* and *Optional* arguments, however, must be provided through the input in declaration order. Because the *IODescriptor* is implicit, we don't have to have a matching argument for it.


###Argument Values
Mandate has built-in support for:
  * All primitive types and their wrapper classes
  * All built-in CharSequence types
  * BigInteger and BigDecimal
  * Enumerations
  * Multidimensional arrays of any kind
  * Sets of any kind
  * Lists of any kind
  * Maps mapping values of one kind to those of another

###Dependencies
Mandate requires ObjectWeb ASM 3.0+, which Maven users can integrate with this dependency:
```xml
<dependency>
    <groupId>org.ow2.asm</groupId>
    <artifactId>asm</artifactId>
    <version>5.1</version>
</dependency>
```
