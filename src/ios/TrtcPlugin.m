//
//  TrtcPlugin.m
//
//  Created by 布丁丸子酱 on 2018/12/26.
//

#import "TrtcPlugin.h"
#import "TCLiveRequestManager.h"
#import <ILiveSDK/ILiveSDK.h>
#import "TCLiveJoinRoomViewController.h"
#import <ILiveSDK/ILiveLoginManager.h>
#import "UIToastView.h"
#import <ILiveLogReport/ILiveLogReport.h>

@interface TrtcPlugin()
{}
@end

@implementation TrtcPlugin

- (void) showCreatePage:(CDVInvokedUrlCommand*)command {
    NSLog(@"showCreatePage");
    [[TCLiveRequestManager getInstance] requestLoginInfo:^(int code) {
        if (code == 0) {
            dispatch_async(dispatch_get_main_queue(), ^{
                int retCode = [[ILiveSDK getInstance] initSdk:[TCLiveRequestManager getInstance].sdkAppID accountType:[TCLiveRequestManager getInstance].accountType];
                NSLog(@"initSdk success %d",retCode);
                if (retCode == 0) {
                    NSLog(@"userId & sig:");
                    NSLog(@"%@", [TCLiveRequestManager getInstance].userID);
                    NSLog(@"%@", [TCLiveRequestManager getInstance].userSig);
                    [[ILiveLoginManager getInstance] iLiveLogin:[TCLiveRequestManager getInstance].userID sig:[TCLiveRequestManager getInstance].userSig succ:^{
                        NSLog(@"-----> login  succ");
                        [[UIToastView getInstance] showToastWithMessage:@"登录成功" toastMode:UIToastShowMode_Succ];
                    } failed:^(NSString *module, int errId, NSString *errMsg) {
                        NSLog(@"-----> login  fail,%@ %d %@",module, errId, errMsg);
                        [[UIToastView getInstance] showToastWithMessage:@"登录失败" toastMode:UIToastShowMode_fail];
                    }];
                }
            });
        }
    }];
    TCLiveJoinRoomViewController *vc = [TCLiveJoinRoomViewController new];
//    vc.defaultRoomId = self.defaultRoomId;
    UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:vc];
    [self.viewController presentViewController:nav animated:YES completion:nil];
}

@end
