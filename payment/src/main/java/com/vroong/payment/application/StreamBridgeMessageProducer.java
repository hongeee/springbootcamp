/** MessageSchema spec: @see https://wiki.mm.meshkorea.net/display/MES/Message+Schema */
package com.vroong.payment.application;

import static com.vroong.payment.config.Constants.PRODUCER_CHANNEL;
import static com.vroong.payment.config.Constants.PROJECT_NAME;

import com.vroong.payment.config.Constants.MessageKey;
import com.vroong.payment.domain.PersistentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StreamBridgeMessageProducer {

  private final StreamBridge streamBridge;

  public boolean produce(PersistentEvent persistentEvent) {
    final String body = persistentEvent.getBody();
    Message<?> message =
        MessageBuilder.withPayload(body)
            .setHeader(MessageKey.ID, persistentEvent.getEventId())
            .setHeader(MessageKey.TYPE, persistentEvent.getEventType())
            .setHeader(MessageKey.VERSION, 1)
            .setHeader(MessageKey.SOURCE, PROJECT_NAME)
            .setHeader(MessageKey.RESOURCE, body.getClass().getSimpleName())
            .setHeader(MessageKey.PARTITION_KEY, persistentEvent.getPartitionKey())
            .build();

    return streamBridge.send(PRODUCER_CHANNEL, message);
  }
}
