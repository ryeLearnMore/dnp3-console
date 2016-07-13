# Building

Install [Maven](https://maven.apache.org/)

```
> sudo apt-get install maven mvn 
```

Install JDK 1.8

```
> sudo add-apt-repository ppa:openjdk-r/ppa
> sudo apt-get update 
> sudo apt-get install openjdk-8-jdk
```

To build and run tests:

```
> mvn package
```

The output will be in aegis-console\target\appassembler\bin

You can then invoke the program using the built .bat or .sh file, e.g.:

```
> ./dnp3-client -help
```

To send an arbitrary ASDU to the target, use the -func and -header parameters. The example below
sends a READ (1) request for Class 1, Class 2, Class 3 data (3C 02 06 3C 03 06 3C 04 06)

```
> ./dnp3-client -func 1 -headers "3C 02 06 3C 03 06 3C 04 06"
```
