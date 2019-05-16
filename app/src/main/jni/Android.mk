LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
STDCXX_PATH 	:= /opt/android/android-ndk-r11c/sources/cxx-stl/gnu-libstdc++/4.9
LOCAL_CFLAGS := -D__STDC_CONSTANT_MACROS
TARGET_CPU_ABI	:= 	armeabi
TARGET_CPU_ABI_V7	:= 	armeabi-v7a

LOCAL_ALLOW_UNDEFINED_SYMBOLS := false
LOCAL_CFLAGS += -DGL_GLEXT_PROTOTYPES
LOCAL_CFLAGS += -D__STDC_CONSTANT_MACROS=1

LOCAL_SRC_FILES := libAVAPIs.so libIOTCAPIs.so

LOCAL_CPPFLAGS += -fexceptions -frtti

LOCAL_SRC_FILES := \
		jpeg.c \
		libjpeg/jcapimin.c libjpeg/jcapistd.c libjpeg/jccoefct.c libjpeg/jccolor.c libjpeg/jcdctmgr.c libjpeg/jchuff.c \
		libjpeg/jcinit.c libjpeg/jcmainct.c libjpeg/jcmarker.c libjpeg/jcmaster.c libjpeg/jcomapi.c libjpeg/jcparam.c \
		libjpeg/jcphuff.c libjpeg/jcprepct.c libjpeg/jcsample.c libjpeg/jctrans.c libjpeg/jdapimin.c libjpeg/jdapistd.c \
		libjpeg/jdatadst.c libjpeg/jdatasrc.c libjpeg/jdcoefct.c libjpeg/jdcolor.c libjpeg/jddctmgr.c libjpeg/jdhuff.c \
		libjpeg/jdinput.c libjpeg/jdmainct.c libjpeg/jdmarker.c libjpeg/jdmaster.c libjpeg/jdmerge.c libjpeg/jdphuff.c \
		libjpeg/jdpostct.c libjpeg/jdsample.c libjpeg/jdtrans.c libjpeg/jerror.c libjpeg/jfdctflt.c libjpeg/jfdctfst.c \
		libjpeg/jfdctint.c libjpeg/jidctflt.c libjpeg/jidctfst.c libjpeg/jidctint.c libjpeg/jidctred.c libjpeg/jquant1.c \
		libjpeg/jquant2.c libjpeg/jutils.c libjpeg/jmemmgr.c libjpeg/jmemansi.c \
		\
		miniupnpc/getwlanIP.c miniupnpc/miniupnpc.c miniupnpc/upnpcommands.c miniupnpc/upnpreplyparse.c miniupnpc/upnpdev.c \
		miniupnpc/igd_desc_parse.c miniupnpc/minixml.c miniupnpc/connecthostport.c miniupnpc/miniwget.c miniupnpc/minissdpc.c \
		miniupnpc/minisoap.c miniupnpc/portlistingparse.c miniupnpc/receivedata.c miniupnpc/upnperrors.c \
		\
		linuxthSDK/skt.c \
		linuxthSDK/list.c \
		linuxthSDK/thSDKlib.c \
		linuxthSDK/common.c \
		jniMain.c \

LOCAL_C_INCLUDES := $(LOCAL_PATH)/ffmpeg \
					$(STDCXX_PATH)/include \
					$(STDCXX_PATH)/libs/$(TARGET_CPU_ABI)/include/ \
					$(STDCXX_PATH)/libs/$(TARGET_CPU_ABI_V7)/include/ \

LOCAL_LDLIBS :=   \
			-L$(STDCXX_PATH)/libs/$(TARGET_CPU_ABI) \
			-L$(STDCXX_PATH)/libs/$(TARGET_CPU_ABI_V7) \
			-llog  -lz -lGLESv1_CM -ldl -L$(LOCAL_PATH) \
			-lavformat -lavcodec -lavdevice -lavfilter -lavutil -lswscale -lAVAPIs -lIOTCAPIs -lSmartConnection -Llib

LOCAL_LDLIBS    += -lOpenSLES
LOCAL_LDLIBS    += -landroid

LOCAL_MODULE := libthSDK

include $(BUILD_SHARED_LIBRARY)
