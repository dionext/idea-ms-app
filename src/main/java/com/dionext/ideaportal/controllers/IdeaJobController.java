package com.dionext.ideaportal.controllers;


import com.dionext.ideaportal.services.IdeaJobService;
import com.dionext.ideaportal.services.IdeaportalPageCreatorService;
import com.dionext.job.JobManager;
import com.dionext.job.JobRunner;
import com.dionext.job.JobView;
import com.dionext.job.entity.JobInstance;
import com.dionext.site.controllers.BaseSiteController;
import com.dionext.site.services.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@Slf4j
public class IdeaJobController extends BaseSiteController {

    @Autowired
    private IdeaJobService jobService;
    @Autowired
    private JobManager jobManager;
    @Autowired
    private AdminService adminService;


    @Autowired
    IdeaportalPageCreatorService ideaportalPageCreatorService;

    @GetMapping(value = {"/admin/generateOfflinePages"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> generateOfflinePages() {
        int filesCount = ideaportalPageCreatorService.generateOfflinePages(true);
        return sendOk("OK. Generated " + filesCount + " files");
    }
    @GetMapping("/admin/job/index")
    public ResponseEntity<String> jobIndex() {
        return sendOk(adminService.createHtmlAll(JobView.makeJobList(jobManager)));
    }

    @PostMapping("/admin/job/run")
    public ResponseEntity<String> jobRun(@RequestParam(value = JobManager.JOB_TYPE_ID) String jobTypeId,
                                         @RequestParam(value = JobManager.JOB_ID, required = false) String jobId,
                                         @RequestParam Map<String, String> requestParams
        ) throws Exception {
        //start job
        JobInstance jobInstance = jobManager.preRegisterJob(jobTypeId, jobId, requestParams);
        JobRunner jobRunner = jobService.createJob(jobInstance);
        jobManager.executeJob(jobInstance, jobRunner);
        //show running job fragment
        return sendFragment(JobView.makeJobBlock(jobManager, jobService, jobTypeId, jobInstance));
    }

    @GetMapping("/admin/job/info")
    public ResponseEntity<String> jobInfo(@RequestParam(required = false, value = JobManager.JOB_TYPE_ID) String jobTypeId,
                                          @RequestParam(required = false, value = JobManager.JOB_ID) String jobId) {
        return sendOk(adminService.createHtmlAll(JobView.makeJobInfoBlock(jobManager, jobService, jobTypeId, jobId)));
    }

    @GetMapping("/admin/job/completed")
    public ResponseEntity<String> job(@RequestParam(value = JobManager.JOB_ID) String jobId) throws Exception {
        JobInstance jobInstance = jobManager.getJobInstance(jobId);
        return sendFragment(JobView.makeJobBlock(jobManager, jobService, jobInstance.getJobTypeId(), jobInstance));
    }

    @PostMapping("/admin/job/cancel")
    public ResponseEntity<String> jobCancel(@RequestParam(value = JobManager.JOB_ID) String jobId) throws Exception {
        JobInstance jobInstance = jobManager.getJobInstance(jobId);
        jobManager.cancelJob(jobId);
        return sendFragment(JobView.makeCanceledJobResult(jobInstance));
    }

    @GetMapping("/admin/job/progress")
    public ResponseEntity<String> jobProgress(@RequestParam(value = JobManager.JOB_ID) String jobId) throws Exception {
        JobInstance jobInstance = jobManager.getJobInstance(jobId);
        return sendFragment(JobView.makeProgressJobBlock(jobInstance), JobView.makeJobProgressHeaders(jobInstance));
    }
}
