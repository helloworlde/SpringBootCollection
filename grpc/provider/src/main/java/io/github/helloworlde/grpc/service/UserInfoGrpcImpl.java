package io.github.helloworlde.grpc.service;

import io.github.helloworlde.grpc.proto.UserInfoRequest;
import io.github.helloworlde.grpc.proto.UserInfoResponse;
import io.github.helloworlde.grpc.proto.UserInfoServiceGrpc;
import io.grpc.stub.StreamObserver;
import io.mobike.grpc.server.GrpcService;

import java.util.Random;

@GrpcService(UserInfoServiceGrpc.class)
public class UserInfoGrpcImpl extends UserInfoServiceGrpc.UserInfoServiceImplBase {
    private final Random random = new Random();


    @Override
    public void getUserInfo(UserInfoRequest request, StreamObserver<UserInfoResponse> responseObserver) {
        System.out.println("收到客户端请求:" + request.getName());
        UserInfoResponse response = UserInfoResponse.newBuilder()
                                                    .setAge(random.nextInt(100))
                                                    .setId(random.nextInt(100))
                                                    .setName("Hello " + request.getName())
                                                    .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
        System.out.println("请求处理结束");
    }
}
