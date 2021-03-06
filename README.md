# kmspan


Kafka topic partitions greatly help in parallelizing messages/events transferring, and it becomes widely adopted in applications where realtime or near-realtime processing on messages/events is desired. It is not meant to server all messaging use cases, of course. Recently I was looking at these cases below,
*  1. a stream of messages can be partitioned to a number of sub-streams of messages, different sub-streams can be processed on consumers independently, 
*  2. a stream of messages are expected to be processed on consumers in a certain order, possibly determined by producer,
*  3. a series of messages are related and you want to know when when the processing of all such messages are done on the consumer.

Case 1 is a common use case for Kafka.

For case 2, it is trivially achievable using Kafka, for example, by limiting to a single producer, a single topic, a single partition and a single consumer. Other non-trivial approaches exist too, but I feel in general case 2 is not for Kafka.

What puzzles me a bit recently is: **is case 3 for Kafka or not?** Let's investigate this more with an example.

Suppose you have a bunch of files, you write some code that works with a Kafka producer to read each line of each file, convert it into a message, send it to Kafka broker. Then you have some code that works with some Kafka consumers to poll the messages from broker, do some calculation on each message, and persist some result to a backend data store. Case 3 is asking: when are the 'processing' of all 'related' messages done?

The '**related**' and '**processing**' are user specific. For example, you may decide that all messages from one file are 'related', and a message is 'processed' after the calculation on this message is completed and result has been persisted to a backend data store. Another person may think all messages from all files under the same folder are 'related'.

Looking at above example, case 3 has the flavor of both case 1 and case2: **messages can be partitioned and processed independently on consumers, but some sort of processing events are desired: when all messages from a file is processed, or, when all messages from all files under a folder are processed, the user wants to be notified, either in the order of events occurrence or not.**

In an effort to solve case 3 with Kafka, I create this project: kmspan.

Kmspan stands for "Kafka messages span".

Some basic terminologies that kmspan uses:

*  span - a series of messages that are related from users' point of view
*  user messages - messages of a span, that carries the data the user intends to process
*  span message - messages sent by kmspan producer to signify the BEGIN or END of a span
*  span BEGIN event - a point in time right before any user message of a span is processed, generated by span consumer
*  span END event - a point in time right after all user messages of a span are processed, generate by span consumer
*  span (boundary) events - the span BEGIN and the span END events
*  span interval - the elapse of time bounded by the point of time of the span BEING event and the point of time of the span END event, of a span 
