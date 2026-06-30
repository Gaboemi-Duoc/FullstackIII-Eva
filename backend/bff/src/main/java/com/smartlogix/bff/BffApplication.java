package com.smartlogix.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import io.sentry.Sentry;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableFeignClients
public class BffApplication {

	public static void main(String[] args) {
		SpringApplication.run(BffApplication.class, args);
	}

    @PostConstruct
    public void initSentry() {
        Sentry.init(options -> {
            options.setDsn("https://11a8058997bd43bead1bb490357eded6@app.glitchtip.com/25284");
            options.setEnvironment("production");
            options.setRelease("your-app-version@1.0.0");
            options.setTracesSampleRate(0.01);
        });
    }
}
