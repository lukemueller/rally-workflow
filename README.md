# Rally Workflow

## About
This is an extension of [Flowdock's Jenkins Plugin](https://github.com/flowdock/jenkins-flowdock-plugin). This plugin provides additional configuration options for sending private messages to users responsible for breaking certain builds.

The intention of this plugin is to increase developer awareness around how their code has effected builds.

*Note: This project is specific to Rally Software and our Jenkin's workflow. It is not intended to be a general purpose extension. Hence it is not a fork.*

## Testing
This plugin includes full end to end integration tests for the Private Message functionality. These tests require that you specify real Flowdock information to the [integrations.cfg](https://github.com/lukemueller/rally-workflow/tree/master/src/test/java/integration/com/flowdock/jenkins/integration.cfg) file. 

Note: The Flowdock API does not allow you to send messages to yourself so you will need two Flowdock users (sender/recipient).

    {
        "senderToken" : "ef8f01ef2ce2e251f13b9dEd0935A78a",
        "recipientEmail" : "lmueller@rallydev.com",
        "recipientId" : "65897"
    }

Avoid accidental commits of your populated config file

    git update-index --assume-unchanged src/test/java/integration/com/flowdock/jenkins/integration.cfg

Run all tests

    mvn install