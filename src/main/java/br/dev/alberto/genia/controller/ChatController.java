package br.dev.alberto.genia.controller;

import br.dev.alberto.genia.model.UserInteraction;
import br.dev.alberto.genia.service.PizzaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final OllamaChatModel chatModel;
    private final ChatMemoryRepository chatMemoryRepository;
    private final ChatClient chatClient;
    private final PizzaService pizzaService;

    public ChatController(OllamaChatModel chatModel, ChatMemoryRepository chatMemoryRepository, ChatClient chatClient,
                          PizzaService pizzaService) {
        this.chatModel = chatModel;
        this.chatMemoryRepository = chatMemoryRepository;
        this.chatClient = chatClient;
        this.pizzaService = pizzaService;
    }

    @PostMapping("/ai/conversation")
    public String generate(@RequestBody UserInteraction userInteraction,
                           @RequestHeader("conversationId") String converationId) {

        var messages = new ArrayList<>(chatMemoryRepository.findByConversationId(converationId));
        messages.add(new UserMessage(userInteraction.getContent()));

        var response = this.chatModel.call(messages.toArray(new Message[0]));

        messages.add(new AssistantMessage(response));
        chatMemoryRepository.saveAll(converationId, messages);

        logger.info("Response: {}", response);
        return response;
    }

    @GetMapping("/ai/conversation/history")
    public List<Message> getConversationHistory(@RequestHeader("conversationId") String conversationId) {
        return chatMemoryRepository.findByConversationId(conversationId);
    }

    @PostMapping("/ai/assistant")
    public String assistant(@RequestBody UserInteraction userInteraction) {
        var systemMessage = new SystemMessage("""
                Você é um engenheiro de software chamado Jarvis. Você é especializado na linguagem Java
            """);
        var userMessage = new UserMessage(userInteraction.getContent());
        Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

        return this.chatModel.call(prompt).getResult().getOutput().getText();
    }

    @PostMapping("/ai/pizzas")
    public String pizza(@RequestBody UserInteraction userInteraction) {
        return chatClient.prompt(userInteraction.getContent())
                .system("""
                    Você é um atendente de uma pizzaria e tem o papel de informar os sabores, valores e tamanhos das pizzas
                """)
                .tools(pizzaService)
                .call()
                .content();
    }

}
