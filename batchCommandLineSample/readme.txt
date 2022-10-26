Run As > Run Configuration 선택
Arguments 탭에 다음중 하나 입력
/egovframework/batch/context-commandline.xml mybatisToDelimitedJob inputFile=egovframework/batch/data/inputs/csvData.csv
/egovframework/batch/context-commandline.xml mybatisToFixedLengthJob inputFile=egovframework/batch/data/inputs/csvData.csv
/egovframework/batch/context-commandline.xml mybatisToMybatisJob inputFile=egovframework/batch/data/inputs/csvData.csv
/egovframework/batch/context-commandline.xml jdbcToJdbcJob inputFile=egovframework/batch/data/inputs/csvData.csv

테스트

