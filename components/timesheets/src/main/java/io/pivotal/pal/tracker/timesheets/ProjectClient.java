package io.pivotal.pal.tracker.timesheets;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProjectClient {

    private final RestOperations restOperations;
    private final String endpoint;
    private final ConcurrentMap<Long,ProjectInfo> cachedProjectInfoMap = new ConcurrentHashMap<>();;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo projectInfo = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        cachedProjectInfoMap.putIfAbsent(projectId, projectInfo);
        return projectInfo;

    }

    public ProjectInfo getProjectFromCache(long projectId){
        logger.info("Getting project with id {} from cache", projectId);
        return cachedProjectInfoMap.getOrDefault(projectId, null);
    }
}
