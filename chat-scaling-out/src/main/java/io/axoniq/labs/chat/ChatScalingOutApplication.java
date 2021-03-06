package io.axoniq.labs.chat;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
public class ChatScalingOutApplication {

    private static final Logger logger = LoggerFactory.getLogger(ChatScalingOutApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ChatScalingOutApplication.class, args);
    }

    @Configuration
    @EnableSwagger2
    public static class SwaggerConfig {
        @Bean
        public Docket api() {
            return new Docket(DocumentationType.SWAGGER_2)
                    .select()
                    .apis(RequestHandlerSelectors.any())
                    .paths(PathSelectors.any())
                    .build();
        }

    }

    @Configuration
    public static class AmqpConfig {

        @Autowired
        public void amqpConfig(AmqpAdmin amqpAdmin) {
            amqpAdmin.declareQueue(participantEventsQueue());
            amqpAdmin.declareExchange(eventsExchange());
            amqpAdmin.declareBinding(binding());
        }

        @Bean
        public Queue participantEventsQueue() {
            return QueueBuilder.durable("participant-events").build();
        }

        @Bean
        public Exchange eventsExchange() {
            return new ExchangeBuilder("events", "fanout").durable(true).build();
        }

        @Bean
        public Binding binding() {
            return BindingBuilder.bind(participantEventsQueue()).to(eventsExchange()).with("axon").noargs();
        }

        @Bean(name = "amqp_participant_event_source")
        SpringAMQPMessageSource springAMQPMessageSource(Serializer serializer) {
            return new SpringAMQPMessageSource(serializer) {

                @RabbitListener(queues = "participant-events")
                @Override
                public void onMessage(Message message, Channel channel) {
                    super.onMessage(message, channel);
                }
            };
        }
    }
}
