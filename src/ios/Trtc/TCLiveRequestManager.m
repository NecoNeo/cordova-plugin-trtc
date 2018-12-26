//
//  TCLiveRequestManager.m
//  TRTC
//
//  Created by Tencent on 2018/5/31.
//  Copyright © 2018年 Tencent. All rights reserved.
//

#import "TCLiveRequestManager.h"
#import "UIToastView.h"
#import "TCLiveConfigDefine.h"
@implementation TCLiveRequestManager
+ (TCLiveRequestManager *)getInstance{
    static TCLiveRequestManager *singleTon = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        singleTon = [TCLiveRequestManager new];
    });
    return singleTon;
}

- (void)requestLoginInfo:(LiveLoginInfoBlock)block{
    NSString *user = [[NSUserDefaults standardUserDefaults] objectForKey:@"TCLIVE_USER"];
    if (user.length == 0) {
        user = @"";
    }
    NSDictionary *params = @{};
    NSMutableString *fullLoginInfoUrl = [[NSMutableString alloc] init];
    [fullLoginInfoUrl appendString:Login_Info_Url];
//    [fullLoginInfoUrl appendString:@"?openId=osiPSspRgjspCsZL18v_g0g2FMyk"];
    NSMutableURLRequest *request = [self getSendPostRequest:fullLoginInfoUrl body:params];
    
    NSURLSessionConfiguration *sessionConfig = [NSURLSessionConfiguration defaultSessionConfiguration];
    [sessionConfig setTimeoutIntervalForRequest:30];
    
    __weak TCLiveRequestManager *weakself = self;
    NSURLSession *session = [NSURLSession sessionWithConfiguration:sessionConfig];
    NSURLSessionDataTask *task = [session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        if (error || data == nil) {
            block(-1);
            [[UIToastView getInstance] showToastWithMessage:@"登录请求失败" toastMode:UIToastShowMode_fail];
        }
        else{
            //无error data解不出
            
//            NSDictionary *info = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
//            if (info) {
//                weakself.sdkAppID = [info[@"sdkAppID"] intValue];
//                weakself.accountType = [info[@"accountType"]intValue];
//                weakself.userID = info[@"userID"];
//                weakself.userSig = info[@"userSig"];
//                [[NSUserDefaults standardUserDefaults] setObject:info[@"userID"] forKey:@"TCLIVE_USER"];
//                block(0);
//            }
//
            NSLog(@"get login data");

            NSDictionary *res = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
            NSLog(@"res:%@", res);
            if (res && res[@"obj"]) {
                NSDictionary *info = res[@"obj"];
                NSLog(@"obj:%@", info);
                NSLog(@"info");
                weakself.sdkAppID = [info[@"sdkAppId"] intValue];
                weakself.accountType = [info[@"accountType"]intValue];
                weakself.userID = info[@"userId"];
                weakself.userSig = info[@"userSig"];
                [[NSUserDefaults standardUserDefaults] setObject:info[@"userID"] forKey:@"TCLIVE_USER"];
                block(0);
            } else {
                block(-1);
                [[UIToastView getInstance] showToastWithMessage:@"登录信息解包失败" toastMode:UIToastShowMode_fail];
            }
        }
    }];
    [task resume];
}

-(void)reqGetAuthBufferInfoWithParams:(NSDictionary *)params block:(LiveAuthBufferBlock)block{
    NSMutableURLRequest *request = [self getSendPostRequest:AuthBuffer_Info_Url body:params];
    
    NSURLSessionConfiguration *sessionConfig = [NSURLSessionConfiguration defaultSessionConfiguration];
    [sessionConfig setTimeoutIntervalForRequest:30];

    NSURLSession *session = [NSURLSession sessionWithConfiguration:sessionConfig];
    NSURLSessionDataTask *task = [session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        if (error|| data == nil) {
            [[UIToastView getInstance] showToastWithMessage:@"获取authBuffer请求失败" toastMode:UIToastShowMode_fail];
        }
        else{
            //无error data解不出
            NSDictionary *info = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
            if (info) {
                block(info);
            }
            else{
                [[UIToastView getInstance] showToastWithMessage:@"获取authBuffer解包失败" toastMode:UIToastShowMode_fail];
            }
        }
    }];
    [task resume];
}

- (NSMutableURLRequest *)getSendPostRequest:(NSString *)url body:(NSDictionary *)body{
    
    NSData *dataBody = [NSJSONSerialization dataWithJSONObject:body options:NSJSONWritingPrettyPrinted error:nil];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url] cachePolicy:NSURLRequestReloadIgnoringLocalAndRemoteCacheData timeoutInterval:10];
    [request setValue:[NSString stringWithFormat:@"%ld", (long) [dataBody length]] forHTTPHeaderField:@"Content-Length"];
    [request setHTTPMethod:@"POST"];
    [request setValue:@"application/json; charset=UTF-8" forHTTPHeaderField:@"Content-Type"];
    [request setValue:@"gzip" forHTTPHeaderField:@"Accept-Encoding"];
    [request setHTTPBody:dataBody];

    return request;
}
@end
