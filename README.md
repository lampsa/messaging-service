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
- Handles provider-specific exceptions and maps them to generic error categories
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
To use the Cloud Agnostic Messaging Service, you need to create an instance of the MessagingService interface.
This will provide you with a cloud-specific instance of the MessagingService interface that you can use to send 
and receive messages. Small example below:

```java
public class MessageSender {
    public void main(String[] args) {
        MessagingService queueService = MessagingServiceBuilder.builder().build();
        SendMessageWrapper message = SendMessageBuilder
               .forPayload("Hello World")
               .attribute("attr1", "value1")
               .attribute("attr2", "value2")
               .build();
        
        queueService.sendMessage("MyQ", message);
        List<ReceivedMessageWrapper> messages = queueService.receiveMessages("MyQ", 1); // Receive 1 message

        ReceivedMessageWrapper receivedMessage = messages.get(0);
        // Process message
       
        queueService.completeMessage("MyQ", receivedMessage.getHandle());
    }
}
```
---
# Configuration
The Cloud Agnostic Messaging Service uses the following environment variables for configuration:

| Variable                     | Service | Description                                                        |
|------------------------------| --- |--------------------------------------------------------------------|
| `MESSAGING_SERVICE_PROVIDER` | All | The messaging provider to use. Currently, only `sqs` is supported. |
| `MESSAGING_SERVICE_REGION`   | AWS SQS | The AWS region to use. |

Please make sure you have the necessary credentials and permissions set up for the chosen provider.

