package com.dionext.ideaportal.services;

import com.dionext.job.*;
import com.dionext.job.entity.JobInstance;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IdeaJobService extends BaseJobService implements JobService {

    @Autowired
    private IdeaAIService ideaAIService;

    @PostConstruct
    void postConstruct() {
        jobManager.addJobType("executeSimpleJob", "Запуск простого тестового задания");
        jobManager.addJobType("executeSimpleJobWithError", "Запуск простого тестового задания с ошибкой");
        jobManager.addJobType("citeExplanation", "Запрос объяснения цитат");
        jobManager.addJobType("testSingle", "Тестовый одиночный запрос");
        jobManager.addJobType("citeInfoCopy", "Копирование информации из таблицы ИИ запросов в таблицу цитат");
    }


    private Collection<String> makeSimpleList(int totalStepCount){
        Collection<String> list = new ArrayList<>();
        for (int i = 0; i <= totalStepCount; i++) {
            list.add(String.valueOf(i));
        }
        return list;
    }

    public String createRunJobParameters(String jobTypeId, JobInstance jobInstance, boolean readOnly) {
        if (jobTypeId == null && jobInstance != null) jobTypeId = jobInstance.getJobTypeId();

        StringBuilder str = new StringBuilder();
        if ("executeSimpleJob".equals(jobTypeId) || "executeSimpleJobWithError".equals(jobTypeId) ){
            str.append(
                    JobView.createRunJobParameter("countType", "Тип количества позиций",
                            jobInstance != null ? jobInstance.getParameter("countType") : CountType.ONE.name(),
                            readOnly));
        }
        else {
            str.append(ideaAIService.createRunJobParameters(jobTypeId, jobInstance, readOnly));
        }
        return str.toString();
    }


    public JobRunner createJob(JobInstance _jobInstance) throws Exception {
        String jobTypeId = _jobInstance.getJobTypeId();
        if ("executeSimpleJob".equals(jobTypeId)
            || ("executeSimpleJobWithError".equals(jobTypeId)) ) {
            if ("executeSimpleJobWithError".equals(jobTypeId)) {
                _jobInstance.putParameter("withError", "true");
            }

            JobBatchRunner jobBatchRunner = new BaseJobBatchRunner();
            jobBatchRunner.setJobBatchListMaker((jobInstance) -> {
                Collection<?> items = makeSimpleList(100);
                CountType countType = CountType.valueOf(jobInstance.getParameter("countType"));
                if (countType == CountType.ONE)
                    return items.stream().limit(1).collect(Collectors.toList());
                else if (countType == CountType.TEN)
                    return items.stream().limit(10).collect(Collectors.toList());
                else //if (countType == CountType.ALL)
                    return items;

            });
            jobBatchRunner.setJobBatchIdExtractor((_, item) -> (String)item);
            jobBatchRunner.setJobBatchItemProcessor((jobInstance, item) -> {
                //jobInstance.incrementProgress();
                try {
                    Thread.sleep(300);
                    if (Integer.parseInt((String)item) > 50) {
                        if ("true".equals(jobInstance.getParameter("withError"))) {
                            throw new RuntimeException("Artificial exception");
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            return jobBatchRunner;
        } else if ("citeExplanation".equals(jobTypeId) || "citeInfoCopy".equals(jobTypeId)){
            JobBatchRunner jobBatchRunner = ideaAIService.createJob(jobTypeId, _jobInstance);
            return jobBatchRunner;
        } else if ("testSingle".equals(jobTypeId)){
            JobSingleRunner jobSingleRunner = new BaseJobSingleRunner();
            jobSingleRunner.setJobProcessor((jobInstanc) -> {
                //
                for (int i = 0; i <= 100; i++) {
                    try {
                        jobInstanc.incrementProgress();
                        log.info("Single jobTypeId " + _jobInstance.getJobTypeId() +
                                " jobId " + _jobInstance.getJobId());
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return jobSingleRunner;
        } else {
            throw new Exception("Unsupported job Type = " + jobTypeId);
        }
    }

}
