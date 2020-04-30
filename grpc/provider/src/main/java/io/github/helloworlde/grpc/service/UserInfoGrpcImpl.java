package io.github.helloworlde.grpc.service;

import io.github.helloworlde.grpc.proto.UserInfoRequest;
import io.github.helloworlde.grpc.proto.UserInfoResponse;
import io.github.helloworlde.grpc.proto.UserInfoServiceGrpc;
import io.grpc.stub.StreamObserver;
import io.mobike.grpc.server.GrpcService;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@GrpcService(UserInfoServiceGrpc.class)
@Slf4j
public class UserInfoGrpcImpl extends UserInfoServiceGrpc.UserInfoServiceImplBase {
    private final Random random = new Random();

    private static final AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    public void getUserInfo(UserInfoRequest request, StreamObserver<UserInfoResponse> responseObserver) {
        log.info("收到客户端请求:" + request.getName());
        UserInfoResponse response = UserInfoResponse.newBuilder()
                                                    .setId(atomicInteger.getAndIncrement())
                                                    .setAge(random.nextInt(100))
                                                    .setName("Hello " + request.getName())
                                                    .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
        log.info("请求处理结束");
    }
}
