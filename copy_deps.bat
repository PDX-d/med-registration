@echo off
setlocal enabledelayedexpansion

echo 正在复制依赖文件...

set MAVEN_REPO=C:\Users\steam\.m2\repository
set TARGET_LIB=lib

if not exist "%TARGET_LIB%" mkdir "%TARGET_LIB%"

echo 复制Spring Boot相关依赖...
copy "%MAVEN_REPO%\org\springframework\boot\spring-boot\2.3.12.RELEASE\spring-boot-2.3.12.RELEASE.jar" "%TARGET_LIB%\" >nul 2>&1
copy "%MAVEN_REPO%\org\springframework\boot\spring-boot-autoconfigure\2.3.12.RELEASE\spring-boot-autoconfigure-2.3.12.RELEASE.jar" "%TARGET_LIB%\" >nul 2>&1
copy "%MAVEN_REPO%\org\springframework\boot\spring-boot-starter-web\2.3.12.RELEASE\spring-boot-starter-web-2.3.12.RELEASE.jar" "%TARGET_LIB%\" >nul 2>&1
copy "%MAVEN_REPO%\org\springframework\boot\spring-boot-starter\2.3.12.RELEASE\spring-boot-starter-2.3.12.RELEASE.jar" "%TARGET_LIB%\" >nul 2>&1
copy "%MAVEN_REPO%\org\springframework\boot\spring-boot-starter-json\2.3.12.RELEASE\spring-boot-starter-json-2.3.12.RELEASE.jar" "%TARGET_LIB%\" >nul 2>&1
copy "%MAVEN_REPO%\org\springframework\boot\spring-boot-starter-tomcat\2.3.12.RELEASE\spring-boot-starter-tomcat-2.3.12.RELEASE.jar" "%TARGET_LIB%\" >nul 2>&1

echo 复制Spring框架依赖...
copy "%MAVEN_REPO%\org\springframework\spring-context\5.2.15.RELEASE\spring-context-5.2.15.RELEASE.jar" "%TARGET_LIB%\" >nul 2>&1
copy "%MAVEN_REPO%\org\springframework\spring-core\5.2.15.RELEASE\spring-core-5.2.15.RELEASE.jar" "%TARGET_LIB%\" >nul 2>&1
copy "%MAVEN_REPO%\org\springframework\spring-beans\5.2.15.RELEASE\spring-beans-5.2.15.RELEASE.jar" "%TARGET_LIB%\" >nul 2>&1
copy "%MAVEN_REPO%\org\springframework\spring-expression\5.2.15.RELEASE\spring-expression-5.2.15.RELEASE.jar" "%TARGET_LIB%\" >nul 2>&1
copy "%MAVEN_REPO%\org\springframework\spring-aop\5.2.15.RELEASE\spring-aop-5.2.15.RELEASE.jar" "%TARGET_LIB%\" >nul 2>&1
copy "%MAVEN_REPO%\org\springframework\spring-web\5.2.15.RELEASE\spring-web-5.2.15.RELEASE.jar" "%TARGET_LIB%\" >nul 2>&1
copy "%MAVEN_REPO%\org\springframework\spring-webmvc\5.2.15.RELEASE\spring-webmvc-5.2.15.RELEASE.jar" "%TARGET_LIB%\" >nul 2>&1

echo 复制其他依赖...
copy "%MAVEN_REPO%\com\baomidou\mybatis-plus-boot-starter\3.4.3\mybatis-plus-boot-starter-3.4.3.jar" "%TARGET_LIB%\" >nul 2>&1
copy "%MAVEN_REPO%\mysql\mysql-connector-java\5.1.47\mysql-connector-java-5.1.47.jar" "%TARGET_LIB%\" >nul 2>&1
copy "%MAVEN_REPO%\cn\hutool\hutool-all\5.7.17\hutool-all-5.7.17.jar" "%TARGET_LIB%\" >nul 2>&1
copy "%MAVEN_REPO%\io\jsonwebtoken\jjwt\0.9.1\jjwt-0.9.1.jar" "%TARGET_LIB%\" >nul 2>&1
copy "%MAVEN_REPO%\org\projectlombok\lombok\1.18.30\lombok-1.18.30.jar" "%TARGET_LIB%\" >nul 2>&1
copy "%MAVEN_REPO%\org\apache\commons\commons-pool2\2.8.1\commons-pool2-2.8.1.jar" "%TARGET_LIB%\" >nul 2>&1
copy "%MAVEN_REPO%\org\redisson\redisson\3.33.0\redisson-3.33.0.jar" "%TARGET_LIB%\" >nul 2>&1

echo 依赖复制完成！
pause
