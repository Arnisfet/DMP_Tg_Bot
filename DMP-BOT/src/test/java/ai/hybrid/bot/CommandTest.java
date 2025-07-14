package ai.hybrid.bot;

import ai.hybrid.bot.config.SshConfig;
import ai.hybrid.bot.data.YarnApp;
import ai.hybrid.bot.data.YarnAppListDTO;
import ai.hybrid.bot.service.dispatcher.DeleteCheckpointCommand;
import ai.hybrid.bot.service.dispatcher.RestartCommand;
import ai.hybrid.bot.service.dispatcher.StartCommand;
import ai.hybrid.bot.service.dispatcher.StopCommand;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev")
public class CommandTest {
    @Autowired
    SshConfig config;
    @Autowired
    StopCommand stopCommand;
    @Autowired
    StartCommand startCommand;
    @Autowired
    RestartCommand restartCommand;
    @Autowired
    DeleteCheckpointCommand deleteCheckpointCommand;
    private String BASE_PATH = "/tmp/checkpoint/";

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    public void startApp_thenStopTest() throws InterruptedException {
        SshConfig.ClusterConfig cluster = config.getClusters().get("RU");
        startCommand.launch(cluster, "LoopLauncher");

        String host = cluster.getHost();
        String query = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(host)
                .port(8088)
                .path("/ws/v1/cluster/apps")
                .queryParam("states", "RUNNING,ACCEPTED")
                .build().toUriString();
        Thread.sleep(20000);

        ResponseEntity<YarnAppListDTO> response = restTemplate.getForEntity(query, YarnAppListDTO.class);

        List<YarnApp> yarnAppList = Optional.ofNullable(response.getBody())
                .map(YarnAppListDTO::getApps)
                .map(YarnAppListDTO.AppContainer::getApp)
                .orElse(List.of());

        Optional<YarnApp> acceptedApp = yarnAppList.stream()
                .filter(app -> (app.getName().contains("LoopLauncher") && app.getState().equals("RUNNING"))
                        || app.getState().equals("ACCEPTED"))
                .findFirst();
        Assert.isTrue(acceptedApp.isPresent(), "App not found");
        stopCommand.launch(cluster, "LoopLauncher");
    }

    @Test
    public void startApp_thenRestartTest() throws InterruptedException {
        SshConfig.ClusterConfig cluster = config.getClusters().get("RU");
        startCommand.launch(cluster, "LoopLauncher");

        String host = cluster.getHost();
        String query = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(host)
                .port(8088)
                .path("/ws/v1/cluster/apps")
                .queryParam("states", "RUNNING,ACCEPTED")
                .build().toUriString();
        Thread.sleep(20000);

        ResponseEntity<YarnAppListDTO> response = restTemplate.getForEntity(query, YarnAppListDTO.class);

        List<YarnApp> yarnAppList = Optional.ofNullable(response.getBody())
                .map(YarnAppListDTO::getApps)
                .map(YarnAppListDTO.AppContainer::getApp)
                .orElse(List.of());

        Optional<YarnApp> acceptedApp = yarnAppList.stream()
                .filter(app -> (app.getName().contains("LoopLauncher") && app.getState().equals("RUNNING"))
                        || app.getState().equals("ACCEPTED"))
                .findFirst();
        Assert.isTrue(acceptedApp.isPresent(), "App not found");
        restartCommand.launch(cluster, "LoopLauncher");
        Thread.sleep(20000);

        yarnAppList = Optional.ofNullable(response.getBody())
                .map(YarnAppListDTO::getApps)
                .map(YarnAppListDTO.AppContainer::getApp)
                .orElse(List.of());

        acceptedApp = yarnAppList.stream()
                .filter(app -> (app.getName().contains("LoopLauncher") && app.getState().equals("RUNNING"))
                        || app.getState().equals("ACCEPTED"))
                .findFirst();
        Assert.isTrue(acceptedApp.isPresent(), "App not found");
        response = restTemplate.getForEntity(query, YarnAppListDTO.class);
        stopCommand.launch(cluster, "LoopLauncher");
    }

    @Test
    public void deleteCheckpointCommandTest() {
        SshConfig.ClusterConfig cluster = config.getClusters().get("RU");
        try {
            JSch jSch = new JSch();
            if (cluster.getPassphrase() != null && !cluster.getPassphrase().isBlank())
                jSch.addIdentity(cluster.getPrivateKey(), cluster.getPassphrase());
            else
                jSch.addIdentity(cluster.getPrivateKey());
            Session session = jSch.getSession(cluster.getUser(), cluster.getHost(), 45292);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            String command = "hdfs dfs -mkdir " + BASE_PATH + "test";
            channelExec.setCommand(command);
            channelExec.connect();

            channelExec.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    }
}
