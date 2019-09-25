//
//  AudioHelper.m
//  RTCPjSip
//
//  Created by Moises Ynfante on 9/25/19.
//  Copyright © 2019 Vadim Ruban. All rights reserved.
//

#import "AudioHelper.h"
@implementation AudioHelper

+ (NSArray *)bluetoothRoutes {
    return @[AVAudioSessionPortBluetoothHFP, AVAudioSessionPortCarAudio, AVAudioSessionPortBluetoothA2DP, AVAudioSessionPortBluetoothLE ];
}

+ (AVAudioSessionPortDescription *)bluetoothAudioDevice {
    return [AudioHelper audioDeviceFromTypes:[AudioHelper bluetoothRoutes]];
}

+ (AVAudioSessionPortDescription *)builtinAudioDevice {
    NSArray *builtinRoutes = @[ AVAudioSessionPortBuiltInMic ];
    return [AudioHelper audioDeviceFromTypes:builtinRoutes];
}

+ (AVAudioSessionPortDescription *)speakerAudioDevice {
    NSArray *builtinRoutes = @[ AVAudioSessionPortBuiltInSpeaker ];
    return [AudioHelper audioDeviceFromTypes:builtinRoutes];
}

+ (AVAudioSessionPortDescription *)audioDeviceFromTypes:(NSArray *)types {
    NSArray *routes = [[AVAudioSession sharedInstance] availableInputs];
    for (AVAudioSessionPortDescription *route in routes) {
        if ([types containsObject:route.portType]) {
            return route;
        }
    }
    return nil;
}

@end
