package karatetest;


import com.intuit.karate.junit5.Karate;
import com.tm_service.com.TmServiceApplication;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

import javax.script.ScriptException;
import java.time.ZoneOffset;
import java.util.TimeZone;

class TmServiceApplicationTests {

	@BeforeAll
	public static void setUp() throws ScriptException {
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
		String[] args = {};
		ConfigurableEnvironment environment = new StandardEnvironment();
		if (System.getenv("profile") != null)
			environment.setActiveProfiles(System.getenv("profile"));
		else environment.setActiveProfiles("devh2", "testing");
		SpringApplication app = new SpringApplication(TmServiceApplication.class);
		app.setEnvironment(environment);
		app.run(args);
	}

	@Karate.Test
	Karate testAll() {
		return Karate.run("src/test/resources/test-features");
	}

}