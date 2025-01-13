package com.dionext.ai.services;

import com.dionext.ai.entity.AiLogInfo;
import com.dionext.ai.entity.AiRequest;
import com.dionext.ai.entity.TmpAiRequest;
import com.dionext.ai.repositories.AiModelRepository;
import com.dionext.ai.repositories.AiPromptRepository;
import com.dionext.ai.repositories.AiRequestRepository;
import com.dionext.ai.repositories.TmpAiRequestRepository;
import com.dionext.ai.utils.DbLoggerAdvisor;
import com.dionext.ideaportal.db.entity.Cite;
import com.dionext.ideaportal.db.repositories.CiteRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class IdeaAIService {
    public static final String CITE_EXPLANATION_1 = "Cite explanation 1";
    @Autowired
    //private OpenAiChatModel chatModel;
    private OllamaChatModel chatModel;

    @Autowired
    private AiModelRepository aiModelRepository;
    @Autowired
    private AiPromptRepository aiPromptRepository;
    @Autowired
    private AiRequestRepository aiRequestRepository;
    @Autowired
    private  AIRequestService aiRequestService;
    @Autowired
    private TmpAiRequestRepository tmpAiRequestRepository;
    @Autowired
    private CiteRepository citeRepository;

    private ChatClient chatClient;

    //public AIService(OpenAiChatModel chatModel) {
    //  this.chatModel = chatModel;
    public IdeaAIService() {
    }

    @PostConstruct
    void postConstruct() {
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        //https://docs.spring.io/spring-ai/reference/api/chatclient.html
        chatClient = builder
                //.defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    //@Async("taskExecutor")
    @Async()
    public void citeExplanationBulkAsync() {
        log.info("Execute method asynchronously - " + Thread.currentThread().getName());
        try {
            Collection<Cite> cites = citeRepository.findAllFavorite();
            int i = 0;
            for(Cite cite : cites) {
                Collection<AiRequest>  aiRequests = aiRequestRepository.findByExternalDomainAndExternalEntityAndExternalVariantAndExternalId(
                        Cite.IDEA, Cite.CITE, Cite.CITE_EXP, cite.getId().toString());
                if (aiRequests.isEmpty()) {
                    log.info("Explanation number: " + i);
                    try {
                        String exp = citeExplanation(cite);

                        log.info("Explanation for cite with id: " + cite.getId());
                        log.info(exp);
                    }
                    catch(Exception ex){
                        log.error("Error Explanation for cite with id: " + cite.getId(), ex);
                    }
                    i++;
                }
                else log.info("Already present explanation for cite with id: " + cite.getId());
            }
        } catch (Exception e) {
            log.error("Error executing background task", e);
        }
        log.info("Task execution completed");
    }

    @Async()
    public void testAsync() {
        log.info("Execute method asynchronously - " + Thread.currentThread().getName());
        try {
            int i = 0;
            long startTime = System.currentTimeMillis();
            while(true) {
                Collection<Cite> cites = citeRepository.findAllFavorite();
                for (Cite cite : cites) {
                    //log.info("Explanation number: " + i);
                    //String exp = citeExplanation(cite);
                    log.info("test id: " + cite.getId());
                    //log.info(exp);

                    TmpAiRequest tmpAiRequest = new TmpAiRequest();
                    tmpAiRequest.setResult("Time : " + LocalDateTime.now());
                    tmpAiRequest.setDateTime(LocalDateTime.now());
                    tmpAiRequest.setDuration(System.currentTimeMillis() - startTime);
                    tmpAiRequestRepository.save(tmpAiRequest);

                    i++;
                }
            }
        } catch (Exception e) {
            log.error("Error executing background task", e);
        }
        log.info("Task execution completed");
    }


    //@Async("taskExecutor")
    @Async()
    public void citeExplanationAsync(Cite cite) {
       log.info("Execute method asynchronously - " + Thread.currentThread().getName());
        try {
            String exp =  citeExplanation(cite);
            log.info("Explanation");
            log.info(exp);
        } catch (Exception e) {
            log.error("Error executing background task", e);
        }
        log.info("Task execution completed");
    }

    public String citeExplanation(Cite cite) {
        String systemTemplate = "SYSTEM: Ты преподаватель философии";
        String userTemplate = """
                "USER: Объясни смысл крылатого выражения "{cite}" автора {author}.
                """;
        //String systemTemplate = "Ты преподаватель философии";
        //String systemTemplate = null;
        //String userTemplate = """
          //      "Объясни смысл крылатого выражения "{cite}" автора {author}.
            //    """;
        AiLogInfo aiLogInfo = aiRequestService.prepareAiLogInfo(
                1L,
                CITE_EXPLANATION_1,
                systemTemplate,
                userTemplate,
                Cite.IDEA,
                Cite.CITE,
                Cite.CITE_EXP,
                cite.getId().toString()
                );


        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemTemplate);
        //Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", name, "voice", voice));
        Message systemMessage = systemPromptTemplate.createMessage();

        PromptTemplate promptTemplate = new PromptTemplate(userTemplate);
        //promptTemplate.add("cite", cite.getText());
        //promptTemplate.add("author", cite.getAuthor().getNames());
        Message userMessage = promptTemplate.createMessage(Map.of("cite", cite.getText(), "author", cite.getAuthor().getNames()));

        //Prompt prompt = new Prompt(List.of(userMessage));
        Prompt prompt = new Prompt(List.of( systemMessage, userMessage));
        //Prompt prompt = promptTemplate.create();

        String resultItems = chatClient
                .prompt(prompt)
                //.system(systemTemplate)
                .advisors(new SimpleLoggerAdvisor(
                        request -> "Custom request: " + request,
                        response -> "Custom response: " + response.getResult(),
                        2
                ), new DbLoggerAdvisor(aiLogInfo))
                .call()
                .content();
        aiRequestService.postProccessAiLogInfo(aiLogInfo);
        return resultItems;
    }


}
