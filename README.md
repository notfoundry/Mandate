##A dead-simple command system, in the spirit of Unix

Mandate allows you to define specific methods as "commands", which can then be registered to a central command manager for future invocation

###Intro
To add Mandate to your project, it is suggested that you use Maven; use this dependency:
```xml
<dependency>
    <groupId>org.ow2.asm</groupId>
    <artifactId>asm</artifactId>
    <version>5.1</version>
</dependency>
```

This library also depends on the Parsor library, a lightweight utility for parsing Strings and other values to concrete types, which can be found at [this link](https://github.com/foundry27/Parsor). Unfortunately, no Maven dependency is available at this time.
