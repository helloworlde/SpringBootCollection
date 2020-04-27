# Spring Boot 使用 gRPC 

Spring Boot 中使用 gRPC 实现跨语言服务调用，以提供者和消费者为例，提供者提供 gRPC 接口实现，供消费者调用，使用 Spring Cloud 框架， Consul作为注册中心，通过 gRPC 调用服务

共三个模块，分别为提供者 `provider`, 消费者 `consumer` 和 protobuf 相关的模块 `grpc-lib`

## Proto 

用于定义接口，提供生成的实现类，用于生产者和消费者引用

- build.gradle

需要注意 protobuf 版本暂只支持到 1.16.1，更高版本 `io.mobike.boot:mofa-boot-starter-grpc`暂不支持

```groovy
plugins {
    id "com.google.protobuf" version "0.8.8"
    id 'java'
}

archivesBaseName = 'grpc-lib'

dependencies {
    implementation 'io.grpc:grpc-netty-shaded:1.16.1'
    implementation 'io.grpc:grpc-protobuf:1.16.1'
    implementation 'io.grpc:grpc-stub:1.16.1'
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:3.6.1" }
    plugins {
        grpc { artifact = "io.grpc:protoc-gen-grpc-java:1.16.1" }
    }
    generateProtoTasks {
        all()*.plugins { grpc {} }
    }
}

sourceSets {
    main {
        java {
            srcDirs 'build/generated/source/proto/main/grpc'
            srcDirs 'build/generated/source/proto/main/java'
        }
    }
}
```

- user-info.proto


```proto
syntax = "proto3";

package io.github.helloworlde.grpc;

option java_multiple_files = true;
option java_package = "io.github.helloworlde.grpc.proto";
option java_outer_classname = "UserInfoProto";

message UserInfoRequest {
  string name = 1;
}
message UserInfoResponse {
  int32 id = 1;
  string name = 2;
  int32 age = 3;
}

service UserInfoService {
  rpc GetUserInfo (UserInfoRequest) returns (UserInfoResponse){

  }
}
```

## 生产者 

- build.gradle

其中 `io.mobike.boot:mofa-boot-starter-grpc`是基于`net.devh:grpc-spring-boot-starter`开发的框架，目前的版本仅支持 SpringBoot 2.1.5.RELEASE 及以下版本，更高版本不支持

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.cloud:spring-cloud-starter-consul-discovery'
    compile 'io.mobike.boot:mofa-boot-starter-grpc:1.0.0.RC3'
    compile project(':grpc:grpc-lib')
}
```

- 接口实现类 `UserInfoGrpcImpl.java`

提供接口的类需要用 `@GrpcService`注解标记

```java
@GrpcService(UserInfoServiceGrpc.class)
@Slf4j
public class UserInfoGrpcImpl extends UserInfoServiceGrpc.UserInfoServiceImplBase {
    private final Random random = new Random();

    @Override
    public void getUserInfo(UserInfoRequest request, StreamObserver<UserInfoResponse> responseObserver) {
        UserInfoResponse response = UserInfoResponse.newBuilder()
                                                    .setAge(random.nextInt(100))
                                                    .setId(random.nextInt(100))
                                                    .setName("Hello " + request.getName())
                                                    .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
```

## 消费者

消费者的依赖和生产者相同

- 调用生产者接口 `CusumerController.java`

gRPC 的提供者要使用 `@GrpcStub`注解标记，name 为服务的名称，用于从注册中心查找该服务
需要注意的是，返回值不能直接使用 protobuf 生产的类，否则会有序列化问题

```java
    @GrpcStub("grpc-provider")
    private UserInfoServiceGrpc.UserInfoServiceBlockingStub stub;

    @GetMapping("/getUserInfo")
    public Object getUserInfo(String name) {
        UserInfoRequest request = UserInfoRequest.newBuilder()
                                                 .setName(name)
                                                 .build();
        UserInfoResponse response = stub.getUserInfo(request);

        UserInfoResponseDTO dto = new UserInfoResponseDTO();
        BeanUtils.copyProperties(response, dto);

        return dto;
    }
```