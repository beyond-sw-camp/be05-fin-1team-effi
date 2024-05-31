package com.example.effi.controller;

import java.util.Map;

import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.effi.domain.DTO.ScheduleTags;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final OpenAiChatClient chatClient;

    @PostMapping("/aiTags")
    public ScheduleTags call(@RequestBody String message){
        var outputParser = new BeanOutputParser<>(ScheduleTags.class);

        String promptString = """
                {message}인 일정의 키워드를 5개 추출해줘.
                {format}
                """;

        PromptTemplate template = new PromptTemplate(promptString, Map.of("message", message, "format", outputParser.getFormat()));
        Prompt prompt = template.create();
        Generation generation = chatClient.call(prompt).getResult();

        ScheduleTags tags = outputParser.parse(generation.getOutput().getContent());
        return tags;
    }
}
