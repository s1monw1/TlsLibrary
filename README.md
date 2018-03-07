

# SeKurity - Kotlin powered TLS library

[![Build Status](https://travis-ci.org/s1monw1/TlsLibrary.svg?branch=master)](https://travis-ci.org/s1monw1/TlsLibrary)

This library provides an API for creating basic SSL/TLS connections with standard [Java Secure Socket Extension, JSSE](http://docs.oracle.com/javase/7/docs/technotes/guides/security/jsse/JSSERefGuide.html).
The Library is implemented in [Kotlin](http://kotlinlang.org/).
The Kotlin API is implemented with a *"type-safe builder"* approach, which is quite popular in the Groovy community.

**Disclaimer**: _The current Version is not optimized for Java yet._

## Motivation

If you also find it hard to use the complex JSSE structure to create your SSL sockets, which also generates lots of boilerplate when used directly, this tool is what you've been looking for.

![JSEE](/images/classhierarchy_jsse.jpg)


The library provides means for creating `SSLSocketFactories` that can be used for most use cases where TLS/SSL connections are required. It's also supposed to provide usage examples and even sample implementations like SSL enabled servers, Apache HTTP Clients and others, which you can use directly in your application.

## Demo of Kotlin DSL

In the following you can see some basic examples of using the Kotlin DSL for setting up ssl-(server)-socket-factories.

### Creating a Client Socket for Mutual Authentication (Client Keystore must be provided):

```kotlin
val fac = createSocketFactory {
            keyManager {
                open("certsandstores/clientkeystore") withPass "123456" ofType "jks"
            }
            trustManager {
                open("certsandstores/myTruststore") withPass "123456" ofType "jks"
            }
            sockets {
                cipherSuites = listOf("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                        "TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA")
                timeout = 10_000
            }
}

val socket = fac.createSocket("192.168.3.10", 443)

```

### Creating a Server Socket with simple keystore (no Client Auth required)

```kotlin
val fac = createServerSocketFactory {
        keyManager {
            open("certsandstores/clientkeystore") withPass "123456" ofType "jks"
        }
    }

    val accept = fac.createServerSocket(443).accept()
}
```

## Getting Started

In your Gradle build, simply include the following repo as well as dependency:

```kotlin
maven { 
    setUrl("https://simon-wirtz.bintray.com/SeKurity"
}

compile("de.swirtz:sekurity:0.0.1")

```

## Basics

You can read about TLS and Keystores [here](crypto.md).