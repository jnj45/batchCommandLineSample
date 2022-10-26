/*
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package egovframework.example.bat.commandline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.egovframe.rte.bat.core.launch.support.EgovCommandLineRunner;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.launch.support.JvmSystemExiter;
import org.springframework.util.StringUtils;

/**
 * @author 배치실행개발팀
 * @since 2012. 07.25
 * @version 1.0
 * @see
 *  <pre>
 *      개정이력(Modification Information)
 *
 *  수정일               수정자                 수정내용
 *  ----------   -----------   ---------------------------
 *  2012.07.25   배치실행개발팀       최초 생성
 *  2021.11.18   신용호                 배치실행환경 v4.0 적용
 *  </pre>
 */

public class EgovCommandLineJobRunner {

	protected static final Log logger = LogFactory.getLog(EgovCommandLineJobRunner.class);

	/**
	 * Launch a batch job using a {@link EgovCommandLineRunner}. Creates a new
	 * Spring context for the job execution, and uses a common parent for all
	 * such contexts. No exception are thrown from this method, rather
	 * exceptions are logged and an integer returned through the exit status in
	 * a {@link JvmSystemExiter} (which can be overridden by defining one in the
	 * Spring context).<br/>
	 * Parameters can be provided in the form key=value, and will be converted
	 * using the injected {@link JobParametersConverter}.
	 *
	 * @param args <p>
	 * <ul>
	 * <li>-restart: (optional) if the job has failed or stopped and the most
	 * should be restarted. If specified then the jobIdentifier parameter can be
	 * interpreted either as the name of the job or the id of teh job execution
	 * that failed.</li>
	 * <li>-next: (optional) if the job has a {@link JobParametersIncrementer}
	 * that can be used to launch the next in a sequence</li>
	 * <li>jobPath: the xml application context containing a {@link Job}
	 * <li>jobIdentifier: the bean id of the job or id of the failed execution
	 * in the case of a restart.
	 * <li>jobParameters: 0 to many parameters that will be used to launch a
	 * job.
	 * </ul>
	 * The options (<code>-restart, -next</code>) can occur anywhere in the
	 * command line.
	 * </p>
	 */
	public static void main(String[] args) throws Exception {
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
						logger.debug("Stdin arg: " + line);
						newargs.add(line);
					}
					line = reader.readLine();
				}
			}
		} catch (IOException e) {
			logger.warn("Could not access stdin (maybe a platform limitation)");
			if (logger.isDebugEnabled()) {
				logger.debug("Exception details", e);
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
			logger.error(message);
			command.setMessage(message);
			command.exit(1);
		}

		String[] parameters = params.toArray(new String[params.size()]);

		int result = command.start(jobPath, jobIdentifier, parameters, opts);
		command.exit(result);
	}
}
