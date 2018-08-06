package io.axoniq.labs.chat;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.sql.SQLException;

@SpringBootApplication
public class ChatScalingOutApplication {

	private static final Logger logger = LoggerFactory.getLogger(ChatScalingOutApplication.class);

	private AmqpAdmin amqpAdmin;

    public ChatScalingOutApplication(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    public static void main(String[] args) throws SQLException {
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

        @Bean
        public ConnectionFactory connectionFactory() {
            CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
            return connectionFactory;
        }

        @Bean
        public AmqpAdmin amqpAdmin() {
            RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
            return rabbitAdmin;
        }

        @Bean
        public Queue queue() {
            return new Queue("participant-events");
        }

        @Bean
        public FanoutExchange exchange() {
            return new FanoutExchange("events");
        }

        @Bean
        public Binding binding() {
            return BindingBuilder.bind(queue()).to(exchange());
        }

        @Bean
        SpringAMQPMessageSource springAMQPMessageSource(Serializer serializer){
            return new SpringAMQPMessageSource(serializer){

                @RabbitListener(queues = "participant-events")
                @Override
                public void onMessage(Message message, Channel channel) throws Exception {
                    super.onMessage(message, channel);
                }
            };
        }



    }
}
