# NaStation

NaStation is the PC wallet client of NA Chain, which is built by Vaddin + Spring boot + Maven.

## Environment

- Nodejs 12+
- JDK 1.8
- Maven 3
- IntelliJ IDEA

## Build

```xml
Switch the artifactId of the jxbrowser in pom.xml 
When you need GUI pages in different environments

<!--jxbrowser-mac-->
<!--jxbrowser-win64-->
<!--jxbrowser-linux64-->
<dependency>
    <groupId>com.teamdev.jxbrowser</groupId>
    <artifactId>jxbrowser-linux64</artifactId>
    <version>7.12.2</version>
</dependency>
```

```java
clean install -Pproduction
```

## IDE Run

```java
# Change to devOnline: [spring.profiles.active: devOnline]
 org.nastation.Application
```

## Home page

```text
http://localhost:20902/
```


## API doc

```text
http://localhost:20902/swagger-ui.html
```

## Server deploy

```shell
# check your jdk version (jdk8+)
java -version

# run nastation by NO-GUI mode
nohup java -Xms256m -Xmx4096m -jar nastation-<version>.jar nogui &

# run nastation by NO-GUI mode and change the server port(--server.port) and the server domain(--server.address)
# check your local ip by command in ubuntu server: ip addr show
nohup java -Xms256m -Xmx4096m -jar nastation-<version>.jar nogui --server.address=<ip> --server.port=18080 &

# check api response by ping test
http://localhost:20902/station/api/ping

# import your wallet
http://localhost:20902/station/api/account/import?walletName=test-wallet-1&mnemonicText=<your_mnemonic>&salt=&password=<your_password>

# send nac token from N001 to N002
http://localhost:20902/station/api/account/send?fromAddress=N001&toAddress=N002&password=<your_password>&value=0.1&instanceId=1&token=1

# query data sync process info list of different instance
http://localhost:20902/station/api/getDataSyncProcessInfoList

# set stop data sync manually
http://localhost:20902/station/api/stopDataSync?flag=true

# more api detail,please check the swagger url:
http://localhost:20902/swagger-ui.html

# how to stop nastation app? first set stop data sync manually, then get the pid of nastation and stop nastation in an elegant way
ps -ef |grep java
kill -2 <PID>

```



