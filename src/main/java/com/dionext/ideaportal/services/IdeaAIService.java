package com.dionext.ideaportal.services;

import com.dionext.ai.entity.AiLogInfo;
import com.dionext.ai.entity.AiPrompt;
import com.dionext.ai.entity.AiRequest;
import com.dionext.ai.repositories.AiPromptRepository;
import com.dionext.ai.repositories.AiRequestRepository;
import com.dionext.ai.services.AIRequestService;
import com.dionext.ai.utils.DbLoggerAdvisor;
import com.dionext.ideaportal.db.entity.Cite;
import com.dionext.ideaportal.db.repositories.CiteRepository;
import com.dionext.job.*;
import com.dionext.job.entity.JobInstance;
import com.dionext.utils.CmMarkdownUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IdeaAIService {

    @Autowired
    private AIRequestService aiRequestService;
    @Autowired
    private AiPromptRepository aiPromptRepository;
    @Autowired
    protected JobManager jobManager;
    @Autowired
    private CiteRepository citeRepository;
    @Autowired
    private AiRequestRepository aiRequestRepository;


    @PostConstruct
    void postConstruct() {
        List<AiPrompt> aiPrompts = aiPromptRepository.findAll();
        if (aiPrompts.size() == 0) {
            //1
            AiPrompt aiPrompt = new AiPrompt();
            aiPrompt.setId(1L);
            aiPrompt.setName("Объяснение цитаты");
            aiPrompt.setSystemPromptTempl("SYSTEM: Ты преподаватель философии");
            aiPrompt.setUserPromptTempl("""
                    USER: Объясни смысл крылатого выражения "{cite}" автора {author}
                    """);
            aiPromptRepository.save(aiPrompt);
        }
    }

    public String createRunJobParameters(String jobTypeId, JobInstance jobInstance, boolean readOnly) {
        if (jobTypeId == null && jobInstance != null) jobTypeId = jobInstance.getJobTypeId();

        StringBuilder str = new StringBuilder();

        if ("citeExplanation".equals(jobTypeId)) {
            str.append(
                    JobView.createRunJobParameter("countType", "Тип количества позиций",
                            jobInstance != null ? jobInstance.getParameter("countType") : CountType.ONE.name(),
                            readOnly));
            str.append(
                    JobView.createRunJobParameter("isTop", "Только топовые цитаты",
                            jobInstance != null ? jobInstance.getParameter("isTop") : "true",
                            readOnly));
            str.append(
                    JobView.createRunJobParameter("isOverrideRequest", "Перезаписывать ранее сохраненные запросы",
                            jobInstance != null ? jobInstance.getParameter("isOverrideRequest") : "true",
                            readOnly));
            str.append(
                    JobView.createRunJobParameter("aiModelId", "Id model",
                            jobInstance != null ? jobInstance.getParameter("aiModelId") : "4",
                            readOnly));
            str.append(
                    JobView.createRunJobParameter("aiPromptId", "Id Prompt",
                            jobInstance != null ? jobInstance.getParameter("aiPromptId") : "1",
                            readOnly));
        }
        else if ("citeInfoCopy".equals(jobTypeId)) {
            str.append(
                    JobView.createRunJobParameter("countType", "Тип количества позиций",
                            jobInstance != null ? jobInstance.getParameter("countType") : CountType.ALL.name(),
                            readOnly));
            str.append(
                    JobView.createRunJobParameter("isOverrideRequest", "Перезаписывать ранее сохраненные запросы",
                            jobInstance != null ? jobInstance.getParameter("isOverrideRequest") : "false",
                            readOnly));

        }

        return str.toString();
    }

    public JobBatchRunner createJob(String jobTypeId, JobInstance _jobInstance) throws InterruptedException {
        JobBatchRunner jobBatchRunner = new BaseJobBatchRunner();

        if ("citeInfoCopy".equals(jobTypeId)) {
            jobBatchRunner.setJobBatchListMaker((jobInstance) -> {
                CountType countType = CountType.valueOf(jobInstance.getParameter("countType"));

                Collection<AiRequest>  items = aiRequestRepository.findByExternalDomainAndExternalEntityAndExternalVariant(
                        Cite.IDEA, Cite.CITE, Cite.CITE_EXP);
                //todo здесь ищутся все запросы, а надо или искать только по определенной модели и промпту
                //или в функцию обработки добавить приоритезацию.

                if (countType == CountType.ONE)
                    return items.stream().limit(1).collect(Collectors.toList());
                else if (countType == CountType.TEN)
                    return items.stream().limit(10).collect(Collectors.toList());
                else //if (countType == CountType.ALL)
                    return items;

            });
            jobBatchRunner.setJobBatchIdExtractor((_, item) -> ((AiRequest) item).getExternalId());

            jobBatchRunner.setJobBatchItemProcessor((jobInstance, item) -> {
                boolean isOverrideRequest = Boolean.parseBoolean(jobInstance.getParameter("isOverrideRequest"));
                AiRequest aiRequest = (AiRequest) item;
                Cite cite = citeRepository.findById(Integer.valueOf(aiRequest.getExternalId())).orElse(null);
                if (cite != null && (cite.getInfo() == null || isOverrideRequest)){
                    cite.setInfo(CmMarkdownUtils.markdownToHtml(aiRequest.getResult()));
                    citeRepository.save(cite);
                }
            });
        }
        else {


            jobBatchRunner.setJobBatchListMaker((jobInstance) -> {
                CountType countType = CountType.valueOf(jobInstance.getParameter("countType"));
                boolean isTop = Boolean.parseBoolean(jobInstance.getParameter("isTop"));
                Collection<Cite> items;
                if (isTop)
                    items = citeRepository.findAllFavorite();
                else
                    items = citeRepository.findAll();
                if (countType == CountType.ONE)
                    return items.stream().limit(1).collect(Collectors.toList());
                else if (countType == CountType.TEN)
                    return items.stream().limit(10).collect(Collectors.toList());
                else //if (countType == CountType.ALL)
                    return items;

            });
            jobBatchRunner.setJobBatchIdExtractor((_, item) -> ((Cite) item).getId().toString());
            jobBatchRunner.setJobBatchItemProcessor((jobInstance, item) -> {
                Long aiModelId = Long.valueOf(jobInstance.getParameter("aiModelId"));
                Long aiPromptId = Long.valueOf(jobInstance.getParameter("aiPromptId"));
                boolean isOverrideRequest = Boolean.parseBoolean(jobInstance.getParameter("isOverrideRequest"));
                Cite cite = (Cite) item;

                Optional<AiRequest> aiRequest = aiRequestService.findByExternalDomainAndExternalEntityAndExternalVariantAndExternalIdAndAiModelIdAndAiPromptId(
                        Cite.IDEA, Cite.CITE, Cite.CITE_EXP, cite.getId().toString(), aiModelId, aiPromptId).stream().findAny();
                if (aiRequest.isEmpty() || isOverrideRequest) {
                    AiLogInfo aiLogInfo = aiRequestService.createAiLogInfo(aiModelId, aiPromptId,
                            aiRequest.orElse(new AiRequest()));
                    citeExplanation(cite, aiLogInfo);
                }
            });
        }
        return jobBatchRunner;
    }
    public String citeExplanation(Cite cite, AiLogInfo aiLogInfo) {
        Map<String, Object> userTemplateMap = Map.of("cite", cite.getText(), "author", cite.getAuthor().getNames());

        aiRequestService.prepareAiLogInfo(
                aiLogInfo,
                Cite.IDEA,
                Cite.CITE,
                Cite.CITE_EXP,
                cite.getId().toString()
                );

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(aiLogInfo.aiPrompt().getSystemPromptTempl());
        //Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", name, "voice", voice));
        Message systemMessage = systemPromptTemplate.createMessage();

        PromptTemplate promptTemplate = new PromptTemplate(aiLogInfo.aiPrompt().getUserPromptTempl());
        //promptTemplate.add("cite", cite.getText());
        //promptTemplate.add("author", cite.getAuthor().getNames());
        Message userMessage = promptTemplate.createMessage(userTemplateMap);


        ChatClient chatClient = aiRequestService.getChatClient(aiLogInfo.aiModel());

        ChatOptions.Builder builder = ChatOptions.builder();
        builder = builder.model(aiLogInfo.aiModel().getModel());
        //builder = builder.temperature(0.8);
        ChatOptions chatOptions = builder.build();

        //Prompt prompt = new Prompt(List.of(userMessage), chatOptions);
        Prompt prompt = new Prompt(List.of( systemMessage, userMessage), chatOptions);
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
