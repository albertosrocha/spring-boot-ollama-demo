package br.dev.alberto.genia.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClient) {
        return chatClient.build();
    }

//    @Bean
//    public MessageWindowChatMemory messageWindow() {
//        return MessageWindowChatMemory.builder()
//                .maxMessages(100)
//                .build();
//    }

//    @Bean
//    public ChatMemoryRepository chatMemory() {
//        return new InMemoryChatMemoryRepository();
//    }
}
