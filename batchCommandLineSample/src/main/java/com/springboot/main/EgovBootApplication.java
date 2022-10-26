package com.springboot.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.egovframe.rte.bat.core.launch.support.EgovCommandLineRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.util.StringUtils;

/**
 * @author 배치실행개발팀
 * @since 2021. 11.25
 * @version 1.0
 * @see
 *  <pre>
 *      개정이력(Modification Information)
 *
 *  수정일               수정자                 수정내용
 *  ----------   -----------   ---------------------------
 *  2021.11.25   신용호                 최초 생성
 *  
 *  </pre>
 */


public class EgovBootApplication implements CommandLineRunner {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EgovBootApplication.class);

	public static void main(String[] args) {
		//SpringApplication.run(EgovBootApplication.class, args);

		LOGGER.debug("##### EgovSampleBootApplication Start #####");

		SpringApplication springApplication = new SpringApplication(EgovBootApplication.class);
		springApplication.setWebApplicationType(WebApplicationType.NONE);
		springApplication.setHeadless(false);
		springApplication.setBannerMode(Banner.Mode.CONSOLE);
		springApplication.run(args);
		
		LOGGER.debug("##### EgovSampleBootApplication End #####");
	}
	
	@Override
    public void run(String... args) throws Exception {
		
		System.out.println("##### EgovSampleBootApplication Run #####");
		/*
		 * CommandLine상에서 실행하기 위해서는 jobPath와 jobIdentifier을 인수로 받아야 한다.
		 * jobPath: Batch Job 실행에 필요한 context 정보가 기술된 xml
		 * jobIdentifier: 실행할 Batch Job의 이름
		 *
		 * 실행예시) 'java EgovCommandLineRunner jobPath jobIdentifier jobParamter1 ...'
		 * 이클립스 기본 세팅 위치: .settings/EgovCommandLineJobRunner.launch
		 * jobPath: /egovframework/batch/context-commandline.xml
		 * jobIdentifier: mybatisToDelimitedJob(기본 세팅), mybatisToFixedLengthJob, mybatisToMybatisJob, jdbcToJdbcJob
		 */
		EgovCommandLineRunner command = new EgovCommandLineRunner();

		List<String> newargs = new ArrayList<String>(Arrays.asList(args));
		newargs.add("timestamp=" + new Date().getTime());

		try {
			if (System.in.available() > 0) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				String line = " ";
				while (StringUtils.hasLength(line)) {
					if (!line.startsWith("#") && StringUtils.hasText(line)) {
						LOGGER.debug("Stdin arg: " + line);
						newargs.add(line);
					}
					line = reader.readLine();
				}
			}
		} catch (IOException e) {
			LOGGER.warn("Could not access stdin (maybe a platform limitation)");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Exception details", e);
			}
		}

		Set<String> opts = new HashSet<String>();
		List<String> params = new ArrayList<String>();

		int count = 0;
		String jobPath = null;
		String jobIdentifier = null;

		for (String arg : newargs) {
			if (arg.startsWith("-")) {
				opts.add(arg);
			} else {
				switch (count) {
					case 0:
						jobPath = arg;
						break;
					case 1:
						jobIdentifier = arg;
						break;
					default:
						params.add(arg);
						break;
				}
				count++;
			}
		}

		if (jobPath == null || jobIdentifier == null) {
			String message = "At least 2 arguments are required: JobPath and jobIdentifier.";
			LOGGER.error(message);
			command.setMessage(message);
			command.exit(1);
		}

		String[] parameters = params.toArray(new String[params.size()]);

		int result = command.start(jobPath, jobIdentifier, parameters, opts);
		command.exit(result);
		
    }

}
