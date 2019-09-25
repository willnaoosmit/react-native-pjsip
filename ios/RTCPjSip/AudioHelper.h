//
//  AudioHelper.h
//  RTCPjSip
//
//  Created by Moises Ynfante on 9/25/19.
//  Copyright © 2019 Vadim Ruban. All rights reserved.
//

#ifndef AudioHelper_h
#define AudioHelper_h
#import <AVFoundation/AVAudioSession.h>
#import <Foundation/Foundation.h>

@interface AudioHelper : NSObject

+ (NSArray *)bluetoothRoutes;
+ (AVAudioSessionPortDescription *)bluetoothAudioDevice;
+ (AVAudioSessionPortDescription *)builtinAudioDevice;
+ (AVAudioSessionPortDescription *)speakerAudioDevice;
+ (AVAudioSessionPortDescription *)audioDeviceFromTypes:(NSArray *)types;
@end

#endif /* AudioHelper_h */
