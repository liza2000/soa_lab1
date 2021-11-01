package ru.itmo.soa.sd;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import lombok.SneakyThrows;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import java.util.Collections;

@Singleton
public class ServiceDiscoveryWorker {
    private Consul client = null;
    private static final String serviceId = "1";

    {
        try {
            client = Consul.builder().build();
            AgentClient agentClient = client.agentClient();
            Registration service = ImmutableRegistration.builder()
                    .id(serviceId)
                    .name("human-being-app")
                    .port(28443)
                    .check(Registration.RegCheck.ttl(30L)) // registers with a TTL of 3 seconds
                    .meta(Collections.singletonMap("app", "soa_lab1-snapshot"))
                    .build();

            agentClient.register(service);
        } catch (Exception e) {
            System.err.println("Consul is unavailable");
        }
    }

    @SneakyThrows
    @Schedule(hour = "*", minute = "*", second = "*/15")
    public void checkIn() {
        AgentClient agentClient = client.agentClient();
        agentClient.pass(serviceId);
    }

}
