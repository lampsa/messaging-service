# Cloud-Agnostic Messaging Service
The Cloud Agnostic Messaging Service is a Java library that provides a unified interface for 
working with messaging services across different cloud providers. It allows you to write 
cloud-agnostic code for sending  and receiving messages, abstracting away the provider-specific 
details.

The design philosophy behind the library is to provide a simple, easy-to-use interface that offers
the least common denominator of functionality across different providers. While this might not allow
you to take advantage of all the features offered by a particular provider, it ensures that your code
is portable across different providers.

The library is designed to be extensible, so you can easily add support for new cloud providers.

**See:** [project page](https://github.com/lampsa/messaging-service/wiki/Cloud-Agnostic-Messaging-Service) for design and implementation details.

# Features
- Currently, supports AWS SQS as the service provider
- Provides a common interface for message publishing and consumption operations
- Offers limited support for mapping provider-specific exceptions to generic exceptions
- Supports message attributes for enriching message metadata
- JSON payload serialization and deserialization is not implemented
- Asynchronous messaging is not supported

# Getting Started
To get started with the Cloud Agnostic Messaging Service, follow these steps:
1. Clone the repository: git clone https://github.com/lampsa/messaging-service.git
2. Build the project: mvn clean install -DskipTests
   - If you want to run the tests, you need to have an AWS account and set up the necessary credentials and permissions
   - Test also assumes an empty SQS queue named MyQ exists in the default (eu-central-1) region
   - See the [AWS documentation](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html) for more information on setting up AWS credentials
   - See src/test/java/fi/techappeal/messagingservice/sqs/SqsMessagingIT.java for more information on the test setup
3. Include the Cloud Agnostic Messaging Service library in your project.

```xml
    <dependencies>
        <dependency>
            <groupId>fi.techappeal</groupId>
            <artifactId>messaging-service</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        ...
    </dependencies>
```
---
Here is a canonical example for sending and receiving messages using the Cloud Agnostic Messaging Service. To use the Cloud Agnostic Messaging library, you need to create a MessageSender and a MessageReceiver instances.
The examples below show how to create a message receiver instance, which listens to a queue named MyQ and prints
out the message payload and attributes. 

```java
public class MessageReceiverExample {
   public static void main(String[] args) {
      MessageReceiver receiver = new MessageReceiver.Builder().build();
      receiver.subscribe("MyQ", (message) -> {
         System.out.println("Id: "+message.getId());
         System.out.println("Payload: "+message.getPayload());
         for (String key : message.getAttributes().keySet()) {
            System.out.println("Attribute: "+key+" = "+message.getAttributes().get(key));
         }
         return ProcessingState.PROCESSED;
      });
   }
}
```
The second example shows how to create a message sender instance, which sends a message to a queue named MyQ.
The message payload is "Hello World" and it has two attributes: attr1 with value "value1" and attr2 with value "value2".
It is noteworthy that the examples assumes that the queue already exists in eu-central-1 region.
```java
public class MessageSenderExample {
   public static void main(String[] args) {
      MessageSender sender = new MessageSender.Builder().build();
      SendMessageWrapper message = new SendMessageWrapper.Builder()
              .payload("Hello World")
              .attribute("attr1", "value1")
              .attribute("attr2", "value2")
              .build();

      sender.sendMessage("MyQ", message);
   }
}
```
---
# Configuration
The Cloud Agnostic Messaging Service uses the following environment variables for configuration. You can switch between 
different providers by setting the `MESSAGING_SERVICE_PROVIDER` environment variable to the desired provider. 
The following table lists the supported environment variables. Since the library currently only supports AWS SQS as the
service provider, the environment variables are only used by the SQS implementation.

| Variable                    | Service | Description                                                                         |
|-----------------------------| --- |-------------------------------------------------------------------------------------|
| `MESSAGING_SERVICE_PROVIDER` | All | The messaging provider to use. Currently, only `sqs` is supported.                  |
| `MESSAGING_SERVICE_REGION`  | AWS SQS | The AWS region to use. The default is `eu-central-1`.                               |
| `SQS_VISIBILITY_TIMEOUT`    | AWS SQS | The visibility timeout for messages in seconds. The default is `20`.                |
| `SQS_MAX_NUMBER_OF_MESSAGES`| AWS SQS | The maximum number of messages to receive in a single request. The default is `10`. |

Please make sure you have the necessary credentials and permissions set up for the chosen provider.

